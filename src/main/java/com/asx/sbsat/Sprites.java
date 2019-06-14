package com.asx.sbsat;

import org.asx.glx.opengl.Sprite;

public class Sprites
{
    public static final Sprite logoMain           = Sprite.load(SBSAT.RESOURCES.getLocation() + "/icon.png");
    public static final Sprite logoWide           = Sprite.load(SBSAT.RESOURCES.getLocation() + "/wide.png");
    public static final Sprite batteryCharging    = Sprite.load(SBSAT.RESOURCES.getLocation() + "/battery_charging.png");
    public static final Sprite batteryDefective   = Sprite.load(SBSAT.RESOURCES.getLocation() + "/battery_defective.png");
    public static final Sprite batteryEmpty       = Sprite.load(SBSAT.RESOURCES.getLocation() + "/battery_empty.png");
    public static final Sprite batteryUnavailable = Sprite.load(SBSAT.RESOURCES.getLocation() + "/battery_unavailable.png");
    public static final Sprite batteryUnspecified = Sprite.load(SBSAT.RESOURCES.getLocation() + "/battery_unspecified.png");
    public static final Sprite batteryWarning     = Sprite.load(SBSAT.RESOURCES.getLocation() + "/battery_warning.png");
    public static final Sprite cellCharging       = Sprite.load(SBSAT.RESOURCES.getLocation() + "/cell_charging.png");
    public static final Sprite cellDefective      = Sprite.load(SBSAT.RESOURCES.getLocation() + "/cell_defective.png");
    public static final Sprite cellEmpty          = Sprite.load(SBSAT.RESOURCES.getLocation() + "/cell_empty.png");
    public static final Sprite cellUnavailable    = Sprite.load(SBSAT.RESOURCES.getLocation() + "/cell_unavailable.png");
    public static final Sprite cellUnspecified    = Sprite.load(SBSAT.RESOURCES.getLocation() + "/cell_unspecified.png");
    public static final Sprite cellWarning        = Sprite.load(SBSAT.RESOURCES.getLocation() + "/cell_warning.png");
}