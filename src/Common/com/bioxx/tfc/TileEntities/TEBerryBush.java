package com.bioxx.tfc.TileEntities;

import net.minecraft.nbt.NBTTagCompound;

public class TEBerryBush extends NetworkTileEntity
{
	public int dayHarvested = -1000;
	public int dayFruited = -1000;

	public TEBerryBush()
	{
	}

	@Override
	public boolean canUpdate()
	{
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		dayHarvested = nbt.getInteger("dayHarvested");
		dayFruited = nbt.getInteger("dayFruited");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		NBTTagCompound tag = super.writeToNBT(nbt);
		tag.setInteger("dayHarvested", dayHarvested);
		tag.setInteger("dayFruited", dayFruited);
		return tag;
	}

	@Override
	public void handleInitPacket(NBTTagCompound nbt) {
	}

	@Override
	public void handleDataPacket(NBTTagCompound nbt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createDataNBT(NBTTagCompound nbt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createInitNBT(NBTTagCompound nbt) {
	}
}
