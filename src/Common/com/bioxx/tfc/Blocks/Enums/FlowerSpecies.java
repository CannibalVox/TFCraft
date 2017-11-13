package com.bioxx.tfc.Blocks.Enums;

import net.minecraft.util.IStringSerializable;

public enum FlowerSpecies implements IStringSerializable {

    DANDELION("dandelion"),
    NASTURTIUM("nasturtium"),
    MEADS_MILKWEED("meads_milkweed"),
    TROPICAL_MILKWEED("tropical_milkweed"),
    BUTTERFLY_MILKWEED("butterfly_milkweed"),
    CALENDULA("calendula"),
    ROSE("rose"),
    BLUE_ORCHID("blue_orchid"),
    ALLIUM("allium"),
    HOUSTONIA("houstonia"),
    TULIP_RED("tulip_red"),
    TULIP_ORANGE("tulip_orange"),
    TULIP_WHITE("tulip_white"),
    TULIP_PINK("tulip_pink"),
    OXEYE_DAISY("oxeye_daisy");

    private String name;

    FlowerSpecies(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
