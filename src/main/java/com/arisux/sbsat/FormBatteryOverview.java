package com.arisux.sbsat;

import java.awt.Font;

import org.asx.glx.gui.GuiPanel;
import org.asx.glx.gui.elements.GuiElement;
import org.asx.glx.gui.elements.GuiText;
import org.asx.glx.gui.forms.GuiForm;
import org.asx.glx.opengl.Sprite;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.arisux.sbsat.SmartBattery.Cell;

public class FormBatteryOverview extends FormSBSATBase
{
    private static FormBatteryOverview INSTANCE;

    private static final Color         CELL_BACKGROUND_COLOR = new Color(0x0A0A0A);
    private static final Font          FONT_SEGOEUI_PLAIN_60 = new Font("Segoe UI", Font.PLAIN, 60);

    GuiText                            statusMarkers         = new GuiText(this, FONT_SEGOEUI_PLAIN_14, "");
    GuiText                            serial                = new GuiText(this, FONT_SEGOEUI_PLAIN_14, "");
    GuiText                            date                  = new GuiText(this, FONT_SEGOEUI_PLAIN_14, "");
    GuiText                            type                  = new GuiText(this, FONT_SEGOEUI_PLAIN_14, "");
    GuiText                            designVoltage         = new GuiText(this, FONT_SEGOEUI_PLAIN_14, "");
    GuiText                            designCapacity        = new GuiText(this, FONT_SEGOEUI_PLAIN_14, "");
    GuiText                            voltage               = new GuiText(this, FONT_SEGOEUI_PLAIN_14, "");
    GuiText                            capacity              = new GuiText(this, FONT_SEGOEUI_PLAIN_14, "");
    GuiText                            current               = new GuiText(this, FONT_SEGOEUI_PLAIN_14, "");
    GuiText                            temperature           = new GuiText(this, FONT_SEGOEUI_PLAIN_14, "");
    GuiText                            charge                = new GuiText(this, FONT_SEGOEUI_PLAIN_14, "");
    GuiText                            chargeVisual          = new GuiText(this, FONT_SEGOEUI_PLAIN_60, "");
    GuiText                            chargeVoltage         = new GuiText(this, FONT_SEGOEUI_PLAIN_14, "");
    GuiText                            chargeCurrent         = new GuiText(this, FONT_SEGOEUI_PLAIN_14, "");
    GuiText                            cycles                = new GuiText(this, FONT_SEGOEUI_PLAIN_14, "");
    GuiText                            ttf                   = new GuiText(this, FONT_SEGOEUI_PLAIN_14, "");
    GuiText                            tte                   = new GuiText(this, FONT_SEGOEUI_PLAIN_14, "");
    GuiText                            console               = new GuiText(this, FONT_SEGOEUI_PLAIN_14, "");

    public static FormBatteryOverview instance()
    {
        return INSTANCE;
    }

    public FormBatteryOverview(GuiPanel panel, GuiForm parentForm)
    {
        super(panel, parentForm);
        INSTANCE = this;
        this.info.setText("Connect a battery...");
        this.chargeVisual.setColor(Cell.COLOR_LABEL, Cell.COLOR_LABEL);
    }

    public void updateConsole(String consoleOutput)
    {
        this.console.setText(consoleOutput);
    }

    public void updateTextValues(SmartBattery battery)
    {
        if (battery != null)
        {
            info.setText(String.format("%s %s(%s)", battery.getManufacturer(), battery.getDevice(), battery.getSerial()));
            serial.setText(String.format("%s: %s%s", "Serial Number", battery.getSerial(), ""));
            date.setText(String.format("%s: %s%s", "Manufactured", battery.getManufactureDate(), ""));
            type.setText(String.format("%s: %s%s", "Battery Chemistry", battery.getType(), ""));
            designVoltage.setText(String.format("%s: %s%s", "Design Voltage", battery.getDesignVoltage(), "V"));
            designCapacity.setText(String.format("%s: %s%s", "Design Capacity", battery.getDesignCapacity(), "mAh"));
            statusMarkers.setText(String.format("%s: %s%s", "Status Markers", battery.getStatusMarkers().toString(), ""));
            voltage.setText(String.format("%s: %s%s", "Voltage", battery.getVoltage(), "V"));
            capacity.setText(String.format("%s: %s%s", "Capacity", battery.getCapacity(), "mAh"));
            current.setText(String.format("%s: %s%s", "Current", battery.getCurrent(), "A"));
            temperature.setText(String.format("%s: %s%s", "Temperature", battery.getTemperature(), "C"));
            charge.setText(String.format("%s: %s%s", "Charge", battery.getCharge(), "%"));
            chargeVisual.setText(String.format("%s%s", battery.getCharge(), "%"));
            chargeVoltage.setText(String.format("%s: %s%s", "Charge Voltage", battery.getChargeVoltage(), "V"));
            chargeCurrent.setText(String.format("%s: %s%s", "Charge Current", battery.getChargeCurrent(), "ma"));
            cycles.setText(String.format("%s: %s%s", "Cycles", battery.getCycles(), " cycles"));
            ttf.setText(String.format("%s: %s%s", "Time To FULL", battery.getTimeToFull(), "minutes"));
            tte.setText(String.format("%s: %s%s", "Time To EMPTY", battery.getTimeToEmpty(), "minutes"));
        }
    }

