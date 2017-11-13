package com.bioxx.tfc.Blocks.Flora;

import java.util.List;
import java.util.Random;

import com.bioxx.tfc.Blocks.Enums.FlowerSpecies;
import com.bioxx.tfc.Helpers.SpanningBlockHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.bioxx.tfc.Blocks.BlockTerra;
import com.bioxx.tfc.Core.TFCTabs;
import com.bioxx.tfc.Core.TFC_Climate;
import com.bioxx.tfc.Core.TFC_Core;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockFlower extends BlockTerra
{
    private static final IProperty<FlowerSpecies> PROP_SPECIES = PropertyEnum.create("species", FlowerSpecies.class);
    private static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0.3f, 0f, 0.3f, 0.7f, 0.6f, 0.7f);

	public BlockFlower()
	{
		super(Material.PLANTS);
		this.setTickRandomly(true);
		this.setCreativeTab(TFCTabs.TFC_DECORATION);
		this.setDefaultState(getBlockState().getBaseState().withProperty(PROP_SPECIES, FlowerSpecies.DANDELION));
	}

    @Override
    protected BlockStateContainer createBlockState() {
	    return new BlockStateContainer(this, PROP_SPECIES);
    }

    @SideOnly(Side.CLIENT)
    @Override
    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (FlowerSpecies species : FlowerSpecies.values()) {
            items.add(new ItemStack(this, 1, species.ordinal()));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        FlowerSpecies species = SpanningBlockHelper.spanFromMeta(FlowerSpecies.class, meta, 0, 0);
        return getBlockState().getBaseState().withProperty(PROP_SPECIES, species);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
        return BOUNDS;
    }

	public boolean canGrowConditions(World world, BlockPos pos, int flowerMeta)
	{
		float evt = TFC_Climate.getCacheManager(world).getEVTLayerAt(pos.getX(), pos.getZ()).floatdata1;
		BlockPos highUp = pos.up(144-pos.getY());
		float rain = TFC_Climate.getRainfall(world, highUp);
		float bioTemperature =TFC_Climate.getBioTemperatureHeight(world, pos) ;
		if(flowerMeta == 3 && bioTemperature > 20 && rain > 500 && evt < 2)
		{
			return true;
		}
		else if(bioTemperature > 5 && rain > 250)
			return true;
		return false;
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return state.getValue(PROP_SPECIES).ordinal();
	}

	/**
	 * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
	 */
	public boolean canBlockStay(World world, BlockPos pos)
	{
		return (world.getLight(pos) >= 8 || world.canSeeSky(pos)) && this.canThisPlantGrowOnThisBlock(world.getBlockState(pos.down()));
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos)
	{
		IBlockState block = world.getBlockState(pos);
		return (world.isAirBlock(pos) || block.getMaterial().isReplaceable()) && this.canThisPlantGrowOnThisBlock(block);
	}

	protected boolean canThisPlantGrowOnThisBlock(IBlockState block)
	{
		return TFC_Core.isSoil(block) || TFC_Core.isFarmland(block);
	}

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_)
    {
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		this.checkAndDropBlock(world, pos);
	}

	@Override
	public void onNeighborChange(IBlockAccess access, BlockPos pos, BlockPos neighbor)
	{
	    if (access instanceof World)
		    this.checkAndDropBlock((World)access, pos);
	}

	protected void checkAndDropBlock(World world, BlockPos pos)
	{
		if (!this.canBlockStay(world, pos))
		{
			world.destroyBlock(pos, true);
		}
	}
}
