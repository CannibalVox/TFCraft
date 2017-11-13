package com.bioxx.tfc.Blocks.Flora;

import java.util.List;
import java.util.Random;

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
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.bioxx.tfc.Blocks.BlockTerra;
import com.bioxx.tfc.Core.TFCTabs;
import com.bioxx.tfc.Core.TFC_Core;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockFlora extends BlockTerra 
{
	private enum FloraSpecies implements IStringSerializable {
	    GOLDENROD("Golden Rod"),
        CATTAILS("Cat Tails");

	    private String name;

	    FloraSpecies(String name) {
	        this.name = name;
        }

        public String getName() {
	        return name;
        }
    }

    private static final IProperty<FloraSpecies> PROP_SPECIES = PropertyEnum.create("species", FloraSpecies.class);

    private static final AxisAlignedBB BLOCK_BOUNDS = new AxisAlignedBB(0.3f, 0.0f, 0.3f, 0.7f, 0.7f, 0.7f);

	public BlockFlora()
	{
		super(Material.PLANTS);
		this.setCreativeTab(TFCTabs.TFC_DECORATION);
		this.setDefaultState(getBlockState().getBaseState().withProperty(PROP_SPECIES, FloraSpecies.GOLDENROD));
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
        for (FloraSpecies species : FloraSpecies.values()) {
            items.add(new ItemStack(this, 1, species.ordinal()));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        FloraSpecies species = SpanningBlockHelper.spanFromMeta(FloraSpecies.class, meta, 0, 0);
        return getBlockState().getBaseState().withProperty(PROP_SPECIES, species);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        FloraSpecies species = state.getValue(PROP_SPECIES);
        return species.ordinal();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
        return BLOCK_BOUNDS;
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

	public boolean canBlockStay(World world, BlockPos pos)
	{
		return (world.getLight(pos) >= 8 ||
				world.canSeeSky(pos)) &&
				this.canThisPlantGrowOnThisBlock(world.getBlockState(pos.down()));
	}

	@Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
	    super.updateTick(world, pos, state, rand);

	    if (!canBlockStay(world, pos))
	        world.destroyBlock(pos, true);
    }

	@Override
    public void onNeighborChange(IBlockAccess access, BlockPos pos, BlockPos neighbor)
	{
		super.onNeighborChange(access, pos, neighbor);
		if (access instanceof World) {
		    World world = (World)access;
            if(!canBlockStay(world, pos))
                world.destroyBlock(pos, true);
        }
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos)
	{
		IBlockState block = world.getBlockState(pos);
		return (world.isAirBlock(pos) || block.getMaterial().isReplaceable()) && this.canThisPlantGrowOnThisBlock(world.getBlockState(pos.down()));
	}

	protected boolean canThisPlantGrowOnThisBlock(IBlockState block)
	{
		return TFC_Core.isSoil(block) || TFC_Core.isFarmland(block);
	}

}
