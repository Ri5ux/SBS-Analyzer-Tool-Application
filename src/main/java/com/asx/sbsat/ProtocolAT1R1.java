package com.asx.sbsat;

import java.util.ArrayList;

import com.asx.sbsat.SerialDevice.DeviceProtocol;

public class ProtocolAT1R1 extends DeviceProtocol
{
    public ProtocolAT1R1()
    {
        super("AT1", "R1", 1);
    }

    @Override
    public void parseSerialData(SerialDevice device, String line)
    {
        if (line.contains("BD================================"))
        {
            this.parseBatteryData(device);
        }
    }

    @Override
    public void parseBatteryData(SerialDevice device)
    {
        ArrayList<String> buffer = device.getBuffer();

        int idxStart = buffer.lastIndexOf("BD================================");
        int idxEnd = buffer.size() - 1;

        if (idxStart > 0)
        {
            ArrayList<String> batteryData = new ArrayList<String>();

            for (int idxs = idxStart; idxs <= idxEnd; idxs++)
            {
                batteryData.add(buffer.get(idxs));
            }

            device.parseSmartBattery(0, batteryData);
        }
    }
}
