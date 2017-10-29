package com.bioxx.tfc.api.Util;

import java.lang.reflect.Field;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import com.bioxx.tfc.TerraFirmaCraft;
import com.bioxx.tfc.api.Enums.EnumItemReach;
import com.bioxx.tfc.api.Interfaces.ISize;


public class Helper {

	public static double getReachDistance(World par1World, EntityLivingBase entity, EnumHand hand, boolean par3)
	{
		double var21 = 1; /*ModLoader.getMinecraftInstance().playerController.getBlockReachDistance()*/
		if(entity.getHeldItem(hand)!=null && (entity.getHeldItem(hand).getItem()) instanceof ISize){
			var21 *= ((ISize)(entity.getHeldItem(hand).getItem())).getReach(null).multiplier;
		}

		else{
			var21 *= EnumItemReach.SHORT.multiplier;
		}
		return var21;
	}

	/**
	 * @return Returns an integer equal to the byte value of all chars in the passed string added together.
	 */
	public static int stringToInt(String s)
	{
		int result = 0;
		for(char c : s.toCharArray())
		{
			result += (byte)c;
		}
		return result;
	}
	
	/**
	 * Returns the value of the first parameter, clamped to be within the lower and upper limits given by the second and
	 * third parameters
	 */
	public static float clampFloat(float par0, float par1, float par2)
	{
		return par0 < par1 ? par1 : par0 > par2 ? par2 : par0;
	}

	public static float roundNumber(float input, float rounding)
	{
		int o = (int)(input * rounding);
		return o / rounding;
	}

	private static boolean usesSRG(Object obj, String srgName)
	{
		Field[] fields = obj.getClass().getFields();
		for(Field f : fields)
		{
			if(f.getName().equals(srgName))
				return true;
		}
		return false;
	}

	public static int getInteger(Object obj, String srgName, String obfName, String deobfName, boolean useDeobf)
	{
		Field f = null;
		try 
		{
			if(!useDeobf)
				f = obj.getClass().getDeclaredField(deobfName);
			else if(usesSRG(obj, srgName))
				f = obj.getClass().getDeclaredField(srgName);
			else
				f = obj.getClass().getDeclaredField(obfName);
			f.setAccessible(true);
			return (Integer) f.get(obj);
		} catch (NoSuchFieldException e) {
			TerraFirmaCraft.LOG.catching(e);
		} catch (SecurityException e) {
			TerraFirmaCraft.LOG.catching(e);
		} catch (IllegalArgumentException e) {
			TerraFirmaCraft.LOG.catching(e);
		} catch (IllegalAccessException e) {
			TerraFirmaCraft.LOG.catching(e);
		}
		return 0; 		
	}

	public static boolean getBoolean(Object obj, String srgName, String obfName, String deobfName, boolean useDeobf)
	{
		Field f = null;
		try 
		{
			if(!useDeobf)
				f = obj.getClass().getDeclaredField(deobfName);
			else if(usesSRG(obj, srgName))
				f = obj.getClass().getDeclaredField(srgName);
			else
				f = obj.getClass().getDeclaredField(obfName);
			f.setAccessible(true);
			return (Boolean) f.get(obj);
		} catch (NoSuchFieldException e) {
			TerraFirmaCraft.LOG.catching(e);
		} catch (SecurityException e) {
			TerraFirmaCraft.LOG.catching(e);
		} catch (IllegalArgumentException e) {
			TerraFirmaCraft.LOG.catching(e);
		} catch (IllegalAccessException e) {
			TerraFirmaCraft.LOG.catching(e);
		}
		return false; 		
	}

	public static Object getObject(Object obj, String srgName, String obfName, String deobfName, boolean useDeobf)
	{
		Field f = null;
		try 
		{
			if(!useDeobf)
				f = obj.getClass().getDeclaredField(deobfName);
			else if(usesSRG(obj, srgName))
				f = obj.getClass().getDeclaredField(srgName);
			else
				f = obj.getClass().getDeclaredField(obfName);
			f.setAccessible(true);
			return f.get(obj);
		} catch (NoSuchFieldException e) {
			TerraFirmaCraft.LOG.catching(e);
		} catch (SecurityException e) {
			TerraFirmaCraft.LOG.catching(e);
		} catch (IllegalArgumentException e) {
			TerraFirmaCraft.LOG.catching(e);
		} catch (IllegalAccessException e) {
			TerraFirmaCraft.LOG.catching(e);
		}
		return null; 		
	}
}
