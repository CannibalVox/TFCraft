package com.bioxx.tfc.Blocks.Enums;

import net.minecraft.util.IStringSerializable;

public enum FruitTreeSpecies implements IStringSerializable {
    RED_APPLE("Red Apple"),
    GREEN_APPLE("Green Apple"),
    BANANA("Banana"),
    ORANGE("Orange"),
    LEMON("Lemon"),
    OLIVE("Olive"),
    CHERRY("Cherry"),
    PEACH("Peach"),
    PLUM("Plum");

    private String name;
    FruitTreeSpecies(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
