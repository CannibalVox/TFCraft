package com.bioxx.tfc.Helpers;

import com.bioxx.tfc.CommonProxy;
import com.google.common.base.Throwables;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.lang.reflect.Array;
import java.util.Collection;

public class SpanningBlockHelper {
    public static <T extends Enum<T>, B extends Block> B[] initBlockArray(Class<T> enumClass, Class<B> blockClass, int count) {
        int permutations = enumClass.getEnumConstants().length;
        int indices = (permutations/count) + (permutations%count) != 0?1:0;
        return (B[]) Array.newInstance(blockClass, indices);
    }

    public static <T extends Enum<T> & IStringSerializable> IProperty<T> injectSpanEnum(Block block, String name, Class<T> c, int offset, int count) {
        IProperty<T> newProperty = PropertyEnum.create(name, c, listEnum(c, offset, count));
        BlockStateContainer container = block.getBlockState();

        BlockStateContainer.Builder builder = new BlockStateContainer.Builder(block);
        builder.add(newProperty);
        for (IProperty<?> property : container.getProperties()) {
            builder.add(property);
        }

        if (container instanceof IExtendedBlockState) {
            IExtendedBlockState extended = (IExtendedBlockState)container;
            for (IUnlistedProperty<?> unlistedProperty : extended.getUnlistedNames()) {
                builder.add(unlistedProperty);
            }
        }

        BlockStateContainer newContainer = builder.build();

        try {
            CommonProxy.FIELD_BLOCK_STATE.set(block, newContainer);
        } catch (Throwable e) {
            Throwables.propagate(e);
        }

        return newProperty;
    }

    public static <T extends Enum<T>> T[] listEnum(Class<T> c, int offset, int count) {
        int start = offset*count;
        int end = start+count;
        T[] values = c.getEnumConstants();

        start = Math.min(Math.max(start, 0), values.length-1);
        end = Math.min(Math.max(end, 0), values.length);
        count = end-start;

        if (count < 1)
            return null;

        T[] array = (T[])new Object[count];
        for (int i = 0; i < count; i++) {
            array[i] = values[i+start];
        }

        return array;
    }

    public static <T extends Enum<T>> T spanFirst(Class<T> c, int offset, int count) {
        int start = offset*count;
        T[] values = c.getEnumConstants();

        start = Math.min(Math.max(start, 0), values.length-1);
        return values[start];
    }

    public static <T extends Enum<T>> T spanFromMeta(Class<T> c, int meta, int offset, int count) {
        int index = offset*count + meta;
        T[] values = c.getEnumConstants();
        if (index < 0)
            index = 0;
        if (index >= values.length)
            index = values.length-1;
        return values[index];
    }

    public static <T extends Enum<T>> int metaFromValue(T value, int offset, int count) {
        int index = value.ordinal();
        int skipValue = offset*count;
        if (index <= skipValue)
            return 0;

        index -= skipValue;
        if (index >= count)
            return count-1;
        return index;
    }
}
