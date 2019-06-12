package com.asx.sbsat;

import static com.asx.sbsat.Colors.COLOR_NEON_GREEN;
import static com.asx.sbsat.Colors.COMPORT_BUTTON_COLOR;
import static com.asx.sbsat.Colors.COMPORT_BUTTON_COLOR_HOVER;
import static com.asx.sbsat.Colors.HEADER_COLOR;
import static com.asx.sbsat.Colors.TRANSPARENT_HIGHLIGHT;
import static com.asx.sbsat.Fonts.FONT_SEGOEUI_PLAIN_14;

import java.util.ArrayList;

import org.asx.glx.gui.GuiPanel;
import org.asx.glx.gui.elements.GuiButton;
import org.asx.glx.gui.elements.GuiElement;
import org.asx.glx.gui.elements.GuiText;
import org.asx.glx.gui.forms.GuiForm;
import org.asx.glx.opengl.Sprite;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import com.asx.sbsat.Util.ComPortEntry;

public class FormComPortSelection extends GuiForm
{
    private static FormComPortSelection INSTANCE;

    public static FormComPortSelection instance()
    {
        return INSTANCE;
    }

    private GuiText textLoading;
    private GuiText textCopyright;
    private GuiText textVersion;

    private int     maxScroll;
    private int     headHeight = 65;
    private int     footHeight = 30;

    public static class GuiButtonComPort extends GuiButton
    {
        private ComPortEntry       com;

        public GuiButtonComPort(GuiForm form, int x, int y, int width, int height, ComPortEntry com)
        {
            super(form, x, y, width, height, new GuiText(form, FONT_SEGOEUI_PLAIN_14, String.format("%s : %s", com.getPort(), com.getFriendlyName())));

            this.com = com;
            this.setColor(COMPORT_BUTTON_COLOR, COMPORT_BUTTON_COLOR_HOVER);
            this.getText().setColor(Color.gray, Color.gray);

            if (com.getFriendlyName().contains("Silab"))
            {
                text.setColor(COLOR_NEON_GREEN, COLOR_NEON_GREEN);
            }

            this.setClickAction(new IAction<GuiElement>() {
                @Override
                public void run(GuiElement o)
                {
                    if (o instanceof GuiButton)
                    {
                        GuiButton button = (GuiButton) o;
                        String[] values = button.getText().getString().split(" : ");
                        String portName = values[0];

                        SBSAT.getUserInterface().setDisplayMode(UserInterface.DISPLAY_MODE_854_480);

                        System.out.println("Selected " + portName);
                        SBSAT.instance().createDevice(portName);
                        SBSAT.getUserInterface().getPanel().setActiveForm(FormBatteryOverview.instance());

                    }
                }
            });
        }

        public ComPortEntry getComPort()
        {
            return com;
        }
    }

    public FormComPortSelection(GuiPanel panel, GuiForm parentForm)
    {
        super(panel, parentForm);

        INSTANCE = this;
        this.textLoading = new GuiText(this, FONT_SEGOEUI_PLAIN_14, "Loading COM Ports...");
        this.textCopyright = new GuiText(this, FONT_SEGOEUI_PLAIN_14, "Copyright (C) 2019 ASX Electronics");
        this.textVersion = new GuiText(this, FONT_SEGOEUI_PLAIN_14, String.format("Version %s", SBSAT.Properties.VERSION));
    }

    @SuppressWarnings("unchecked")
    private void clearComButtonList()
    {
        if (this.getElements().size() > 0)
        {
            for (GuiElement e : (ArrayList<GuiElement>) this.getElements().clone())
            {
                if (e instanceof GuiButtonComPort)
                {
                    this.getElements().remove(e);
                }
            }
        }
    }

    private ArrayList<ComPortEntry> loadedComPorts = new ArrayList<ComPortEntry>();

    public static ComPortEntry getCOMPortInList(ArrayList<ComPortEntry> list, String portName)
    {
        for (ComPortEntry e : list)
        {
            if (e.getPort().equalsIgnoreCase(portName))
            {
                return e;
            }
        }

        return null;
    }

