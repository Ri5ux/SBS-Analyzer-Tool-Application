package com.asx.sbsat;

import java.util.ArrayList;

public class AnalyzerDevice
{
    private String       model;
    private int          revision;
    private SmartBattery sbObject = null;

    public AnalyzerDevice()
    {
        this.model = "Generic";
        this.revision = 1;
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

    public SmartBattery getSmartBattery()
    {
        return sbObject;
    }

    public void handleLineData(String line)
    {
        if (line.equals("================================"))
        {
            this.parseBatteryData();
        }
    }

    public void parseBatteryData()
    {
        // FormBatteryOverview.instance().updateConsole(buffer.toString());
        ArrayList<String> buffer = SBSAT.instance().getBuffer();

        int idxStart = buffer.lastIndexOf("================================");
        int idxEnd = buffer.size() - 1;

        if (idxStart > 0)
        {
            ArrayList<String> batteryData = new ArrayList<String>();

            for (int idxs = idxStart; idxs <= idxEnd; idxs++)
            {
                batteryData.add(buffer.get(idxs));
            }

            SmartBattery battery = SmartBattery.parse(batteryData);

            if (battery != null)
            {
                FormBatteryOverview.instance().updateTextValues(battery);
                sbObject = battery;
                System.out.println("Received data from battery(" + sbObject.getSerial() + ")");

                SBSAT.instance().heartbeat();
            }
        }
    }
}