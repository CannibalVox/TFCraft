package com.bioxx.tfc.Blocks.Enums;

import net.minecraft.util.IStringSerializable;

public enum BerrySpecies implements IStringSerializable {
    WINTERGREEN("Wintergreen"),
    BLUEBERRY("Blueberry"),
    RASPBERRY("Raspberry"),
    STRAWBERRY("Strawberry"),
    BLACKBERRY("Blackberry"),
    BUNCHBERRY("Bunchberry"),
    CRANBERRY("Cranberry"),
    SNOWBERRY("Snowberry"),
    ELDERBERRY("Elderberry"),
    GOOSEBERRY("Gooseberry"),
    CLOUDBERRY("Cloudberry");

    private final String name;

    BerrySpecies(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
