package com.asx.sbsat;

import static com.asx.sbsat.Fonts.FONT_SEGOEUI_PLAIN_14;
import static com.asx.sbsat.Fonts.FONT_SEGOEUI_PLAIN_60;

import org.asx.glx.gui.elements.GuiText;

import com.asx.sbsat.SmartBattery.Cell;

public class BatteryFormData
{
    private SmartBattery battery;

    public GuiText       statusMarkers;
    public GuiText       serial;
    public GuiText       date;
    public GuiText       type;
    public GuiText       designVoltage;
    public GuiText       designCapacity;
    public GuiText       voltage;
    public GuiText       capacity;
    public GuiText       current;
    public GuiText       temperature;
    public GuiText       charge;
    public GuiText       chargeVisual;
    public GuiText       chargeVoltage;
    public GuiText       chargeCurrent;
    public GuiText       cycles;
    public GuiText       ttf;
    public GuiText       tte;

    public BatteryFormData(FormBatteryOverview form, SmartBattery battery)
    {
        this.battery = battery;

        this.statusMarkers = new GuiText(form, FONT_SEGOEUI_PLAIN_14, "");
        this.serial = new GuiText(form, FONT_SEGOEUI_PLAIN_14, "");
        this.date = new GuiText(form, FONT_SEGOEUI_PLAIN_14, "");
        this.type = new GuiText(form, FONT_SEGOEUI_PLAIN_14, "");
        this.designVoltage = new GuiText(form, FONT_SEGOEUI_PLAIN_14, "");
        this.designCapacity = new GuiText(form, FONT_SEGOEUI_PLAIN_14, "");
        this.voltage = new GuiText(form, FONT_SEGOEUI_PLAIN_14, "");
        this.capacity = new GuiText(form, FONT_SEGOEUI_PLAIN_14, "");
        this.current = new GuiText(form, FONT_SEGOEUI_PLAIN_14, "");
        this.temperature = new GuiText(form, FONT_SEGOEUI_PLAIN_14, "");
        this.charge = new GuiText(form, FONT_SEGOEUI_PLAIN_14, "");
        this.chargeVisual = new GuiText(form, FONT_SEGOEUI_PLAIN_60, "");
        this.chargeVoltage = new GuiText(form, FONT_SEGOEUI_PLAIN_14, "");
        this.chargeCurrent = new GuiText(form, FONT_SEGOEUI_PLAIN_14, "");
        this.cycles = new GuiText(form, FONT_SEGOEUI_PLAIN_14, "");
        this.ttf = new GuiText(form, FONT_SEGOEUI_PLAIN_14, "");
        this.tte = new GuiText(form, FONT_SEGOEUI_PLAIN_14, "");

        this.setDefaults();
    }

    private void setDefaults()
    {
        this.chargeVisual.setColor(Cell.COLOR_LABEL, Cell.COLOR_LABEL);
    }

    public void updateValues()
    {
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