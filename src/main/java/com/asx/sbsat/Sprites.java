package com.asx.sbsat;

import org.asx.glx.opengl.ResourceLocation;
import org.asx.glx.opengl.Sprite;

public class Sprites
{
    public static final Sprite logoMain           = Sprite.load(new ResourceLocation(SBSAT.RESOURCES, "icon.png"));
    public static final Sprite logoWide           = Sprite.load(new ResourceLocation(SBSAT.RESOURCES, "wide.png"));
    public static final Sprite batteryCharging    = Sprite.load(new ResourceLocation(SBSAT.RESOURCES, "battery_charging.png"));
    public static final Sprite batteryDefective   = Sprite.load(new ResourceLocation(SBSAT.RESOURCES, "battery_defective.png"));
    public static final Sprite batteryEmpty       = Sprite.load(new ResourceLocation(SBSAT.RESOURCES, "battery_empty.png"));
    public static final Sprite batteryUnavailable = Sprite.load(new ResourceLocation(SBSAT.RESOURCES, "battery_unavailable.png"));
    public static final Sprite batteryUnspecified = Sprite.load(new ResourceLocation(SBSAT.RESOURCES, "battery_unspecified.png"));
    public static final Sprite batteryWarning     = Sprite.load(new ResourceLocation(SBSAT.RESOURCES, "battery_warning.png"));
    public static final Sprite cellCharging       = Sprite.load(new ResourceLocation(SBSAT.RESOURCES, "cell_charging.png"));
    public static final Sprite cellDefective      = Sprite.load(new ResourceLocation(SBSAT.RESOURCES, "cell_defective.png"));
    public static final Sprite cellEmpty          = Sprite.load(new ResourceLocation(SBSAT.RESOURCES, "cell_empty.png"));
    public static final Sprite cellUnavailable    = Sprite.load(new ResourceLocation(SBSAT.RESOURCES, "cell_unavailable.png"));
    public static final Sprite cellUnspecified    = Sprite.load(new ResourceLocation(SBSAT.RESOURCES, "cell_unspecified.png"));
    public static final Sprite cellWarning        = Sprite.load(new ResourceLocation(SBSAT.RESOURCES, "cell_warning.png"));
}