package com.bioxx.tfc.TileEntities;

import net.minecraft.nbt.NBTTagCompound;

public class TEFruitLeaves extends NetworkTileEntity
{
	public int dayHarvested = -1000;
	public int dayFruited = -1000;

	public TEFruitLeaves()
	{
		this.shouldSendInitData = true;
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
		super.writeToNBT(nbt);
		nbt.setInteger("dayHarvested", dayHarvested);
		nbt.setInteger("dayFruited", dayFruited);
		return nbt;
	}

	@Override
	public void handleInitPacket(NBTTagCompound nbt) {
		world.notifyBlockUpdate(getPos());
	}

	@Override
	public void handleDataPacket(NBTTagCompound nbt) {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void createDataNBT(NBTTagCompound nbt) {
	}

	@Override
	public void createInitNBT(NBTTagCompound nbt) {
	}
}
