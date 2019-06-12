package com.arisux.sbsat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Util
{
    public static double map(double x, double in_min, double in_max, double out_min, double out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public static class ComPortEntry
    {
        private String friendlyName;
        private String port;

        public ComPortEntry(String friendlyName, String port)
        {
            this.friendlyName = friendlyName;
            this.port = port;
        }

        public String getFriendlyName()
        {
            return friendlyName;
        }

        public String getPort()
        {
            return port;
        }
    }

    public static ArrayList<ComPortEntry> getListOfComPorts()
    {
        String hive = "HKLM";
        String key = "HARDWARE\\DEVICEMAP\\SERIALCOMM";
        ArrayList<ComPortEntry> comPorts = new ArrayList<ComPortEntry>();
        Process p;

        try
        {
            p = Runtime.getRuntime().exec("reg query " + hive + "\\" + key);
            String result = readProcessOutput(p);
            String[] lines = result.split("\n");

            for (String s : lines)
            {
                if (!s.isEmpty() && !s.contains(key))
                {
                    String[] values = s.split("    ");
                    comPorts.add(new ComPortEntry(values[1], values[3]));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return comPorts;
    }

    public static String readProcessOutput(Process p) throws Exception
    {
        p.waitFor();
        InputStream stream = p.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        String buffer = "";

        while (reader.ready())
        {
            String line = reader.readLine();
            buffer = buffer + line + "\n";
        }

        return buffer;
    }
}