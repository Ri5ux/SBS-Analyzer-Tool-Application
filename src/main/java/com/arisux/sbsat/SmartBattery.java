package com.arisux.sbsat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.asx.glx.gui.elements.GuiText;
import org.asx.glx.opengl.Sprite;
import org.newdawn.slick.Color;

import com.arisux.sbsat.SBSATMain.Sprites;

public class SmartBattery
{
    private static final Logger logger          = Logger.getLogger("SBSAT");
    private ArrayList<Cell>     cells           = new ArrayList<Cell>();
    private Sprite              statusIndicator = SBSATMain.Sprites.batteryUnspecified;
    // private boolean canRenderMeter;

    /** Parsed values **/
    protected String            manufacturer;
    protected String            device;
    protected String            serial;
    protected String            manufactureDate;
    protected String            type;
    protected double            designVoltage;
    protected int               designCapacity;
    protected String            statusString;
    protected ArrayList<Status> status;
    protected double            voltage;
    protected double            current;
    protected int               capacity;
    protected ArrayList<Double> cellVoltages;
    protected double            cell1Voltage;
    protected double            cell2Voltage;
    protected double            cell3Voltage;
    protected double            cell4Voltage;
    protected double            temperature;
    protected int               charge;
    protected int               chargeCurrent;
    protected double            chargeVoltage;
    protected int               cycles;
    protected int               timeToFull;
    protected int               timeToEmpty;

    public static enum Status
    {
        DISCHARGED,
        CHARGED,
        DISCHARGING,
        INIT,
        REM_TIME_ALARM,
        REM_CAPACITY_ALARM,
        TERMINATE_DISCHARGE_ALARM,
        OVERTEMP_ALARM,
        TERMINATE_CHARGE_ALARM,
        OVERCHARGE_ALARM;

        public static Status getStatus(String string)
        {
            for (Status status : Status.values())
            {
                if (status.name().equalsIgnoreCase(string))
                {
                    return status;
                }
            }

            return null;
        }
    }

    public static class Cell
    {
        public static final Color COLOR_LABEL = new Color(0xFF007700);
        protected SmartBattery    battery;

        // private boolean canRenderMeter;

        private int               index;
        private double            voltage;
        private double            voltageMax;
        private double            voltageMin;

        private GuiText           labelIndex;
        private GuiText           labelVoltage;
        private GuiText           labelVoltageMin;
        private GuiText           labelVoltageMax;
        private GuiText           labelCharge;
        private Sprite            cellStatusIndicator;

        public Cell(SmartBattery battery, int index)
        {
            this.battery = battery;
            this.index = index;
            FormBatteryOverview f = FormBatteryOverview.instance();
            this.labelIndex = new GuiText(f, FormSBSATBase.FONT_SEGOEUI_PLAIN_14, "Cell " + String.valueOf(index));
            this.labelVoltage = new GuiText(f, FormSBSATBase.FONT_SEGOEUI_PLAIN_14, "0V");
            this.labelVoltageMin = new GuiText(f, FormSBSATBase.FONT_SEGOEUI_PLAIN_14, "0V");
            this.labelVoltageMax = new GuiText(f, FormSBSATBase.FONT_SEGOEUI_PLAIN_14, "0V");
            this.labelCharge = new GuiText(f, FormSBSATBase.FONT_SEGOEUI_PLAIN_20, "0%");
            this.labelCharge.setApplyShadow(true);
            this.labelCharge.setColor(COLOR_LABEL, COLOR_LABEL);
            this.cellStatusIndicator = Sprites.batteryEmpty;
        }

        public void updateCellSprite()
        {
            ArrayList<Status> markers = this.getBattery().getStatusMarkers();
            // this.canRenderMeter = false;

            if (markers.contains(Status.OVERTEMP_ALARM) || markers.contains(Status.OVERCHARGE_ALARM))
            {
                this.setCellStatusIndicator(SBSATMain.Sprites.cellWarning);
            }
            else if (this.getVoltage() == 0 && this.getBattery().getSerial().isEmpty())
            {
                this.setCellStatusIndicator(SBSATMain.Sprites.cellUnavailable);
            }
            else if (this.getChargePercent() >= -20)
            {
                this.setCellStatusIndicator(SBSATMain.Sprites.cellEmpty);
                // this.canRenderMeter = true;
            }
            else
            {
                this.setCellStatusIndicator(SBSATMain.Sprites.cellDefective);
            }
        }

