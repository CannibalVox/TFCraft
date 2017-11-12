package com.bioxx.tfc.Blocks.Flora;

import java.util.List;
import java.util.Random;

import com.bioxx.tfc.Blocks.Enums.BerrySpecies;
import com.bioxx.tfc.Helpers.SpanningBlockHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.bioxx.tfc.Blocks.BlockTerraContainer;
import com.bioxx.tfc.Core.TFCTabs;
import com.bioxx.tfc.Core.TFC_Climate;
import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.Core.TFC_Time;
import com.bioxx.tfc.Food.FloraIndex;
import com.bioxx.tfc.Food.FloraManager;
import com.bioxx.tfc.Food.ItemFoodTFC;
import com.bioxx.tfc.TileEntities.TEBerryBush;
import com.bioxx.tfc.api.Util.Helper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import static com.bioxx.tfc.Blocks.Enums.BerrySpecies.CLOUDBERRY;
import static com.bioxx.tfc.Blocks.Enums.BerrySpecies.ELDERBERRY;
import static com.bioxx.tfc.Blocks.Enums.BerrySpecies.GOOSEBERRY;

public class BlockBerryBush extends BlockTerraContainer
{
	public static final String PROP_BERRY_SPECIES = "species";
	public static final PropertyBool PROP_FLOWERING = PropertyBool.create("berry");
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool UP = PropertyBool.create("up");

	public static final int BERRIES_PER_BLOCK=8;

	private int offset;
	private IProperty<BerrySpecies> speciesProperty = null;

	private static final AxisAlignedBB[] ALL_BOUNDING_BOXES = new AxisAlignedBB[352];
	static {
	    //Cache all the bounding boxes for every combination of berry species & connection combinations
	    for (BerrySpecies species : BerrySpecies.values()) {
	        int speciesStart = species.ordinal() * 32;
	        for (int i = 0; i < 32; i++) {
	            ALL_BOUNDING_BOXES[speciesStart+i] = buildBoundingBox(species, i);
            }
        }
    }

    //Build a cached bounding box from the berry type & a boxindex representing a bitfield
    //of connection directions
    // bit 0 = north
    // bit 1 = south
    // bit 2 = east
    // bit 3 = west
    // bit 4 = up
    private static AxisAlignedBB buildBoundingBox(BerrySpecies species, int boxIndex) {
	    boolean north = (boxIndex & 1) != 0;
	    boolean south = (boxIndex & 2) != 0;
	    boolean east = (boxIndex & 4) != 0;
	    boolean west = (boxIndex & 8) != 0;
	    boolean up = (boxIndex & 16) != 0;

        float minX = 0.1f;
        float minZ = 0.1f;
        float maxX = 0.9f;
        float maxZ = 0.9f;
        float maxY = 1f;

        if(west) minX = 0;
        if(east) maxX = 1;
        if(north) minZ = 0;
        if(south) maxZ = 1;

        switch(species)
        {
            case WINTERGREEN:
            {
                maxY = 0.2f;
            }
            case BLUEBERRY:
            {
                maxY = 0.85f;
            }
            case RASPBERRY:
            {
                maxY = 0.85f;
                if(up)
                    maxY = 1;
            }
            case STRAWBERRY:
            {
                maxY = 0.2f;
            }
            case BLACKBERRY:
            {
                maxY = 0.85f;
                if(up)
                    maxY = 1;
            }
            case BUNCHBERRY:
            {
                maxY = 0.2f;
            }
            case CRANBERRY:
            {
                maxY = 0.6f;
            }
            case SNOWBERRY:
            {
                maxY = 0.2f;
            }
            case ELDERBERRY:
            {
                maxY = 0.85f;
                if(up)
                    maxY = 1;
            }
            case GOOSEBERRY:
            {
                maxY = 0.75f;
            }
            case CLOUDBERRY:
            {
                maxY = 0.35f;
            }
        }

        return new AxisAlignedBB(minX, 0, minZ, maxX, maxY, maxZ);
    }