    @Override
    public void render()
    {
        super.render();
        
        AnalyzerDevice device = SBSATMain.instance().getConnectedDevice();

        if (device != null)
        {
            SmartBattery battery = device.getSmartBattery();

            if (battery != null)
            {
                int width = Display.getWidth();
                int cellWidth = 355;
                int qtyCells = battery.getCells().size();
                int cellPadding = 10;
                int leftCellPadding = 60;
                int i = 0;
                double centerCellIndex = qtyCells / 2.0 - 1;
                int centerOfWindowX = width / 2;

                GuiElement.renderColoredRect(0, 50, width, 75, CELL_BACKGROUND_COLOR);
                GuiElement.renderColoredRect(0, 124, width, 1, TRANSPARENT_HIGHLIGHT);

                int mult = (width / (qtyCells - 0));

                for (Cell cell : battery.getCells())
                {
                    int cellX = 0;

                    if (i <= centerCellIndex)
                    {
                        cellX = leftCellPadding + cellPadding + (mult * i);
                    }
                    else if (((qtyCells % 2) == 1) && ((i) == Math.round(centerCellIndex)))
                    {
                        cellX = (leftCellPadding / 2) + centerOfWindowX - (cellWidth / 4);
                    }
                    else if (i > centerCellIndex)
                    {
                        cellX = width - (mult * (qtyCells - (i + 1))) - (cellWidth / 2) - cellPadding;
                    }

                    int cellY = 60;

                    cell.updateCellSprite();

                    if (cell.getCellStatusIndicator() == SBSATMain.Sprites.cellEmpty)
                    {
                        if (cell.getChargePercent() > 0)
                        {
                            int cellColor = cell.getChargePercent() > 25 ? 0x00ff4e : 0xAA0000;
                            double p = cell.getChargePercent();
                            p = p > 100 ? 100 : p;
                            double chargeMeterWidth = Util.map(p, 0.0D, 100.0D, 0.0D, 150.0D);
                            GuiElement.renderColoredRect(cellX + 7, cellY + 7, (int) chargeMeterWidth, 40, new Color(cellColor));
                        }
                    }

                    cell.getCellStatusIndicator().draw(cellX, cellY, 0.35F);
                    cell.getLabelCharge().render(cellX + 20, cellY + 11);
                    cell.getLabelIndex().render(cellX - 60, cellY - 5);
                    cell.getLabelVoltage().render(cellX - 60, cellY + 10);
                    cell.getLabelVoltageMin().render(cellX - 60, cellY + 25);
                    cell.getLabelVoltageMax().render(cellX - 60, cellY + 40);
                    i++;
                }

                if (battery != null)
                {
                    int v = 0;
                    int startX = 10;
                    int startY = 135;
                    int columnWidth = width / 3;
                    int columnHeight = 0;

                    serial.setBackgroundColor(Color.black);
                    serial.setPadding(5, 0);
                    serial.setWidth(columnWidth);
                    serial.render(startX, startY + (v++ * 21));
                    date.setBackgroundColor(Color.black);
                    date.setPadding(5, 0);
                    date.setWidth(columnWidth);
                    date.render(startX, startY + (v++ * 21));
                    type.setBackgroundColor(Color.black);
                    type.setPadding(5, 0);
                    type.setWidth(columnWidth);
                    type.render(startX, startY + (v++ * 21));
                    designVoltage.setBackgroundColor(Color.black);
                    designVoltage.setPadding(5, 0);
                    designVoltage.setWidth(columnWidth);
                    designVoltage.render(startX, startY + (v++ * 21));
                    designCapacity.setBackgroundColor(Color.black);
                    designCapacity.setPadding(5, 0);
                    designCapacity.setWidth(columnWidth);
                    designCapacity.render(startX, startY + (v++ * 21));
                    statusMarkers.setBackgroundColor(Color.black);
                    statusMarkers.setPadding(5, 0);
                    statusMarkers.setWidth(columnWidth);
                    statusMarkers.render(startX, startY + (v++ * 21));
                    voltage.setBackgroundColor(Color.black);
                    voltage.setPadding(5, 0);
                    voltage.setWidth(columnWidth);
                    voltage.render(startX, startY + (v++ * 21));
                    capacity.setBackgroundColor(Color.black);
                    capacity.setPadding(5, 0);
                    capacity.setWidth(columnWidth);
                    capacity.render(startX, startY + (v++ * 21));
                    current.setBackgroundColor(Color.black);
                    current.setPadding(5, 0);
                    current.setWidth(columnWidth);
                    current.render(startX, startY + (v++ * 21));
                    temperature.setBackgroundColor(Color.black);
                    temperature.setPadding(5, 0);
                    temperature.setWidth(columnWidth);
                    temperature.render(startX, startY + (v++ * 21));
                    charge.setBackgroundColor(Color.black);
                    charge.setPadding(5, 0);
                    charge.setWidth(columnWidth);
                    charge.render(startX, startY + (v++ * 21));
                    chargeVoltage.setBackgroundColor(Color.black);
                    chargeVoltage.setPadding(5, 0);
                    chargeVoltage.setWidth(columnWidth);
                    chargeVoltage.render(startX, startY + (v++ * 21));
                    chargeCurrent.setBackgroundColor(Color.black);
                    chargeCurrent.setPadding(5, 0);
                    chargeCurrent.setWidth(columnWidth);
                    chargeCurrent.render(startX, startY + (v++ * 21));
                    cycles.setBackgroundColor(Color.black);
                    cycles.setPadding(5, 0);
                    cycles.setWidth(columnWidth);
                    cycles.render(startX, startY + (v++ * 21));
                    ttf.setBackgroundColor(Color.black);
                    ttf.setPadding(5, 0);
                    ttf.setWidth(columnWidth);
                    ttf.render(startX, startY + (v++ * 21));
                    tte.setBackgroundColor(Color.black);
                    tte.setPadding(5, 0);
                    tte.setWidth(columnWidth);
                    tte.render(startX, startY + (columnHeight = v++ * 21));

                    battery.updateStatusIndicator();
                    Sprite s = battery.getStatusIndicator();
                    float scaleSprite = 0.25F;
                    int battX = Math.round(width - (s.getWidth() * scaleSprite) - cellPadding) - (int) ((width - columnWidth) / 2 - (s.getWidth() * scaleSprite) / 2);
                    int battY = (135) + (int) ((columnHeight / 2) - (150 / 2));
                    s.draw(battX, battY, scaleSprite);

                    if (battery.getCharge() > 0)
                    {
                        int cellColor = battery.getCharge() > 25 ? 0x00ff4e : 0xAA0000;

                        double chargeMeterWidth = Util.map(battery.getCharge(), 0.0D, 100.0D, 0.0D, 255.0D);
                        GuiElement.renderColoredRect(battX + 16, battY + 16, (int) chargeMeterWidth, 122, new Color(cellColor));
                        this.chargeVisual.render((int) (battX + (s.getWidth() * scaleSprite / 2 - 20) - this.chargeVisual.getWidth() / 2), battY + 30);
                    }

                    int consoleStartY = startY + cellPadding * 3 + columnHeight;
                    GuiElement.renderColoredRect(0, consoleStartY, width, Display.getHeight() - columnHeight, CELL_BACKGROUND_COLOR);
                    GuiElement.renderColoredRect(cellPadding, consoleStartY + cellPadding, width - (cellPadding * 2), Display.getHeight() - consoleStartY - (cellPadding * 2), Color.black);
                    console.render(cellPadding * 2, consoleStartY);
                }
            }
            else if (!SBSATMain.instance().isConnected())
            {
                SBSATMain.setStatusText("Connecting...");
            }
            else if (SBSATMain.instance().isConnected())
            {
                SBSATMain.setStatusText("Waiting for data...");
            }
        }
    }
}