        public SmartBattery getBattery()
        {
            return battery;
        }

        public double getChargePercent()
        {
            return Math.round(Util.map(this.voltage, this.voltageMin, this.voltageMax, 0, 100) * 100D) / 100D;
        }

        public int getIndex()
        {
            return index;
        }

        public double getVoltageMin()
        {
            return Math.round(voltageMin * 100D) / 100D;
        }

        public void setVoltageMin(double voltageMin)
        {
            this.voltageMin = voltageMin;
            this.labelVoltageMin.setText(this.getVoltageMin() + "V");
        }

        public double getVoltageMax()
        {
            return Math.round(voltageMax * 100D) / 100D;
        }

        public void setVoltageMax(double voltageMax)
        {
            this.voltageMax = voltageMax;
            this.labelVoltageMax.setText(this.getVoltageMax() + "V");
        }

        public double getVoltage()
        {
            return voltage;
        }

        public void setVoltage(double voltage)
        {
            this.voltage = voltage;
            this.labelVoltage.setText(voltage + "V");
        }

        public GuiText getLabelIndex()
        {
            return labelIndex;
        }

        public GuiText getLabelVoltage()
        {
            return labelVoltage;
        }

        public GuiText getLabelVoltageMin()
        {
            return labelVoltageMin;
        }

        public GuiText getLabelVoltageMax()
        {
            return labelVoltageMax;
        }

        public GuiText getLabelCharge()
        {
            return labelCharge;
        }

        public Sprite getCellStatusIndicator()
        {
            return cellStatusIndicator;
        }

        public void setCellStatusIndicator(Sprite cellStatusIndicator)
        {
            this.cellStatusIndicator = cellStatusIndicator;
        }
    }

    private SmartBattery()
    {
        this.cellVoltages = new ArrayList<Double>();
        this.status = new ArrayList<Status>();
    }

    public static SmartBattery parse(ArrayList<String> batteryData)
    {
        SmartBattery battery = new SmartBattery();

        Map<String, String> dataMap = new HashMap<String, String>();

        for (String line : batteryData)
        {
            if (line.contains(": "))
            {
                String[] s = line.split(": ");

                if (s.length != 2)
                {

                    logger.warning("Invalid Smart Battery Data. Unable to parse.");
                    return null;
                }

                s[1] = s[1].replaceAll("(\\r|\\n|\\r\\n)+", "");

                dataMap.put(s[0], s[1]);
            }
        }

        try
        {
            battery.manufacturer = dataMap.get("Manufacturer");
            battery.device = dataMap.get("Device");
            battery.serial = dataMap.get("SN");
            battery.manufactureDate = dataMap.get("Manufactured");
            battery.type = dataMap.get("Type");
            battery.designVoltage = Double.parseDouble(dataMap.get("DesignVoltage").split(" ")[0]);
            battery.designCapacity = (int) Double.parseDouble(dataMap.get("DesignCapacity").split(" ")[0]);
            battery.statusString = dataMap.get("Status");
            battery.voltage = Double.parseDouble(dataMap.get("Voltage").split(" ")[0]);
            battery.current = Double.parseDouble(dataMap.get("Current").split(" ")[0]);
            battery.capacity = (int) Double.parseDouble(dataMap.get("Capacity").split(" ")[0]);
            battery.cellVoltages.add(Double.parseDouble(dataMap.get("C1").split(" ")[0]));
            battery.cellVoltages.add(Double.parseDouble(dataMap.get("C2").split(" ")[0]));
            battery.cellVoltages.add(Double.parseDouble(dataMap.get("C3").split(" ")[0]));
            battery.cellVoltages.add(Double.parseDouble(dataMap.get("C4").split(" ")[0]));
            battery.temperature = Double.parseDouble(dataMap.get("Temp").split(" ")[0]);
            battery.charge = (int) Double.parseDouble(dataMap.get("Charge").split(" ")[0]);
            battery.chargeVoltage = Double.parseDouble(dataMap.get("ChargeVoltage").split(" ")[0]);
            battery.chargeCurrent = (int) Double.parseDouble(dataMap.get("ChargeCurrent").split(" ")[0]);
            battery.cycles = (int) Double.parseDouble(dataMap.get("Cycles"));
            battery.timeToFull = (int) Double.parseDouble(dataMap.get("TTF").split(" ")[0]);
            battery.timeToEmpty = (int) Double.parseDouble(dataMap.get("TTE").split(" ")[0]);

            battery.initRuntimeVariables();
        }
        catch (Exception e)
        {
            System.out.println("Invalid Smart Battery Data. Unable to parse.");
            e.printStackTrace();
            return null;
        }

        return battery;
    }