	public BlockBerryBush(int offset)
	{
		super(Material.PLANTS);
		this.setTickRandomly(true);
		this.setCreativeTab(TFCTabs.TFC_DECORATION);
		this.offset = offset;
		this.setDefaultState(getBlockState().getBaseState()
            .withProperty(speciesProperty, SpanningBlockHelper.spanFirst(BerrySpecies.class, offset, BERRIES_PER_BLOCK))
            .withProperty(PROP_FLOWERING, false).withProperty(NORTH, false).withProperty(SOUTH, false)
            .withProperty(EAST, false).withProperty(WEST, false).withProperty(UP, false));
	}

	@Override
    protected BlockStateContainer createBlockState() {
	    speciesProperty = SpanningBlockHelper.spanEnum(PROP_BERRY_SPECIES, BerrySpecies.class, this.offset, BERRIES_PER_BLOCK);
	    return new BlockStateContainer(this, speciesProperty, PROP_FLOWERING, NORTH, SOUTH, WEST, EAST, UP);
    }

	@SideOnly(Side.CLIENT)
	@Override
	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
	{
	    for (BerrySpecies berry : SpanningBlockHelper.listEnum(BerrySpecies.class, this.offset, BERRIES_PER_BLOCK)) {
	        items.add(new ItemStack(this, 1, berry.ordinal()));
        }
	}

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(speciesProperty).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean flowering = (meta % 8) != 0;
        BerrySpecies species = SpanningBlockHelper.spanFromMeta(BerrySpecies.class, meta & 7, this.offset, BERRIES_PER_BLOCK);

        return getBlockState().getBaseState().withProperty(speciesProperty, species).withProperty(PROP_FLOWERING, flowering);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        boolean flowering = state.getValue(PROP_FLOWERING);
        int meta = flowering?8:0;
        BerrySpecies species = state.getValue(speciesProperty);
        return meta | SpanningBlockHelper.metaFromValue(species, this.offset, BERRIES_PER_BLOCK);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        boolean north = isSamePlant(worldIn, pos.north(), state);
        boolean south = isSamePlant(worldIn, pos.south(), state);
        boolean east = isSamePlant(worldIn, pos.east(), state);
        boolean west = isSamePlant(worldIn, pos.west(), state);
        boolean up = isSamePlant(worldIn, pos.up(), state);

        return state.withProperty(NORTH, north).withProperty(SOUTH, south).withProperty(EAST, east)
                .withProperty(WEST, west).withProperty(UP, up);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return getStateFromMeta(meta % BERRIES_PER_BLOCK);
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
    {
        BerrySpecies species = state.getValue(speciesProperty);
        if(species == BerrySpecies.RASPBERRY || species == BerrySpecies.BLACKBERRY)
        {
            if(entity instanceof EntityLivingBase)
                entity.attackEntityFrom(DamageSource.CACTUS, 5);
        }
    }

	@Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
        int connections = 0;
        if (isSamePlant(access, pos.north(), state)) connections |= 1;
        if (isSamePlant(access, pos.south(), state)) connections |= 2;
        if (isSamePlant(access, pos.east(), state)) connections |= 4;
        if (isSamePlant(access, pos.west(), state)) connections |= 8;
        if (isSamePlant(access, pos.up(), state)) connections |= 16;

