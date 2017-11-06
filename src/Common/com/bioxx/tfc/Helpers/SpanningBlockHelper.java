package com.bioxx.tfc.Helpers;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

import java.lang.reflect.Array;

public class SpanningBlockHelper {
    public static <T extends Enum<T>, B extends Block> B[] initBlockArray(Class<T> enumClass, Class<B> blockClass, int count) {
        int permutations = enumClass.getEnumConstants().length;
        int indices = (permutations/count) + (permutations%count) != 0?1:0;
        return (B[]) Array.newInstance(blockClass, indices);
    }

    public static <T extends Enum<T> & IStringSerializable> IProperty<T> spanEnum(String name, Class<T> c, int offset, int count) {
        return PropertyEnum.create(name, c, listEnum(c, offset, count));
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
