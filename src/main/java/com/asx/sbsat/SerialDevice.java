package com.asx.sbsat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class SerialDevice implements SerialPortEventListener
{
    private String            model;
    private int               revision;
    private DeviceProtocol    protocol;
    private SmartBattery[]    batteries;
    private BufferedReader    input;
    private OutputStream      output;
    private SerialPort        serialPort;
    private volatile String   portId;
    private long              conStartTimeStamp;
    private long              timeSinceLastRead;
    private boolean           isConnected;
    private volatile boolean  canConnect;
    private String            lineBuffer;
    private ArrayList<String> buffer;
    private boolean           deviceReady;
    private int               timeout       = 5000;
    private int               maxBufferSize = 2000;
    private String            mode;

    public abstract static class DeviceProtocol
    {
        private String model;
        private String revision;
        private int    maxBatteries;

        public DeviceProtocol(String model, String revision, int maxBatteries)
        {
            this.model = model;
            this.revision = revision;
            this.maxBatteries = maxBatteries;
        }

        public abstract void parseSerialData(SerialDevice device, String line);

        public abstract void parseBatteryData(SerialDevice device);

        public FormBatteryOverview getBatteryOverviewForm()
        {
            return FormBatteryOverview.instance();
        }

        public String getModel()
        {
            return model;
        }

        public String getRevision()
        {
            return revision;
        }

        public int getMaxBatteries()
        {
            return maxBatteries;
        }
    }

    public SerialDevice()
    {
        this.model = "Generic";
        this.mode = "continuous";
        this.protocol = new ProtocolAT1R1();
        this.revision = 1;
        this.buffer = new ArrayList<String>();
    }

    public void update()
    {
        long time = System.currentTimeMillis();
        long timeSinceLastRead = this.getTimeSinceLastRead();

        if (isConnected && timeSinceLastRead > 0 && time - timeSinceLastRead > timeout)
        {
            this.close();
            SBSAT.getUserInterface().reset();
        }

        if (this.canConnect())
        {
            this.connect();
            this.setCanConnect(false);
        }
    }

    public String readParameter(String parameter, String line)
    {
        return readParameter(parameter, null, line);
    }

    public String readParameter(String parameter, String nextParameter, String line)
    {
        if (line.startsWith(parameter))
        {
            int idxStart = line.lastIndexOf(parameter);
            int idxEnd = line.length();

            String val = line.substring(idxStart, idxEnd);
            val = val.replace(parameter + " ", "");
            val = val.trim();

            if (nextParameter != null && !nextParameter.isEmpty())
            {
                send(nextParameter); // Request the next parameter
            }

            return val;
        }

        return null;
    }

    /**
     * The standard SBSAT device protocol that all analyzers must implement.
     */
    public void parseDeviceParameters(String line)
    {
        if (line.contains("Listening"))
        {
            SBSAT.setInfoText("Reading Parameters...");
            send("model"); // Request the first parameter
        }

        String model = readParameter("Model:", "rev", line);

        if (model != null)
            this.setModel(model);

        String rev = readParameter("Rev:", "mode " + mode, line);

        if (rev != null)
        {
            this.setRevision(Integer.parseInt(rev));
            SBSAT.setInfoText(String.format("%s R%s", this.getModel().toUpperCase(), this.getRevision()));
        }

        String mode = readParameter("Mode:", line);

        if (mode != null && (mode.equalsIgnoreCase("continuous") || mode.equalsIgnoreCase("simulate")))
            deviceReady = true;
    }

    public void onLineRead(String line)
    {
        if (!deviceReady)
        {
            this.parseDeviceParameters(line);
        }
        else if (this.protocol != null)
        {
            this.protocol.parseSerialData(this, line);
        }
    }

    public void connect()
    {
        CommPortIdentifier port = null;

        try
        {
            System.out.println("Connecting to " + this.getPortId());
            SBSAT.setInfoText("Connecting to " + this.getPortId() + "...");
            port = CommPortIdentifier.getPortIdentifier(this.getPortId());
        }
        catch (NoSuchPortException e1)
        {
            e1.printStackTrace();
        }

        if (port == null)
        {
            System.out.println("Could not find COM port.");
            SBSAT.getUserInterface().reset();
            this.isConnected = false;
            return;
        }

        try
        {
            serialPort = (SerialPort) port.open(this.getClass().getName(), timeout);
            serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();

            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            SBSAT.setInfoText("Connected to " + this.getPortId());
            this.isConnected = true;
            System.out.println("Connected to serial line on " + portId);
            conStartTimeStamp = System.currentTimeMillis();
            send("reset");
        }
        catch (Exception e)
        {
            System.err.println(e.toString());
            this.isConnected = false;
            SBSAT.instance().terminate();
        }
    }

    public void heartbeat()
    {
        this.timeSinceLastRead = System.currentTimeMillis();
    }

    @Override
    public synchronized void serialEvent(SerialPortEvent oEvent)
    {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            try
            {
                while (input.ready())
                {
                    char c = (char) input.read();
                    this.lineBuffer = this.lineBuffer + c;
                    System.out.print(c);

                    if (c == '\n')
                    {
                        String line = this.lineBuffer.trim();

                        this.onLineRead(line);
                        this.buffer.add(line);
                        this.lineBuffer = "";
                    }
                }

                if (this.buffer.size() > maxBufferSize)
                {
                    this.buffer.clear();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public synchronized void close()
    {
        if (serialPort != null)
        {
            serialPort.removeEventListener();
            serialPort.close();

            try
            {
                input.close();
                output.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        this.timeSinceLastRead = 0;
        this.isConnected = false;
        this.portId = "";
        this.deviceReady = false;
    }

    public void send(String data)
    {
        try
        {
            output.write(data.getBytes());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public String getPortId()
    {
        return portId;
    }

    public long getTimeSinceLastRead()
    {
        return timeSinceLastRead;
    }

    public synchronized void setPortId(String portId)
    {
        this.portId = portId;
    }

    public long getConStartTimeStamp()
    {
        return conStartTimeStamp;
    }

    public void setConStartTimeStamp(long conStartTimeStamp)
    {
        this.conStartTimeStamp = conStartTimeStamp;
    }

    public SerialPort getSerialPort()
    {
        return serialPort;
    }

    public boolean isConnected()
    {
        return isConnected;
    }

    public ArrayList<String> getBuffer()
    {
        return buffer;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public void setRevision(int revision)
    {
        this.revision = revision;
    }

    public String getModel()
    {
        return model;
    }

    public int getRevision()
    {
        return revision;
    }

    public SmartBattery[] getSmartBatteries()
    {
        return batteries;
    }

    public boolean canConnect()
    {
        return canConnect;
    }

    public synchronized void setCanConnect(boolean v)
    {
        this.canConnect = v;
    }

    public DeviceProtocol getProtocol()
    {
        return protocol;
    }

    public SmartBattery parseSmartBattery(int index, ArrayList<String> batteryData)
    {
        if (this.batteries == null)
        {
            this.batteries = new SmartBattery[this.getProtocol().getMaxBatteries()];
        }

        SmartBattery battery = SmartBattery.parse(this.batteries[index], batteryData);

        if (battery != null)
        {
            if (battery.getFormData() == null)
            {
                battery.setFormData(new BatteryFormData(this.getProtocol().getBatteryOverviewForm(), battery));
            }

            battery.getFormData().updateValues();
            System.out.println(String.format("Received data from battery %s (%s)", index, battery.getSerial()));

            this.heartbeat();
        }

        return this.batteries[index] = battery;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public boolean isDeviceReady()
    {
        return deviceReady;
    }
}