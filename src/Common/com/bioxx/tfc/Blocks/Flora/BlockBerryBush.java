package com.bioxx.tfc.Blocks.Flora;

import java.util.List;
import java.util.Random;

import com.bioxx.tfc.Blocks.Enums.BerrySpecies;
import com.bioxx.tfc.Helpers.SpanningBlockHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IIcon;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.bioxx.tfc.Reference;
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

public class BlockBerryBush extends BlockTerraContainer
{
	public static final String PROP_BERRY_SPECIES = "species";
	public static final PropertyBool PROP_FLOWERING = PropertyBool.create("berry");

	public static final int BERRIES_PER_BLOCK=8;

	private int offset;

	public BlockBerryBush(int offset)
	{
		super(Material.PLANTS);
		this.setTickRandomly(true);
		this.setCreativeTab(TFCTabs.TFC_DECORATION);
		this.offset = offset;
		this.setDefaultState(getBlockState().getBaseState()
            .withProperty((IProperty<BerrySpecies>)getBlockState().getProperty(PROP_BERRY_SPECIES), SpanningBlockHelper.spanFirst(BerrySpecies.class, offset, BERRIES_PER_BLOCK))
            .withProperty(PROP_FLOWERING, false));
	}

	@Override
    protected BlockStateContainer createBlockState() {
	    return new BlockStateContainer(this,
                SpanningBlockHelper.spanEnum(PROP_BERRY_SPECIES, BerrySpecies.class, this.offset, BERRIES_PER_BLOCK),
                PROP_FLOWERING);
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
        IProperty<BerrySpecies> prop = (IProperty<BerrySpecies>)getBlockState().getProperty(PROP_BERRY_SPECIES);
        return state.getValue(prop).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean flowering = (meta % 8) != 0;
        BerrySpecies species = SpanningBlockHelper.spanFromMeta(BerrySpecies.class, meta & 7, this.offset, BERRIES_PER_BLOCK);

        IProperty<BerrySpecies> speciesProp = (IProperty<BerrySpecies>)getBlockState().getProperty(PROP_BERRY_SPECIES);
        return getBlockState().getBaseState().withProperty(speciesProp, species).withProperty(PROP_FLOWERING, flowering);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        boolean flowering = state.getValue(PROP_FLOWERING);
        int meta = flowering?8:0;
        IProperty<BerrySpecies> speciesProp = (IProperty<BerrySpecies>)getBlockState().getProperty(PROP_BERRY_SPECIES);
        BerrySpecies species = state.getValue(speciesProp);
        return meta | SpanningBlockHelper.metaFromValue(species, this.offset, BERRIES_PER_BLOCK);
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
        IProperty<BerrySpecies> speciesProp = (IProperty<BerrySpecies>)getBlockState().getProperty(PROP_BERRY_SPECIES);
        BerrySpecies species = state.getValue(speciesProp);
        if(species == BerrySpecies.BLUEBERRY || species == BerrySpecies.RASPBERRY || species == BerrySpecies.BLACKBERRY ||
                species == BerrySpecies.ELDERBERRY || species == BerrySpecies.GOOSEBERRY)
        {
            entity.motionX *= 0.7D;
            entity.motionZ *= 0.7D;
        }

        if(species == BerrySpecies.RASPBERRY || species == BerrySpecies.BLACKBERRY)
        {
            if(entity instanceof EntityLivingBase)
                entity.attackEntityFrom(DamageSource.CACTUS, 5);
        }
    }

	@Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
		float minX = 0.1f;
		float minZ = 0.1f;
		float maxX = 0.9f;
		float maxZ = 0.9f;
		float maxY = 1f;

		if(isSamePlant(access, pos.west(), state)) minX = 0;
		if(isSamePlant(access, pos.east(), state)) maxX = 1;
		if(isSamePlant(access, pos.north(), state)) minZ = 0;
		if(isSamePlant(access, pos.south(), state)) maxZ = 1;

		IProperty<BerrySpecies> speciesProp = (IProperty<BerrySpecies>)getBlockState().getProperty(PROP_BERRY_SPECIES);

