package com.arisux.sbsat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import org.asx.glx.gui.forms.GuiForm;
import org.asx.glx.opengl.ResourceLocation;
import org.asx.glx.opengl.Sprite;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class SBSATMain implements SerialPortEventListener
{
    private static final File    LWJGL_NATIVES = new File("lib/natives");
    public static final File     RESOURCES     = new File("src/main/resources");

    private static UserInterface ui;
    private static boolean       appRunning    = true;
    private static Thread        uiThread;

    private SerialPort           serialPort;
    private volatile String      portId        = null;
    private long                 conStartTimeStamp;
    private long                 timeSinceLastRead;
    private BufferedReader       input;
    private OutputStream         output;
    private static final int     TIME_OUT      = 500;
    private static final int     DATA_RATE     = 115200;
    private volatile boolean     canStart;
    private String               lineBuffer;
    private ArrayList<String>    buffer        = new ArrayList<String>();
    private boolean              continuousModeEnabled;
    private boolean              isConnected;
    private AnalyzerDevice       connectedDevice;

    private static SBSATMain     instance;

    public static SBSATMain instance()
    {
        return instance;
    }

    public static class Properties
    {
        public static final String NAME    = "SBSAT - Smart Battery System Analyzer Tool";
        public static final String VERSION = "1.0";
    }

    public static class Sprites
    {
        public static final Sprite logoMain           = Sprite.load(new ResourceLocation(RESOURCES, "icon.png"));
        public static final Sprite logoWide           = Sprite.load(new ResourceLocation(RESOURCES, "wide.png"));
        public static final Sprite batteryCharging    = Sprite.load(new ResourceLocation(RESOURCES, "battery_charging.png"));
        public static final Sprite batteryDefective   = Sprite.load(new ResourceLocation(RESOURCES, "battery_defective.png"));
        public static final Sprite batteryEmpty       = Sprite.load(new ResourceLocation(RESOURCES, "battery_empty.png"));
        public static final Sprite batteryUnavailable = Sprite.load(new ResourceLocation(RESOURCES, "battery_unavailable.png"));
        public static final Sprite batteryUnspecified = Sprite.load(new ResourceLocation(RESOURCES, "battery_unspecified.png"));
        public static final Sprite batteryWarning     = Sprite.load(new ResourceLocation(RESOURCES, "battery_warning.png"));
        public static final Sprite cellCharging       = Sprite.load(new ResourceLocation(RESOURCES, "cell_charging.png"));
        public static final Sprite cellDefective      = Sprite.load(new ResourceLocation(RESOURCES, "cell_defective.png"));
        public static final Sprite cellEmpty          = Sprite.load(new ResourceLocation(RESOURCES, "cell_empty.png"));
        public static final Sprite cellUnavailable    = Sprite.load(new ResourceLocation(RESOURCES, "cell_unavailable.png"));
        public static final Sprite cellUnspecified    = Sprite.load(new ResourceLocation(RESOURCES, "cell_unspecified.png"));
        public static final Sprite cellWarning        = Sprite.load(new ResourceLocation(RESOURCES, "cell_warning.png"));
    }

    public long getConStartTimeStamp()
    {
        return conStartTimeStamp;
    }

    public void setConStartTimeStamp(long conStartTimeStamp)
    {
        this.conStartTimeStamp = conStartTimeStamp;
    }

    public static void main(String[] args)
    {
        System.out.println("LWJGL Natives Path: " + LWJGL_NATIVES.getAbsolutePath());
        System.setProperty("org.lwjgl.librarypath", LWJGL_NATIVES.getAbsolutePath());
        System.out.println(Properties.NAME);
        System.out.println("Version " + Properties.VERSION);

        instance = new SBSATMain();

        uiThread = new Thread() {
            @Override
            public void run()
            {
                ui = new UserInterface();
                ui.init();
            }
        };

        uiThread.start();
        appRunning = true;

        while (appRunning)
        {
            SBSATMain.instance().update();

            try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void update()
    {
        long time = System.currentTimeMillis();
        long conStart = this.getConStartTimeStamp();
        long timeSinceLastRead = this.getTimeSinceLastRead();

        if (conStart > 0 && time - conStart > 5000)
        {
            // SBSATMain.instance().setConStartTimeStamp(0);
            // getUserInterface().getPanel().setForm(FormComPortSelection.instance());
        }

        if (timeSinceLastRead > 0 && time - timeSinceLastRead > 5000)
        {
            SBSATMain.instance().serialPort.close();
            this.isConnected = false;
            getUserInterface().reset();
        }

        if (this.canStartSerialCommunication())
        {
            this.startSerialCommunication();
            this.setCanStartSerialConnection(false);
        }
    }

    public synchronized void close()
    {
        if (serialPort != null)
        {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    public void onLineRead(String line)
    {
        try
        {
            if (line.contains("Listening"))
            {
                this.connectedDevice = new AnalyzerDevice();

                if (!continuousModeEnabled)
                {
                    output.write("model".getBytes());
                }

                SBSATMain.setInfoText("Reading Parameters...");
            }

            if (this.connectedDevice != null)
            {
                String parameter = "Model:";

                if (line.contains(parameter))
                {
                    int idxStart = line.lastIndexOf(parameter);
                    int idxEnd = line.length();

                    String val = line.substring(idxStart, idxEnd);
                    val = val.replace(parameter + " ", "");
                    val = val.trim();
                    this.connectedDevice.setModel(val);
                    output.write("rev".getBytes());
                }

                parameter = "Rev:";

                if (line.contains(parameter))
                {
                    int idxStart = line.lastIndexOf(parameter);
                    int idxEnd = line.length();

                    String val = line.substring(idxStart, idxEnd);
                    val = val.replace(parameter + " ", "");
                    val = val.trim();
                    this.connectedDevice.setRevision(Integer.parseInt(val));
                    SBSATMain.setInfoText(String.format("%s R%s", this.connectedDevice.getModel().toUpperCase(), this.connectedDevice.getRevision()));
                    output.write("mode simulate".getBytes());
                }

                parameter = "Mode:";

                if (line.contains(parameter))
                {
                    int idxStart = line.lastIndexOf(parameter);
                    int idxEnd = line.length();

                    String val = line.substring(idxStart, idxEnd);
                    val = val.replace(parameter + " ", "");
                    val = val.trim();

                    if (val.equalsIgnoreCase("continuous"))
                    {
                        continuousModeEnabled = true;
                    }
                }

                this.connectedDevice.handleLineData(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
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

                if (this.buffer.size() > 2000)
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

    public void startSerialCommunication()
    {
        CommPortIdentifier port = null;

        try
        {
            System.out.println("Connecting to " + this.getPortId());
            SBSATMain.setInfoText("Connecting to " + this.getPortId() + "...");
            port = CommPortIdentifier.getPortIdentifier(this.getPortId());
        }
        catch (NoSuchPortException e1)
        {
            e1.printStackTrace();
        }

        if (port == null)
        {
            System.out.println("Could not find COM port.");
            SBSATMain.getUserInterface().reset();
            this.isConnected = false;
            return;
        }

        try
        {
            serialPort = (SerialPort) port.open(this.getClass().getName(), TIME_OUT);
            serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();

            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            SBSATMain.setInfoText("Connected to " + this.getPortId());
            this.isConnected = true;
            System.out.println("Connected to serial line on " + portId);
            conStartTimeStamp = System.currentTimeMillis();
            output.write("reset".getBytes());
        }
        catch (Exception e)
        {
            System.err.println(e.toString());
            this.isConnected = false;
        }
    }

    public static boolean isAppRunning()
    {
        return appRunning;
    }

    public static UserInterface getUserInterface()
    {
        return ui;
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

    public boolean canStartSerialCommunication()
    {
        return canStart;
    }

    public synchronized void setCanStartSerialConnection(boolean v)
    {
        this.canStart = v;
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
    
    public AnalyzerDevice getConnectedDevice()
    {
        return connectedDevice;
    }

    public synchronized static void setInfoText(String text)
    {
        GuiForm activeForm = getUserInterface().getPanel().getActiveForm();

        if (activeForm instanceof FormSBSATBase)
        {
            FormSBSATBase sbsatForm = (FormSBSATBase) activeForm;
            sbsatForm.getInfo().setText(text);
        }
    }

    public synchronized static void setStatusText(String text)
    {
        GuiForm activeForm = getUserInterface().getPanel().getActiveForm();

        if (activeForm instanceof FormSBSATBase)
        {
            FormSBSATBase sbsatForm = (FormSBSATBase) activeForm;
            sbsatForm.getStatus().setText(text);
        }
    }

    public static void terminate()
    {
        instance().close();
        appRunning = false;
    }
}