package com.asx.sbsat;

import java.io.File;

import com.asx.sbsat.SBSAT.Properties;

public class LaunchWrapper
{
    private static final File LWJGL_NATIVES = new File("lib/natives");
    private static final File RXTX_NATIVE   = new File("rxtxSerial.dll");

    public static void main(String[] args)
    {
        System.out.println("LWJGL Natives Path: " + LWJGL_NATIVES.getAbsolutePath());
        System.setProperty("org.lwjgl.librarypath", LWJGL_NATIVES.getAbsolutePath());
        System.out.println("RXTX Native Library Path: " + RXTX_NATIVE.getAbsolutePath());
        System.setProperty("java.library.path", String.format("%s;%s", RXTX_NATIVE.getAbsolutePath(), System.getProperty("java.library.path")));
        System.out.println("java.library.path=" + System.getProperty("java.library.path"));

        try
        {
            System.loadLibrary("rxtxSerial");
            System.out.println("Loaded RXTX Library successfully.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("\n" + Properties.NAME);
        System.out.println("Version " + Properties.VERSION);

        new SBSAT();

        while (SBSAT.isAppRunning())
        {
            SBSAT.instance().update();

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
}