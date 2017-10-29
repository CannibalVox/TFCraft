package com.bioxx.tfc.api.Crafting;

import java.util.Stack;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class BarrelLiquidToLiquidRecipe extends BarrelRecipe
{
	public FluidStack inputfluid;
	public BarrelLiquidToLiquidRecipe(FluidStack fluidInBarrel, FluidStack inputfluid, FluidStack outputFluid)
	{
		super(null, fluidInBarrel, null, outputFluid);
		this.inputfluid = inputfluid;
	}

	@Override
	public Boolean matches(ItemStack item, FluidStack fluid)
	{
		FluidStack itemLiquid = getSingleFluidStack(item);

		if(recipeFluid != null && recipeFluid.isFluidEqual(fluid) && itemLiquid != null && itemLiquid.isFluidEqual(inputfluid))
		{
			//Make sure that when we combine the liquids that there is enough room in the barrel for the new liquid to fit
			if(10000-fluid.amount < itemLiquid.amount)
				return false;

			return true;
		}
		return false;
	}

	@Override
	public Stack<ItemStack> getResult(ItemStack inIS, FluidStack inFS, int sealedTime)
	{
		Stack<ItemStack> result = new Stack<ItemStack>();
		if(inIS != null)
			result.push(inIS.getItem().getContainerItem(inIS));
		else
			result.push(null);

		return result;
	}

	@Override
	public FluidStack getResultFluid(ItemStack inIS, FluidStack inFS, int sealedTime)
	{
		if(recipeOutFluid != null)
		{
			FluidStack fs = recipeOutFluid.copy();
			FluidStack itemLiquid = getSingleFluidStack(inIS);
			if(!removesLiquid)
			{
				fs.amount = inFS.amount+itemLiquid.amount;
			}
			else
			{
				fs.amount = ( fs.amount * inFS.amount ) / recipeFluid.amount;
			}
			return fs;
		}
		return null;
	}

	public FluidStack getInputfluid()
	{
		return inputfluid;
	}
	private FluidStack getSingleFluidStack(ItemStack item) {
	    IFluidHandlerItem capability = item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
	    if (capability == null)
	        return null;

	    if (capability.getTankProperties().length != 1)
	        return null;

	    return capability.getTankProperties()[0].getContents();
	}
}
