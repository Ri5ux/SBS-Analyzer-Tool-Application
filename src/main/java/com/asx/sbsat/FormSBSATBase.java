package com.asx.sbsat;

import static com.asx.sbsat.Colors.BACKGROUND_COLOR;
import static com.asx.sbsat.Colors.HEADER_COLOR;
import static com.asx.sbsat.Colors.NORMAL_COLOR;
import static com.asx.sbsat.Colors.TRANSPARENT_HIGHLIGHT;
import static com.asx.sbsat.Fonts.FONT_SEGOEUI_BOLD_20;
import static com.asx.sbsat.Fonts.FONT_SEGOEUI_PLAIN_20;

import org.asx.glx.gui.GuiPanel;
import org.asx.glx.gui.elements.GuiElement;
import org.asx.glx.gui.elements.GuiText;
import org.asx.glx.gui.forms.GuiForm;
import org.lwjgl.opengl.Display;

public abstract class FormSBSATBase extends GuiForm
{
    protected GuiText            title;
    protected GuiText            info;
    protected GuiText            status;

    public FormSBSATBase(GuiPanel panel, GuiForm parentForm)
    {
        super(panel, parentForm);

        title = new GuiText(this, FONT_SEGOEUI_BOLD_20, "");
        title.setColor(NORMAL_COLOR, NORMAL_COLOR);
        info = new GuiText(this, FONT_SEGOEUI_BOLD_20, "");
        status = new GuiText(this, FONT_SEGOEUI_BOLD_20, "");
    }

    @Override
    public void render()
    {
        if (SBSAT.getUserInterface().getPanel().getActiveForm() == this)
        {
            GuiElement.renderColoredRect(0, 0, Display.getWidth(), Display.getHeight(), BACKGROUND_COLOR);
            super.render();
            GuiElement.renderColoredRect(0, 0, Display.getWidth(), 50, HEADER_COLOR);
            GuiElement.renderColoredRect(0, 49, Display.getWidth(), 1, TRANSPARENT_HIGHLIGHT);
            title.setText("Smart Battery System Analyzer Tool");
            title.setFont(FONT_SEGOEUI_PLAIN_20);
            title.render(150, 12);
            Sprites.logoWide.draw(10, 0, 1F);
            info.render(Display.getWidth() - info.getWidth() - 20, 12);
            status.render(Display.getWidth() / 2 - (status.getWidth() / 2), Display.getHeight() / 2);
        }
    }

    @Override
    public void onScroll(int dwheel)
    {
        super.onScroll(dwheel);
    }

    @Override
    public void onElementClick(GuiElement element)
    {
        super.onElementClick(element);
    }

    @Override
    public void onKey(int key, char character)
    {
        super.onKey(key, character);
    }

    public GuiText getInfo()
    {
        return info;
    }
    
    public GuiText getStatus()
    {
        return status;
    }
}
