package com.asx.sbsat;

import org.asx.glx.gui.forms.GuiForm;
import org.asx.glx.opengl.ResourceLocation;

public class SBSAT
{
    public static final ResourceLocation RESOURCES = new ResourceLocation("sprites");

    private static SBSAT                 instance;
    private static UserInterface         ui;
    private static Thread                uiThread;
    private static boolean               appRunning;
    private String                       javaVersion;
    private SerialDevice                 connectedDevice;

    public SBSAT(String version)
    {
        instance = this;
        javaVersion = version;

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
    }

    public static SBSAT instance()
    {
        return instance;
    }

    public static class Properties
    {
        public static final String NAME    = "SBSAT - Smart Battery System Analyzer Tool";
        public static final String VERSION = "1.0";
    }

    public void update()
    {
        if (this.getConnectedDevice() != null)
        {
            this.getConnectedDevice().update();
        }
    }

    public static UserInterface getUserInterface()
    {
        return ui;
    }

    public SerialDevice getConnectedDevice()
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

    public static boolean isAppRunning()
    {
        return appRunning;
    }

    public synchronized void terminate()
    {
        if (instance().getConnectedDevice() != null)
        {
            instance().getConnectedDevice().close();
        }
        
        appRunning = false;
    }

    public boolean isCompatibleWithJavaVersion()
    {
        return this.javaVersion != null ? this.javaVersion.contains("1.8") : false;
    }

    public synchronized void createDevice(String portId)
    {
        SerialDevice device = new SerialDevice();
        device.setPortId(portId);
        device.setCanConnect(true);

        this.connectedDevice = device;
    }
}