package com.bioxx.tfc.Blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockTerraContainer extends BlockContainer
{
	public BlockTerraContainer()
	{
		super(Material.ROCK);
	}

	public BlockTerraContainer(Material material)
	{
		super(material);
	}

	@Override
	public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2)
	{
		return null;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		TileEntity te = worldIn.getTileEntity(pos);
		if(te != null)
		{
			if(te instanceof IInventory)
			{
				for(int i = 0; i< ((IInventory)te).getSizeInventory(); i++)
				{
					if(((IInventory)te).getStackInSlot(i) != null)
					{
						EntityItem ei = new EntityItem(worldIn, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, ((IInventory)te).getStackInSlot(i));
						ei.motionX = 0;
						ei.motionY = 0;
						ei.motionZ = 0;
						worldIn.spawnEntity(ei);
					}
				}
			}
		}
		super.breakBlock(worldIn, pos, state);
	}
}
