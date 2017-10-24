package com.bioxx.tfc.TileEntities;

import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;

public class TEPartial extends NetworkTileEntity
{
	public short typeID = -1;
	public byte metaID;
	public byte material;
	public long extraData;

	public TEPartial()
	{
	}

	@Override
	public boolean canUpdate()
	{
		return false;
	}

	public Material getMaterial()
	{
		switch(material)
		{
		case 1:
			return Material.GROUND;
		case 2:
			return Material.WOOD;
		case 3:
			return Material.ROCK;
		case 4:
			return Material.IRON;
		case 5:
			return Material.WATER;
		case 6:
			return Material.LAVA;
		case 7:
			return Material.LEAVES;
		case 8:
			return Material.PLANTS;
		case 9:
			return Material.VINE;
		case 10:
			return Material.SPONGE;
		case 11:
			return Material.CLOTH;
		case 12:
			return Material.FIRE;
		case 13:
			return Material.SAND;
		case 14:
			return Material.CIRCUITS;
		case 15:
			return Material.GLASS;
		case 16:
			return Material.REDSTONE_LIGHT;
		case 17:
			return Material.TNT;
		case 19:
			return Material.ICE;
		case 20:
			return Material.SNOW;
		case 21:
			return Material.CRAFTED_SNOW;
		case 22:
			return Material.CACTUS;
		case 23:
			return Material.CLAY;
		case 24:
			return Material.GOURD;
		case 25:
			return Material.DRAGON_EGG;
		case 26:
			return Material.PORTAL;
		case 27:
			return Material.CAKE;
		case 28:
			return Material.WEB;
		case 29:
			return Material.PISTON;
		default:
			return Material.GRASS;
		}
	}

	public void setMaterial(Material mat)
	{
		if(mat == Material.GROUND) {material = 1;}
		else if (mat == Material.WOOD)
		{
			material = 2;
		}
		else if (mat == Material.ROCK)
		{
			material = 3;
		}
		else if (mat == Material.IRON)
		{
			material = 4;
		}
		else if (mat == Material.WATER)
		{
			material = 5;
		}
		else if (mat == Material.LAVA)
		{
			material = 6;
		}
		else if (mat == Material.LEAVES)
		{
			material = 7;
		}
		else if (mat == Material.PLANTS)
		{
			material = 8;
		}
		else if (mat == Material.VINE)
		{
			material = 9;
		}
		else if (mat == Material.SPONGE)
		{
			material = 10;
		}
		else if (mat == Material.CLOTH)
		{
			material = 11;
		}
		else if (mat == Material.FIRE)
		{
			material = 12;
		}
		else if (mat == Material.SAND)
		{
			material = 13;
		}
		else if (mat == Material.CIRCUITS)
		{
			material = 14;
		}
		else if (mat == Material.GLASS)
		{
			material = 15;
		}
		else if (mat == Material.REDSTONE_LIGHT)
		{
			material = 16;
		}
		else if (mat == Material.TNT)
		{
			material = 17;
		}
		else if (mat == Material.ICE)
		{
			material = 19;
		}
		else if (mat == Material.SNOW)
		{
			material = 20;
		}
		else if (mat == Material.CRAFTED_SNOW)
		{
			material = 21;
		}
		else if (mat == Material.CACTUS)
		{
			material = 22;
		}
		else if (mat == Material.CLAY)
		{
			material = 23;
		}
		else if (mat == Material.GOURD)
		{
			material = 24;
		}
		else if (mat == Material.DRAGON_EGG)
		{
			material = 25;
		}
		else if (mat == Material.PORTAL)
		{
			material = 26;
		}
		else if (mat == Material.CAKE)
		{
			material = 27;
		}
		else if (mat == Material.WEB)
		{
			material = 28;
		}
		else if (mat == Material.PISTON)
		{
			material = 29;
		}
		else if (mat == Material.GRASS)
		{
			material = 0;
		}
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		metaID = par1NBTTagCompound.getByte("metaID");
		typeID = par1NBTTagCompound.getShort("typeID");
		material = par1NBTTagCompound.getByte("material");
		extraData = par1NBTTagCompound.getLong("extraData");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setShort("typeID", typeID);
		par1NBTTagCompound.setByte("metaID", metaID);
		par1NBTTagCompound.setByte("material", material);
		par1NBTTagCompound.setLong("extraData", extraData);
	}

	@Override
	public void handleInitPacket(NBTTagCompound nbt)
	{
		metaID = nbt.getByte("metaID");
		typeID = nbt.getShort("typeID");
		material = nbt.getByte("material");
		extraData = nbt.getLong("extraData");
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void handleDataPacket(NBTTagCompound nbt)
	{
		extraData = nbt.getLong("extraData");
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void createDataNBT(NBTTagCompound nbt)
	{
		nbt.setLong("extraData", extraData);
	}

	@Override
	public void createInitNBT(NBTTagCompound nbt)
	{
		nbt.setShort("typeID", typeID);
		nbt.setByte("metaID", metaID);
		nbt.setByte("material", material);
		nbt.setLong("extraData", extraData);
	}

}
