package com.bioxx.tfc.Blocks.Enums;

import com.bioxx.tfc.api.Constant.ITiered;
import com.bioxx.tfc.api.Constant.MaterialTier;
import net.minecraft.util.IStringSerializable;

public enum AnvilMaterial implements IStringSerializable, ITiered
{
    STONE("Stone", MaterialTier.STONE),
    COPPER("Copper", MaterialTier.SOFT),
    BRONZE("Bronze", MaterialTier.BRONZE),
    WROUGHTIRON("Wrought Iron", MaterialTier.IRON),
    STEEL("Steel", MaterialTier.STEEL),
    BLACKSTEEL("Black Steel", MaterialTier.EXPERT),
    REDSTEEL("Red Steel", MaterialTier.ADVANCED),
    BLUESTEEL("Blue Steel", MaterialTier.ADVANCED),
    BISMUTHBRONZE("Bismuth Bronze", MaterialTier.BRONZE),
    BLACKBRONZE("Black Bronze", MaterialTier.BRONZE),
    ROSEGOLD("Rose Gold", MaterialTier.BRONZE);

    private final MaterialTier tier;
    private final String name;

    AnvilMaterial(String name, MaterialTier tier)
    {
        this.name = name;
        this.tier = tier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MaterialTier getTier() {
        return tier;
    }

    public boolean matches(int tier)
    {
		return tier >= tier;
    }
    public boolean matches(AnvilMaterial req)
    {
		return req.tier.ordinal() >= tier.ordinal();
    }
    public static boolean matches(int i, int j)
    {
		return j >= i;
    }
}
