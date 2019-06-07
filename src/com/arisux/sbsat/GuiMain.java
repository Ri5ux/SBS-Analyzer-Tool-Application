package com.arisux.sbsat;

import org.asx.glx.gui.GuiPanel;
import org.asx.glx.gui.themes.Theme;

public class GuiMain extends GuiPanel
{
    public GuiMain()
    {
        super(new Theme());

        new FormComPortSelection(this, null);
        new FormBatteryOverview(this, null);

        this.activeForm = FormComPortSelection.instance();
    }

    @Override
    public void render()
    {
        super.render();
    }
}