    public static boolean areCOMPortListsIdentical(ArrayList<ComPortEntry> list1, ArrayList<ComPortEntry> list2)
    {
        for (ComPortEntry e1 : list1)
        {
            ComPortEntry e2 = getCOMPortInList(list2, e1.getPort());

            if (e2 == null || e2 != null && !list2.contains(e2))
            {
                return false;
            }
        }

        for (ComPortEntry e2 : list2)
        {
            ComPortEntry e1 = getCOMPortInList(list1, e2.getPort());

            if (e1 == null || e1 != null && !list1.contains(e1))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public void render()
    {
        super.render();

        GL11.glEnd();// Why does this resolve font rendering on initial load?

        // GuiElement.renderColoredRect(0, 0, Display.getWidth(), headHeight, FormSBSATBase.HEADER_COLOR);
        // GuiElement.renderColoredRect(0, headHeight - 1, Display.getWidth(), 1,
        // FormSBSATBase.TRANSPARENT_HIGHLIGHT);

        if (SBSAT.getUserInterface().getTicks() % (60 * 1) == 0)
        {
            if (SBSAT.instance().isCompatibleWithJavaVersion())
            {
                ArrayList<ComPortEntry> newComPortList = Util.getListOfComPorts();

                if (!areCOMPortListsIdentical(loadedComPorts, newComPortList))
                {
                    loadedComPorts = newComPortList;
                    this.clearComButtonList();

                    for (ComPortEntry com : newComPortList)
                    {
                        GuiButtonComPort button = new GuiButtonComPort(this, -1000, -1000, 200, 25, com);
                        this.add(button);
                    }
                }
            }
            else
            {
                this.textLoading.setText("Java 8 Required. Please install and try again.");
            }
        }

        if (this.getElements().size() > 0)
        {
            int i = 0;
            maxScroll = 0;

            for (GuiElement e : this.getElements())
            {
                if (e instanceof GuiButtonComPort)
                {
                    GuiButtonComPort b = (GuiButtonComPort) e;
                    int elementHeight = 26;
                    int elementHeightOffset = i * elementHeight;

                    b.setWidth(Display.getWidth() - (2));
                    b.render(1, headHeight + 1 + elementHeightOffset + scrollOffset);

                    maxScroll = maxScroll + elementHeight;
                    i++;
                }
            }
        }
        else
        {
            textLoading.render(Display.getWidth() / 2 - (textLoading.getWidth() / 2), Display.getHeight() / 2);
        }

        GuiElement.renderColoredRect(0, 0, Display.getWidth(), headHeight, HEADER_COLOR);
        GuiElement.renderColoredRect(0, headHeight - 1, Display.getWidth(), 1, TRANSPARENT_HIGHLIGHT);
        Sprite logo = Sprites.logoWide;
        logo.draw(Display.getWidth() / 2 - (int) (logo.getWidth()) / 2, 0, 1F);
        this.textVersion.render(Display.getWidth() / 2 - this.textVersion.getWidth() / 2, 40);

        GuiElement.renderColoredRect(0, Display.getHeight() - footHeight, Display.getWidth(), footHeight, HEADER_COLOR);
        GuiElement.renderColoredRect(0, Display.getHeight() - footHeight, Display.getWidth(), 1, TRANSPARENT_HIGHLIGHT);
        this.textCopyright.render(Display.getWidth() / 2 - (this.textCopyright.getWidth() / 2), Display.getHeight() - (25));
    }

    @Override
    public void onElementClick(GuiElement element)
    {
        super.onElementClick(element);
    }

    @Override
    public void onScroll(int dwheel)
    {
        if (dwheel < 0 && this.scrollOffset > -maxScroll && maxScroll > Display.getHeight() - footHeight - headHeight)
        {
            System.out.println(this.scrollOffset);
            super.onScroll(dwheel);
        }
        else if (dwheel > 0)
        {
            super.onScroll(dwheel);
        }
    }
}
