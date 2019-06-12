package com.arisux.sbsat;

import java.awt.Font;

import org.asx.glx.gui.GuiPanel;
import org.asx.glx.gui.elements.GuiElement;
import org.asx.glx.gui.elements.GuiText;
import org.asx.glx.gui.forms.GuiForm;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.arisux.sbsat.SBSATMain.Sprites;

public abstract class FormSBSATBase extends GuiForm
{
    protected static final Font  FONT_SEGOEUI_BOLD_20         = new Font("Segoe UI", Font.BOLD, 20);
    protected static final Font  FONT_SEGOEUI_PLAIN_14        = new Font("Segoe UI", Font.PLAIN, 14);
    protected static final Font  FONT_SEGOEUI_PLAIN_20        = new Font("Segoe UI", Font.PLAIN, 20);

    protected static final Color BACKGROUND_COLOR             = new Color(0.2F, 0.2F, 0.2F, 0.25F);
    protected static final Color HIGHLIGHT_COLOR              = new Color(0.75F, 0, 0.1F, 1F);
    protected static final Color NORMAL_COLOR                 = new Color(1F, 1F, 1F, 0.75F);
    protected static final Color HOVER_COLOR                  = new Color(1F, 1F, 1F, 1F);
    protected static final Color TITLEBAR_COLOR               = new Color(0.175F, 0.175F, 0.175F, 0.95F);
    protected static final Color BUTTON_COLOR                 = new Color(0.5F, 0.5F, 0.5F, 0.25F);
    protected static final Color BUTTON_HOVER_COLOR           = new Color(0.75F, 0.75F, 0.75F, 0.25F);
    protected static final Color ERROR_HOVER_BACKGROUND_COLOR = new Color(0.75F, 0, 0.1F, 1F);
    protected static final Color ERROR_BACKGROUND_COLOR       = new Color(0.75F, 0, 0.1F, 0.75F);
    protected static final Color HEADER_COLOR                 = new Color(0x222222);
    protected static final Color TRANSPARENT_HIGHLIGHT        = new Color(0x22FFFFFF);
    protected static final Color COLOR_NEON_GREEN             = new Color(0xFFAAFF00);

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
        if (SBSATMain.getUserInterface().getPanel().getActiveForm() == this)
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