        BerrySpecies species = state.getValue(speciesProperty);
        int speciesStart = species.ordinal() * 32;
        return ALL_BOUNDING_BOXES[speciesStart+connections];
	}

	private boolean isSamePlant(IBlockAccess bAccess, BlockPos pos, IBlockState state)
	{
	    IBlockState neighborState = bAccess.getBlockState(pos);

	    BerrySpecies species = state.getValue(speciesProperty);
	    BerrySpecies neighborSpecies = neighborState.getValue(speciesProperty);

		return species == neighborSpecies;
	}

	/* Left-Click Harvest Berries */
	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer entityplayer)
	{
		if (!world.isRemote)
		{
		    IBlockState state = world.getBlockState(pos);
		    boolean hasFruit = state.getValue(PROP_FLOWERING);
			FloraManager manager = FloraManager.getInstance();
			FloraIndex fi = manager.findMatchingIndex(getType(state));

			TEBerryBush te = (TEBerryBush) world.getTileEntity(pos);
			if (te != null && hasFruit)
			{
				te.dayHarvested = TFC_Time.getTotalDays();
				world.setBlockState(pos, state.withProperty(PROP_FLOWERING, false));
				spawnAsEntity(world, pos, ItemFoodTFC.createTag(fi.getOutput(), Helper.roundNumber(3 + world.rand.nextFloat() * 5, 10)));
			}
		}
	}

	/* Right-Click Harvest Berries */
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!world.isRemote)
		{
		    boolean hasFruit = state.getValue(PROP_FLOWERING);
			FloraManager manager = FloraManager.getInstance();
			FloraIndex fi = manager.findMatchingIndex(getType(state));

			TEBerryBush te = (TEBerryBush) world.getTileEntity(pos);
			if(te != null && hasFruit)
			{
				te.dayHarvested = TFC_Time.getTotalDays();
				world.setBlockState(pos, state.withProperty(PROP_FLOWERING, false));
				spawnAsEntity(world, pos, ItemFoodTFC.createTag(fi.getOutput(), Helper.roundNumber(3 + world.rand.nextFloat() * 5, 10)));
				return true;
			}
		}
		return false;
	}

	@Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		lifeCycle(world, pos);
	}

	private void lifeCycle(World world, BlockPos pos)
	{
		if(!world.isRemote)
		{
			if(!canBlockStay(world, pos))
			{
				this.dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
				world.setBlockToAir(pos);
				return;
			}

			IBlockState currentState = world.getBlockState(pos);
			boolean currentlyFlowering = currentState.getValue(PROP_FLOWERING);

			TileEntity te = world.getTileEntity(pos);
			TEBerryBush tebb = null;
			if (te instanceof TEBerryBush)
				tebb = (TEBerryBush) world.getTileEntity(pos);
			if(tebb != null)
			{
				FloraIndex floraIndex = FloraManager.getInstance().findMatchingIndex(getType(world.getBlockState(pos)));
				float temp = TFC_Climate.getHeightAdjustedTemp(world, pos);

				if(temp >= floraIndex.minTemp && temp < floraIndex.maxTemp)
				{
					if(!currentlyFlowering && floraIndex.inHarvest(TFC_Time.getSeasonAdjustedMonth(pos.getZ())) && TFC_Time.getMonthsSinceDay(tebb.dayHarvested) > 0)
					{
						tebb.dayFruited = TFC_Time.getTotalDays();
						world.setBlockState(pos, currentState.withProperty(PROP_FLOWERING, true));
					}
				}
				else if(temp < floraIndex.minTemp - 5 || temp > floraIndex.maxTemp + 5)
				{
					if(currentlyFlowering)
					{
						world.setBlockState(pos, currentState.withProperty(PROP_FLOWERING, false));
					}
				}

				if(currentlyFlowering && TFC_Time.getMonthsSinceDay(tebb.dayFruited) > floraIndex.fruitHangTime)
				{
					world.setBlockState(pos, currentState.withProperty(PROP_FLOWERING, false));
				}
			}
		}
		else
		{
			world.getTileEntity(pos).validate();
		}
	}

	public String getType(IBlockState state)
	{
		return getBlockState().getBaseState().getValue(speciesProperty).getName();
	}

	@Override
    public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
    
	public boolean canBlockStay(World world, BlockPos pos)
	{
		IBlockState thisState = world.getBlockState(pos);
		IBlockState downState = world.getBlockState(pos.down());

		BerrySpecies species = thisState.getValue(speciesProperty);

		return (world.getLight(pos) >= 8 || world.canSeeSky(pos)) &&
				(this.canThisPlantGrowOnThisBlock(downState) ||
				isSamePlant(world, pos.down(), downState) && canStack(species));
	}

	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		if(!canBlockStay(world, pos))
		{
			onNeighborChange(world, pos, pos);
		}
		else
		{
			TEBerryBush te = (TEBerryBush)world.getTileEntity(pos);
			te.dayHarvested = TFC_Time.getTotalDays();
		}
	}

	@Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
	{
		super.onNeighborChange(world, pos, neighbor);

		if (world instanceof World)
		    lifeCycle((World)world, pos);
	}

	protected boolean canThisPlantGrowOnThisBlock(IBlockState block)
	{
		return TFC_Core.isGrass(block);
	}

	@Override
	public TileEntity createNewTileEntity(World i, int meta)
	{
		return new TEBerryBush();
	}

	private boolean canStack(BerrySpecies species) {
		return species == BerrySpecies.RASPBERRY || species == BerrySpecies.BLACKBERRY || species == ELDERBERRY;
	}
}