    private void initRuntimeVariables()
    {
        int idx = 1;

        for (double v : this.cellVoltages)
        {
            Cell cell = new Cell(this, idx);

            /** 3 cell fix **/
            if (idx == 4 && v > 0 || idx != 4)
            {
                cell.setVoltage(v);
                this.cells.add(cell);
            }
            idx++;
        }

        double voltsPerCellMin = this.getDesignVoltage() / this.getAmountOfCells();
        double voltsPerCellMax = this.getChargeVoltage() / this.getAmountOfCells();

        for (Cell c : this.getCells())
        {
            c.setVoltageMin(voltsPerCellMin);
            c.setVoltageMax(voltsPerCellMax);
            c.getLabelCharge().setText(c.getChargePercent() + "%");
        }

        String[] markers = this.statusString.split(" ");

        for (String marker : markers)
        {
            this.status.add(Status.getStatus(marker));
        }
    }

    public ArrayList<Cell> getCells()
    {
        return cells;
    }

    public int getAmountOfCells()
    {
        return this.getCells().size();
    }

    public String getManufacturer()
    {
        return manufacturer;
    }

    public String getDevice()
    {
        return device;
    }

    public int getCapacity()
    {
        return capacity;
    }

    public String getSerial()
    {
        return serial;
    }

    public String getManufactureDate()
    {
        return manufactureDate;
    }

    public String getType()
    {
        return type;
    }

    public double getDesignVoltage()
    {
        return designVoltage;
    }

    public int getDesignCapacity()
    {
        return designCapacity;
    }

    public String getStatusString()
    {
        return statusString;
    }

    public ArrayList<Status> getStatusMarkers()
    {
        return status;
    }

    public double getVoltage()
    {
        return voltage;
    }

    public double getCurrent()
    {
        return current;
    }

    public double getCell1Voltage()
    {
        return cell1Voltage;
    }

    public double getCell2Voltage()
    {
        return cell2Voltage;
    }

    public double getCell3Voltage()
    {
        return cell3Voltage;
    }

    public double getCell4Voltage()
    {
        return cell4Voltage;
    }

    public double getTemperature()
    {
        return temperature;
    }

    public int getCharge()
    {
        return charge;
    }

    public int getCycles()
    {
        return cycles;
    }

    public int getTimeToFull()
    {
        return timeToFull;
    }

    public int getTimeToEmpty()
    {
        return timeToEmpty;
    }

    public int getChargeCurrent()
    {
        return chargeCurrent;
    }

    public double getChargeVoltage()
    {
        return chargeVoltage;
    }

    public Sprite getStatusIndicator()
    {
        return statusIndicator;
    }

    public void setStatusIndicator(Sprite statusIndicator)
    {
        this.statusIndicator = statusIndicator;
    }

    public void updateStatusIndicator()
    {
        ArrayList<Status> markers = this.getStatusMarkers();

        if (markers.contains(Status.OVERTEMP_ALARM) || markers.contains(Status.OVERCHARGE_ALARM))
        {
            this.setStatusIndicator(SBSATMain.Sprites.batteryWarning);
        }
        else if (this.getVoltage() == 0 && this.getSerial().isEmpty())
        {
            this.setStatusIndicator(SBSATMain.Sprites.batteryUnavailable);
        }
        else if (markers.contains(Status.CHARGED))
        {
            this.setStatusIndicator(SBSATMain.Sprites.batteryCharging);
        }
        else if (this.getCharge() >= -20)
        {
            this.setStatusIndicator(SBSATMain.Sprites.batteryEmpty);
            // this.canRenderMeter = true;
        }
        else
        {
            this.setStatusIndicator(SBSATMain.Sprites.batteryDefective);
        }
    }
    
    public static Logger logger()
    {
        return logger;
    }
}
