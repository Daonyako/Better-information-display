package com.example.examplemod;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.DoubleValue BOX_WIDTH = BUILDER.defineInRange("boxWidth", 80.0, 10.0, 400.0);
    public static final ForgeConfigSpec.DoubleValue BOX_HEIGHT = BUILDER.defineInRange("boxHeight", 40.0, 10.0, 400.0);
    public static final ForgeConfigSpec.BooleanValue SHOW_EFFECTS = BUILDER.define("showEffects", true);
    public static final ForgeConfigSpec.DoubleValue OFFSET_X = BUILDER.defineInRange("offsetX", 0.8, -2.0, 2.0);
    public static final ForgeConfigSpec.DoubleValue OFFSET_Y = BUILDER.defineInRange("offsetY", 0.35, -2.0, 2.0);
    public static final ForgeConfigSpec.DoubleValue OFFSET_Z = BUILDER.defineInRange("offsetZ", 0.0, -5.0, 5.0);
    public static final ForgeConfigSpec.BooleanValue SHOW_HP = BUILDER.define("showHp", true);
    public static final ForgeConfigSpec.BooleanValue SHOW_HELD = BUILDER.define("showHeldItem", true);
    public static final ForgeConfigSpec.DoubleValue SCALE = BUILDER.defineInRange("scale", 0.03, 0.005, 0.1);
    public static final ForgeConfigSpec.DoubleValue Z_BIAS = BUILDER.defineInRange("zBias", -0.02, -0.5, 0.5);
    public static final ForgeConfigSpec.IntValue TEXT_COLOR = BUILDER.defineInRange("textColorARGB", 0xFFFFFFFF, Integer.MIN_VALUE, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue BORDER_COLOR = BUILDER.defineInRange("borderColorARGB", 0xFFFFFFFF, Integer.MIN_VALUE, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue BACKGROUND_COLOR = BUILDER.defineInRange("backgroundColorARGB", 0x66000000, Integer.MIN_VALUE, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.BooleanValue BACKGROUND_ENABLED = BUILDER.define("backgroundEnabled", true);
    public static final ForgeConfigSpec.BooleanValue USE_SEE_THROUGH = BUILDER.define("useSeeThrough", true);
    public static final ForgeConfigSpec.BooleanValue SHADOW = BUILDER.define("shadow", true);
    public static final ForgeConfigSpec SPEC = BUILDER.build();
}
