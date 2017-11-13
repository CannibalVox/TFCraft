package com.bioxx.tfc.Blocks.Flora;

import java.util.List;
import java.util.Random;

import com.bioxx.tfc.Blocks.Enums.FruitTreeSpecies;
import com.bioxx.tfc.Helpers.SpanningBlockHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.bioxx.tfc.Blocks.BlockTerraContainer;
import com.bioxx.tfc.Core.TFC_Climate;
import com.bioxx.tfc.Core.TFC_Time;
import com.bioxx.tfc.Food.FloraIndex;
import com.bioxx.tfc.Food.FloraManager;
import com.bioxx.tfc.Food.ItemFoodTFC;
import com.bioxx.tfc.TileEntities.TEFruitLeaves;
import com.bioxx.tfc.api.TFCBlocks;
import com.bioxx.tfc.api.Constant.Global;
import com.bioxx.tfc.api.Util.Helper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockFruitLeaves extends BlockTerraContainer
{
    private static final PropertyBool PROP_FRUIT = PropertyBool.create("fruit");
    private static final String PROP_SPECIES_NAME = "species";
    private IProperty<FruitTreeSpecies> PROP_SPECIES;

    private enum FloweringState implements IStringSerializable {
        EMPTY("empty"),
        FRUIT("fruit"),
        FLOWERING("flowering");

        private String name;
        FloweringState (String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    private static final IProperty<FloweringState> PROP_FLOWERING = PropertyEnum.create("flowering", FloweringState.class);
    private static final int FRUITS_PER_BLOCK = 8;

	private int offset;

	public BlockFruitLeaves(int offset)
	{
		super(Material.LEAVES);
		this.setTickRandomly(true);
		this.offset = offset;
        PROP_SPECIES = SpanningBlockHelper.injectSpanEnum(this, PROP_SPECIES_NAME, FruitTreeSpecies.class, this.offset, FRUITS_PER_BLOCK);
        setDefaultState(getBlockState().getBaseState().withProperty(PROP_SPECIES, FruitTreeSpecies.BANANA)
            .withProperty(PROP_FRUIT, false).withProperty(PROP_FLOWERING, FloweringState.EMPTY));
	}

    @Override
    protected BlockStateContainer createBlockState() {
	    return new BlockStateContainer(this, PROP_FRUIT, PROP_FLOWERING);
    }

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state)
	{
		return false;
	}

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_)
    {
    }

    @SideOnly(Side.CLIENT)
    @Override
    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
    {
        for (FruitTreeSpecies species : FruitTreeSpecies.values()) {
            items.add(new ItemStack(this, 1, species.ordinal()));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean hasFruit = (meta & 8) != 0;
        FruitTreeSpecies species = SpanningBlockHelper.spanFromMeta(FruitTreeSpecies.class, meta, this.offset, FRUITS_PER_BLOCK);
        return getBlockState().getBaseState().withProperty(PROP_SPECIES, species).withProperty(PROP_FRUIT, hasFruit);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        boolean hasFruit = state.getValue(PROP_FRUIT);
        FruitTreeSpecies species = state.getValue(PROP_SPECIES);

        int meta = SpanningBlockHelper.metaFromValue(species, this.offset, FRUITS_PER_BLOCK);
        if (hasFruit)
            meta |= 8;

        return meta;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if (state.getValue(PROP_FRUIT))
            return state.withProperty(PROP_FLOWERING, FloweringState.FRUIT);

        FruitTreeSpecies species = state.getValue(PROP_SPECIES);
        FloraManager manager = FloraManager.getInstance();
        FloraIndex index = manager.findMatchingIndex(species.getName());
        if(index != null)
        {
            if(index.inBloom(TFC_Time.getSeasonAdjustedMonth(pos.getZ())))//blooming
                return state.withProperty(PROP_FLOWERING, FloweringState.FLOWERING);
        }

        return state.withProperty(PROP_FLOWERING, FloweringState.EMPTY);
    }

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		this.lifeCycle(world, pos, state);
	}

	private void lifeCycle(World world, BlockPos pos, IBlockState state)
	{
		if(!world.isRemote)
		{
			if (!canStay(world, pos))
			{
				destroyLeaves(world, pos);
				return;
			}

			Random rand = new Random();
			FruitTreeSpecies species = state.getValue(PROP_SPECIES);
			boolean hasFruit = state.getValue(PROP_FRUIT);

			FloraManager manager = FloraManager.getInstance();
			FloraIndex fi = manager.findMatchingIndex(species.getName());

			float temp = TFC_Climate.getHeightAdjustedTemp(world, pos);
			TEFruitLeaves te = (TEFruitLeaves) world.getTileEntity(pos);
			if(te != null && fi != null)
			{
			    if (hasFruit) {
			        if (!fi.inHarvest(TFC_Time.getSeasonAdjustedMonth(pos.getZ())) ||
                            (rand.nextInt(10) == 0 && (temp < fi.minTemp || temp >= fi.maxTemp))) {
			            world.setBlockState(pos, state.withProperty(PROP_FRUIT, false));
                    }
                } else {
			        if (fi.inHarvest(TFC_Time.getSeasonAdjustedMonth(pos.getZ())) &&
                            TFC_Time.getMonthsSinceDay(te.dayHarvested) > 1 &&
                            temp >= fi.minTemp && temp < fi.maxTemp) {

			            te.dayFruited = TFC_Time.getTotalDays();
			            world.setBlockState(pos, state.withProperty(PROP_FRUIT, true));
                    }
                }
			}
		}
	}

	public static boolean canStay(World world, BlockPos pos)
	{
		//Only leaf blocks that are within one block and on the same level or 1 above a branch or the top of the trunk
		for (int i = 1; i >= -1; i--)
		{
			for (int j = 0; j >= -1; j--)
			{
				for (int k = 1; k >= -1; k--)
				{
				    BlockPos branchPos = pos.east(i).south(k).up(j);
					if (world.getBlockState(branchPos).getBlock() == TFCBlocks.fruitTreeWood &&
							world.getBlockState(branchPos.up()).getBlock() != TFCBlocks.fruitTreeWood) // Only branches or the top of the trunk
						return true;
				}
			}
		}

		return false;
	}

	@Override
	public void onNeighborChange(IBlockAccess access, BlockPos pos, BlockPos neighbor)
	{
		super.onNeighborChange(access, pos, neighbor);

		if (access instanceof  World)
		    lifeCycle((World)access, pos, access.getBlockState(pos));
	}

	private void destroyLeaves(World world, BlockPos pos)
	{
		world.setBlockToAir(pos);
	}

	@Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
	{
		// Intentionally Blank
	}

	/* Left-Click Harvest Fruit */
	@Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer entityplayer)
	{
		if (!world.isRemote)
		{
			IBlockState state = world.getBlockState(pos);
			FruitTreeSpecies species = state.getValue(PROP_SPECIES);
			boolean hasFruit = state.getValue(PROP_FRUIT);

			FloraManager manager = FloraManager.getInstance();
			FloraIndex fi = manager.findMatchingIndex(species.getName());

			if (fi != null && (fi.inHarvest(TFC_Time.getSeasonAdjustedMonth(pos.getZ())) || fi.inHarvest((TFC_Time.getSeasonAdjustedMonth(pos.getZ()) + 11) % 12) && hasFruit))
			{
				TEFruitLeaves te = (TEFruitLeaves) world.getTileEntity(pos);
				if (te != null)
				{
					te.dayHarvested = TFC_Time.getTotalDays();

					world.setBlockState(pos, state.withProperty(PROP_FRUIT, false), 3);
                    spawnAsEntity(world, pos, ItemFoodTFC.createTag(fi.getOutput(), Helper.roundNumber(4 + (world.rand.nextFloat() * 12), 10)));
				}
			}
		}
	}

	/* Right-Click Harvest Fruit */
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!world.isRemote)
		{
		    FruitTreeSpecies species = state.getValue(PROP_SPECIES);
		    boolean hasFruit = state.getValue(PROP_FRUIT);

			FloraManager manager = FloraManager.getInstance();
			FloraIndex fi = manager.findMatchingIndex(species.getName());

			if (fi != null && (fi.inHarvest(TFC_Time.getSeasonAdjustedMonth(pos.getZ())) || fi.inHarvest((TFC_Time.getSeasonAdjustedMonth(pos.getZ()) + 11) % 12) && hasFruit))
			{
				TEFruitLeaves te = (TEFruitLeaves) world.getTileEntity(pos);
				if(te != null)
				{
					te.dayHarvested = TFC_Time.getTotalDays();
					world.setBlockState(pos, state.withProperty(PROP_FRUIT, false), 3);
					spawnAsEntity(world, pos, ItemFoodTFC.createTag(fi.getOutput(), Helper.roundNumber(4 + (world.rand.nextFloat() * 12), 10)));
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TEFruitLeaves();
	}
}
