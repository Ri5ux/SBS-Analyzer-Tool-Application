package com.asx.sbsat;

import static com.asx.sbsat.Colors.CELL_BACKGROUND_COLOR;
import static com.asx.sbsat.Colors.TRANSPARENT_HIGHLIGHT;
import static com.asx.sbsat.Fonts.FONT_SEGOEUI_PLAIN_14;

import org.asx.glx.gui.GuiPanel;
import org.asx.glx.gui.elements.GuiElement;
import org.asx.glx.gui.elements.GuiText;
import org.asx.glx.gui.forms.GuiForm;
import org.asx.glx.opengl.Sprite;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.asx.sbsat.SmartBattery.Cell;

public class FormBatteryOverview extends FormSBSATBase
{
    private static FormBatteryOverview INSTANCE;

    protected GuiText                  console = new GuiText(this, FONT_SEGOEUI_PLAIN_14, "");

    public static FormBatteryOverview instance()
    {
        return INSTANCE;
    }

    public FormBatteryOverview(GuiPanel panel, GuiForm parentForm)
    {
        super(panel, parentForm);
        INSTANCE = this;
        this.info.setText("Connect a battery...");
    }

    public void updateConsole(String consoleOutput)
    {
        this.console.setText(consoleOutput);
    }

    @Override
    public void render()
    {
        super.render();

        SerialDevice device = SBSAT.instance().getConnectedDevice();

        if (device != null)
        {
            if (device.getProtocol() != null && device.getProtocol().getMaxBatteries() == 1 && device.getSmartBatteries() != null && device.getSmartBatteries().length == 1)
            {
                renderBatteryOverview(device);
            }
            else if (SBSAT.instance().getConnectedDevice() != null && !SBSAT.instance().getConnectedDevice().isConnected())
            {
                SBSAT.setStatusText("Connecting...");
            }
            else if (SBSAT.instance().getConnectedDevice() != null && SBSAT.instance().getConnectedDevice().isConnected())
            {
                SBSAT.setStatusText("Waiting for data...");
            }
        }
    }

    protected void renderBatteryOverview(SerialDevice device)
    {
        SmartBattery battery = device.getSmartBatteries()[0];

        if (battery != null)
        {
            info.setText(String.format("%s %s(%s)", battery.getManufacturer(), battery.getDevice(), battery.getSerial()));

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

                if (cell.getCellStatusIndicator() == Sprites.cellEmpty)
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

                BatteryFormData data = battery.getFormData();

                data.serial.setBackgroundColor(Color.black);
                data.serial.setPadding(5, 0);
                data.serial.setWidth(columnWidth);
                data.serial.render(startX, startY + (v++ * 21));
                data.date.setBackgroundColor(Color.black);
                data.date.setPadding(5, 0);
                data.date.setWidth(columnWidth);
                data.date.render(startX, startY + (v++ * 21));
                data.type.setBackgroundColor(Color.black);
                data.type.setPadding(5, 0);
                data.type.setWidth(columnWidth);
                data.type.render(startX, startY + (v++ * 21));
                data.designVoltage.setBackgroundColor(Color.black);
                data.designVoltage.setPadding(5, 0);
                data.designVoltage.setWidth(columnWidth);
                data.designVoltage.render(startX, startY + (v++ * 21));
                data.designCapacity.setBackgroundColor(Color.black);
                data.designCapacity.setPadding(5, 0);
                data.designCapacity.setWidth(columnWidth);
                data.designCapacity.render(startX, startY + (v++ * 21));
                data.statusMarkers.setBackgroundColor(Color.black);
                data.statusMarkers.setPadding(5, 0);
                data.statusMarkers.setWidth(columnWidth);
                data.statusMarkers.render(startX, startY + (v++ * 21));
                data.voltage.setBackgroundColor(Color.black);
                data.voltage.setPadding(5, 0);
                data.voltage.setWidth(columnWidth);
                data.voltage.render(startX, startY + (v++ * 21));
                data.capacity.setBackgroundColor(Color.black);
                data.capacity.setPadding(5, 0);
                data.capacity.setWidth(columnWidth);
                data.capacity.render(startX, startY + (v++ * 21));
                data.current.setBackgroundColor(Color.black);
                data.current.setPadding(5, 0);
                data.current.setWidth(columnWidth);
                data.current.render(startX, startY + (v++ * 21));
                data.temperature.setBackgroundColor(Color.black);
                data.temperature.setPadding(5, 0);
                data.temperature.setWidth(columnWidth);
                data.temperature.render(startX, startY + (v++ * 21));
                data.charge.setBackgroundColor(Color.black);
                data.charge.setPadding(5, 0);
                data.charge.setWidth(columnWidth);
                data.charge.render(startX, startY + (v++ * 21));
                data.chargeVoltage.setBackgroundColor(Color.black);
                data.chargeVoltage.setPadding(5, 0);
                data.chargeVoltage.setWidth(columnWidth);
                data.chargeVoltage.render(startX, startY + (v++ * 21));
                data.chargeCurrent.setBackgroundColor(Color.black);
                data.chargeCurrent.setPadding(5, 0);
                data.chargeCurrent.setWidth(columnWidth);
                data.chargeCurrent.render(startX, startY + (v++ * 21));
                data.cycles.setBackgroundColor(Color.black);
                data.cycles.setPadding(5, 0);
                data.cycles.setWidth(columnWidth);
                data.cycles.render(startX, startY + (v++ * 21));
                data.ttf.setBackgroundColor(Color.black);
                data.ttf.setPadding(5, 0);
                data.ttf.setWidth(columnWidth);
                data.ttf.render(startX, startY + (v++ * 21));
                data.tte.setBackgroundColor(Color.black);
                data.tte.setPadding(5, 0);
                data.tte.setWidth(columnWidth);
                data.tte.render(startX, startY + (columnHeight = v++ * 21));

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
                    data.chargeVisual.render((int) (battX + (s.getWidth() * scaleSprite / 2 - 20) - data.chargeVisual.getWidth() / 2), battY + 30);
                }

                int consoleStartY = startY + cellPadding * 3 + columnHeight;
                GuiElement.renderColoredRect(0, consoleStartY, width, Display.getHeight() - columnHeight, CELL_BACKGROUND_COLOR);
                GuiElement.renderColoredRect(cellPadding, consoleStartY + cellPadding, width - (cellPadding * 2), Display.getHeight() - consoleStartY - (cellPadding * 2), Color.black);
                console.render(cellPadding * 2, consoleStartY);
            }

            SBSAT.setStatusText("");
        }
    }
}