		switch(state.getValue(speciesProp))
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
			if(isSamePlant(access, pos.up(), state))
				maxY = 1;
		}
		case STRAWBERRY:
		{
			maxY = 0.2f;
		}
		case BLACKBERRY:
		{
			maxY = 0.85f;
			if(isSamePlant(access, pos.up(), state))
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
			if(isSamePlant(access, pos.up(), state))
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

	private boolean isSamePlant(IBlockAccess bAccess, BlockPos pos, IBlockState state)
	{
	    IBlockState neighborState = bAccess.getBlockState(pos);

	    IProperty<BerrySpecies> speciesProp = (IProperty<BerrySpecies>)getBlockState().getProperty(PROP_BERRY_SPECIES);
	    BerrySpecies species = state.getValue(speciesProp);
	    BerrySpecies neighborSpecies = neighborState.getValue(speciesProp);

		return species == neighborSpecies;
	}

	/* Left-Click Harvest Berries */
	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer entityplayer)
	{
		if (!world.isRemote)
		{
		    IBlockState state = world.getBlockState(pos);
			FloraManager manager = FloraManager.getInstance();
			FloraIndex fi = manager.findMatchingIndex(getType(state));

			TEBerryBush te = (TEBerryBush) world.getTileEntity(pos);
			if (te != null && te.hasFruit)
			{
				te.hasFruit = false;
				te.dayHarvested = TFC_Time.getTotalDays();
				world.markBlockForUpdate(x, y, z);
				dropBlockAsItem(world, pos, ItemFoodTFC.createTag(fi.getOutput(), Helper.roundNumber(3 + world.rand.nextFloat() * 5, 10)));
			}
		}
	}

	/* Right-Click Harvest Berries */
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!world.isRemote)
		{
			FloraManager manager = FloraManager.getInstance();
			FloraIndex fi = manager.findMatchingIndex(getType(state));

			TEBerryBush te = (TEBerryBush) world.getTileEntity(pos);
			if(te != null && te.hasFruit)
			{
				te.hasFruit = false;
				te.dayHarvested = TFC_Time.getTotalDays();
				world.markBlockForUpdate(x, y, z);
				dropBlockAsItem(world, pos, ItemFoodTFC.createTag(fi.getOutput(), Helper.roundNumber(3 + world.rand.nextFloat() * 5, 10)));
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
					if(!tebb.hasFruit && floraIndex.inHarvest(TFC_Time.getSeasonAdjustedMonth(z)) && TFC_Time.getMonthsSinceDay(tebb.dayHarvested) > 0)
					{
						tebb.hasFruit = true;
						tebb.dayFruited = TFC_Time.getTotalDays();
						world.markBlockForUpdate(x, y, z);
					}
				}
				else if(temp < floraIndex.minTemp - 5 || temp > floraIndex.maxTemp + 5)
				{
					if(tebb.hasFruit)
					{
						tebb.hasFruit = false;
						world.markBlockForUpdate(x, y, z);
					}
				}

				if(tebb.hasFruit && TFC_Time.getMonthsSinceDay(tebb.dayFruited) > floraIndex.fruitHangTime)
				{
					tebb.hasFruit = false;
					world.markBlockForUpdate(x, y, z);
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
	    IProperty<BerrySpecies> speciesProp = (IProperty<BerrySpecies>)getBlockState().getProperty(PROP_BERRY_SPECIES);
		return getBlockState().getBaseState().getValue(speciesProp).getName();
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
	public boolean canBlockStay(World world, BlockPos pos)
	{
		int meta = world.getBlockMetadata(x, y, z);
		return (world.getLight(pos) >= 8 || world.canSeeSky(pos)) &&
				(this.canThisPlantGrowOnThisBlock(world.getBlock(x, y - 1, z)) || 
				isSamePlant(world, x, y - 1, z, world.getBlockMetadata(x, y, z)) && canStack(meta));
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
		lifeCycle(world, pos);
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

	private boolean canStack(int meta) {
		return meta == RASPBERRY || meta == BLACKBERRY || meta == ELDERBERRY;
	}
}
