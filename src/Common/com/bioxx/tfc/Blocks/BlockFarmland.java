package com.bioxx.tfc.Blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.bioxx.tfc.Reference;
import com.bioxx.tfc.Core.TFCTabs;
import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.TileEntities.TEFarmland;
import com.bioxx.tfc.api.Constant.Global;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFarmland extends BlockContainer
{
	private Block dirtBlock;
	private int textureOffset;

	public BlockFarmland(Block block, int tex)
	{
		super(Material.GROUND);
		this.setTickRandomly(true);
		this.dirtBlock = block;
		this.textureOffset = tex;
		this.setCreativeTab(TFCTabs.TFC_BUILDING);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SideOnly(Side.CLIENT)
	@Override
	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	public void getSubBlocks(Item item, CreativeTabs tabs, List list)
	{
		// Change to false if this block should not be added to the creative tab
		Boolean addToCreative = true;

		if(addToCreative)
		{
			int count;
			if(textureOffset == 0) count = 16;
			else count = Global.STONE_ALL.length - 16;

			for(int i = 0; i < count; i++)
				list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public int damageDropped(int dmg)
	{
		return dmg;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return AxisAlignedBB.getBoundingBox(x + 0, y + 0, z + 0, x + 1, y + 1, z + 1);
	}

	@Override
	public Item getItemDropped(int metadata, Random rand, int fortune)
	{
		return Item.getItemById(0);
	}

	/**
	 * returns true if there's water nearby (x-4 to x+4, y to y+1, k-4 to k+4)
	 */
	public static boolean isFreshWaterNearby(World world, BlockPos pos)
	{
		for (int x = -4; x <= 4; ++x)
		{
			for (int y = 0; y <= 1; ++y)
			{
				for (int z = -4; z <= 4; ++z)
				{
						IBlockState b = world.getBlockState(pos.east(x).up(y).south(z));
						if (TFC_Core.isFreshWater(b))
							return true;
					}
			}
		}
		return false;
	}

	public static boolean isSaltWaterNearby(World world, BlockPos pos)
	{
		for (int x = -4; x <= 4; ++x)
		{
			for (int y = 0; y <= 1; ++y)
			{
				for (int z = -4; z <= 4; ++z)
				{
					IBlockState b = world.getBlockState(pos);
					if (TFC_Core.isSaltWater(b))
						return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TEFarmland();
	}

	@Override
	public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction, IPlantable plantable)
	{
		Block plant = plantable.getPlant(world, x, y + 1, z);
		if (plant == Blocks.pumpkin_stem || plant == Blocks.melon_stem)
			return false;

		EnumPlantType plantType = plantable.getPlantType(world, x, y + 1, z);
		if (plantType == EnumPlantType.Crop)
			return true;

		return super.canSustainPlant(world, x, y, z, direction, plantable);
	}
}
