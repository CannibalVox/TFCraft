package com.bioxx.tfc.Blocks.Devices;

import com.bioxx.tfc.Helpers.SpanningBlockHelper;
import com.bioxx.tfc.api.Constant.MaterialTier;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.bioxx.tfc.Blocks.BlockTerraContainer;
import com.bioxx.tfc.Core.TFCTabs;
import com.bioxx.tfc.TileEntities.TEAnvil;
import com.bioxx.tfc.Blocks.Enums.AnvilMaterial;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockAnvil extends BlockTerraContainer
{
	private int offset;

	public static final String PROP_ANVIL_REQ = "anvilreq";
	public static final PropertyEnum<EnumFacing> PROP_FACING = PropertyEnum.create("facing", EnumFacing.class, EnumFacing.EAST, EnumFacing.NORTH);

	private static final AxisAlignedBB METAL_ANVIL_AABB = new AxisAlignedBB(0.2, 0, 0, 0.8, 0.6, 1);
	private static final AxisAlignedBB STONE_ANVIL_AABB = new AxisAlignedBB(0, 0, 0, 1, 0.9, 1);

	public static final int ANVILS_PER_BLOCK=8;

	public BlockAnvil(int offset)
	{
		super(Material.IRON);
		this.setCreativeTab(TFCTabs.TFC_DEVICES);
		this.offset = offset;
		this.setDefaultState(getBlockState().getBaseState()
                .withProperty((IProperty<AnvilMaterial>)getBlockState().getProperty(PROP_ANVIL_REQ), SpanningBlockHelper.spanFirst(AnvilMaterial.class, this.offset, ANVILS_PER_BLOCK))
                .withProperty(PROP_FACING, EnumFacing.EAST));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this,
                SpanningBlockHelper.spanEnum(PROP_ANVIL_REQ, AnvilMaterial.class, this.offset, ANVILS_PER_BLOCK),
                PROP_FACING);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
	{
	    for (AnvilMaterial req : SpanningBlockHelper.listEnum(AnvilMaterial.class, this.offset, ANVILS_PER_BLOCK)) {
	        items.add(new ItemStack(this, 1, req.ordinal()));
        }
	}

	@Override
    public int damageDropped(IBlockState state)
	{
	    IProperty<AnvilMaterial> prop = (IProperty<AnvilMaterial>)getBlockState().getProperty(PROP_ANVIL_REQ);
		return state.getValue(prop).ordinal();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityPlayer, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(world.isRemote)
		{
			return true;
		}
		else
		{
			TileEntity tileentity = world.getTileEntity(pos);

			if (tileentity instanceof TEAnvil)
			{
				entityPlayer.displayGui((TEAnvil)tileentity);
			}
			return true;
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
        IProperty<AnvilMaterial> anvilreqProp = (IProperty<AnvilMaterial>)getBlockState().getProperty(PROP_ANVIL_REQ);
        AnvilMaterial anvilMaterial = blockState.getValue(anvilreqProp);

        if (anvilMaterial == AnvilMaterial.STONE)
            return Block.FULL_BLOCK_AABB;
        return super.getCollisionBoundingBox(blockState, worldIn, pos);
	}

	@Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) { ;
        IProperty<AnvilMaterial> anvilreqProp = (IProperty<AnvilMaterial>)getBlockState().getProperty(PROP_ANVIL_REQ);

        AnvilMaterial anvilMaterial = state.getValue(anvilreqProp);
        EnumFacing facing = state.getValue(PROP_FACING);

        if (anvilMaterial != AnvilMaterial.STONE)
        {
            return METAL_ANVIL_AABB;
        }
        else
        {
            return STONE_ANVIL_AABB;
        }
    }

	@Override
	@SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
    public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}

	@Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
	{
        IProperty<AnvilMaterial> prop = (IProperty<AnvilMaterial>)getBlockState().getProperty(PROP_ANVIL_REQ);
        if (state.getValue(prop) == AnvilMaterial.STONE)
            return;

		super.harvestBlock(worldIn, player, pos, state, te, stack);
	}

	@Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
	{
        IProperty<AnvilMaterial> prop = (IProperty<AnvilMaterial>)getBlockState().getProperty(PROP_ANVIL_REQ);
        if (state.getValue(prop) == AnvilMaterial.STONE)
            return;

        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
	}

	@Override
    public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
    public IBlockState getStateFromMeta(int meta) {
	    AnvilMaterial req = SpanningBlockHelper.spanFromMeta(AnvilMaterial.class, meta & 7, this.offset, 8);
	    EnumFacing face = (meta & 8) == 0?EnumFacing.EAST:EnumFacing.NORTH;

	    IProperty<AnvilMaterial> anvilreq = (IProperty<AnvilMaterial>)getBlockState().getProperty(PROP_ANVIL_REQ);
	    return getBlockState().getBaseState().withProperty(anvilreq, req).withProperty(PROP_FACING, face);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        IProperty<AnvilMaterial> anvilreq = (IProperty<AnvilMaterial>)getBlockState().getProperty(PROP_ANVIL_REQ);

        AnvilMaterial req = state.getValue(anvilreq);
        EnumFacing face = state.getValue(PROP_FACING);

        int meta = (face == EnumFacing.NORTH)?8:0;
        return meta | SpanningBlockHelper.metaFromValue(req, this.offset, ANVILS_PER_BLOCK);
    }

	@Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
	    IBlockState state = getStateFromMeta(meta % ANVILS_PER_BLOCK);

		int l = MathHelper.floor(placer.rotationYaw * 4F / 360F + 0.5D) & 3;
		EnumFacing face = EnumFacing.EAST;
		if(l == 0 || l == 3)//+/-z
			face = EnumFacing.NORTH;

		return state.withProperty(PROP_FACING, face);
	}

	@Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		TEAnvil var5 = (TEAnvil)world.getTileEntity(pos);

		if (var5 != null)
		{
			for (int var6 = 0; var6 < var5.getSizeInventory(); ++var6)
			{
				ItemStack var7 = var5.getStackInSlot(var6);

				if (var7 != null)
				{
					float var8 = world.rand.nextFloat() * 0.8F + 0.1F;
					float var9 = world.rand.nextFloat() * 0.8F + 0.1F;
					EntityItem var12;

					for (float var10 = world.rand.nextFloat() * 0.8F + 0.1F; var7.getCount() > 0; world.spawnEntity(var12))
					{
						int var11 = world.rand.nextInt(21) + 10;

						if (var11 > var7.getCount())
							var11 = var7.getCount();
						var7.shrink(var11);

						var12 = new EntityItem(world, pos.getX() + var8, pos.getY() + var9, pos.getZ() + var10, new ItemStack(var7.getItem(), var11, var7.getItemDamage()));
						float var13 = 0.05F;
						var12.motionX = (float)world.rand.nextGaussian() * var13;
						var12.motionY = (float)world.rand.nextGaussian() * var13 + 0.2F;
						var12.motionZ = (float)world.rand.nextGaussian() * var13;
						if (var7.hasTagCompound())
							var12.getItem().setTagCompound((NBTTagCompound)var7.getTagCompound().copy());
					}
				}
			}
		}
		super.breakBlock(world, pos, state);
	}

	public static int getAnvilTypeFromMeta(int j)
	{
		int l = 7;
		return j & l;
	}

	public static int getDirectionFromMetadata(int i)
	{
		int d = i >> 3;
			if (d == 1)
				return 1;
			else
				return 0;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2)
	{
		return new TEAnvil();
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, net.minecraft.client.particle.ParticleManager manager)
	{
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, BlockPos pos, net.minecraft.client.particle.ParticleManager manager)
	{
		return world.getBlockState(pos).getBlock() == this;
	}
}
