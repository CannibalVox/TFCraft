package com.bioxx.tfc.Core;

import java.nio.ByteBuffer;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.WorldInfo;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.bioxx.tfc.TerraFirmaCraft;
import com.bioxx.tfc.Chunkdata.ChunkData;
import com.bioxx.tfc.Chunkdata.ChunkDataManager;
import com.bioxx.tfc.Core.Player.BodyTempStats;
import com.bioxx.tfc.Core.Player.FoodStatsTFC;
import com.bioxx.tfc.Core.Player.InventoryPlayerTFC;
import com.bioxx.tfc.Core.Player.SkillStats;
import com.bioxx.tfc.Food.ItemFoodTFC;
import com.bioxx.tfc.Items.ItemOre;
import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.Items.ItemBlocks.ItemTerraBlock;
import com.bioxx.tfc.TileEntities.TEMetalSheet;
import com.bioxx.tfc.WorldGen.TFCBiome;
import com.bioxx.tfc.api.*;
import com.bioxx.tfc.api.Constant.Global;
import com.bioxx.tfc.api.Entities.IAnimal;
import com.bioxx.tfc.api.Enums.EnumFuelMaterial;
import com.bioxx.tfc.api.Interfaces.IFood;

public class TFC_Core
{
	private static Map<Integer, ChunkDataManager> cdmMap = new HashMap<Integer, ChunkDataManager>();
	public static boolean preventEntityDataUpdate;

	public static ChunkDataManager getCDM(World world)
	{
		int key = world.isRemote ? 128 | world.provider.getDimension() : world.provider.getDimension();
		return cdmMap.get(key);
	}

	public static ChunkDataManager addCDM(World world)
	{
		int key = world.isRemote ? 128 | world.provider.getDimension() : world.provider.getDimension();
		if(!cdmMap.containsKey(key))
			return cdmMap.put(key, new ChunkDataManager(world));
		else return cdmMap.get(key);
	}

	public static ChunkDataManager removeCDM(World world)
	{
		int key = world.isRemote ? 128 | world.provider.getDimension() : world.provider.getDimension();
		return cdmMap.remove(key);
	}

	@SideOnly(Side.CLIENT)
	public static int getMouseX()
	{
		ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
		int i = scaledresolution.getScaledWidth();
		int k = Mouse.getX() * i / Minecraft.getMinecraft().displayWidth;

		return k;
	}

	@SideOnly(Side.CLIENT)
	public static int getMouseY()
	{
		ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
		int j = scaledresolution.getScaledHeight();
		int l = j - Mouse.getY() * j / Minecraft.getMinecraft().displayHeight - 1;

		return l;
	}

	public static Boolean isBlockAboveSolid(IBlockAccess blockAccess, BlockPos pos)
	{
		if(TerraFirmaCraft.proxy.getCurrentWorld().getBlockState(pos.up()).isOpaqueCube())
			return true;
		return false;
	}

	public static int getExtraEquipInventorySize(){
		//Just the back
		return 1;
	}

	public static InventoryPlayer getNewInventory(EntityPlayer player)
	{
		InventoryPlayer ip = player.inventory;
		NBTTagList nbt = new NBTTagList();
		nbt = player.inventory.writeToNBT(nbt);
		ip = new InventoryPlayerTFC(player);
		ip.readFromNBT(nbt);
		return ip;
	}

	public static ItemStack randomGem(Random random, int rockType)
	{
		ItemStack is = null;
		if (random.nextInt(500) == 0)
		{
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			items.add(new ItemStack(TFCItems.gemAgate, 1, 0));
			items.add(new ItemStack(TFCItems.gemAmethyst, 1, 0));
			items.add(new ItemStack(TFCItems.gemBeryl, 1, 0));
			items.add(new ItemStack(TFCItems.gemEmerald, 1, 0));
			items.add(new ItemStack(TFCItems.gemGarnet, 1, 0));
			items.add(new ItemStack(TFCItems.gemJade, 1, 0));
			items.add(new ItemStack(TFCItems.gemJasper, 1, 0));
			items.add(new ItemStack(TFCItems.gemOpal, 1, 0));
			items.add(new ItemStack(TFCItems.gemRuby, 1, 0));
			items.add(new ItemStack(TFCItems.gemSapphire, 1, 0));
			items.add(new ItemStack(TFCItems.gemTourmaline, 1, 0));
			items.add(new ItemStack(TFCItems.gemTopaz, 1, 0));

			is = (ItemStack) items.toArray()[random.nextInt(items.toArray().length)];
		}
		else if (random.nextInt(1000) == 0)
		{
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			items.add(new ItemStack(TFCItems.gemAgate, 1, 1));
			items.add(new ItemStack(TFCItems.gemAmethyst, 1, 1));
			items.add(new ItemStack(TFCItems.gemBeryl, 1, 1));
			items.add(new ItemStack(TFCItems.gemEmerald, 1, 1));
			items.add(new ItemStack(TFCItems.gemGarnet, 1, 1));
			items.add(new ItemStack(TFCItems.gemJade, 1, 1));
			items.add(new ItemStack(TFCItems.gemJasper, 1, 1));
			items.add(new ItemStack(TFCItems.gemOpal, 1, 1));
			items.add(new ItemStack(TFCItems.gemRuby, 1, 1));
			items.add(new ItemStack(TFCItems.gemSapphire, 1, 1));
			items.add(new ItemStack(TFCItems.gemTourmaline, 1, 1));
			items.add(new ItemStack(TFCItems.gemTopaz, 1, 1));

			is = (ItemStack) items.toArray()[random.nextInt(items.toArray().length)];
		}
		else if (random.nextInt(2000) == 0)
		{
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			items.add(new ItemStack(TFCItems.gemAgate, 1, 2));
			items.add(new ItemStack(TFCItems.gemAmethyst, 1, 2));
			items.add(new ItemStack(TFCItems.gemBeryl, 1, 2));
			items.add(new ItemStack(TFCItems.gemEmerald, 1, 2));
			items.add(new ItemStack(TFCItems.gemGarnet, 1, 2));
			items.add(new ItemStack(TFCItems.gemJade, 1, 2));
			items.add(new ItemStack(TFCItems.gemJasper, 1, 2));
			items.add(new ItemStack(TFCItems.gemOpal, 1, 2));
			items.add(new ItemStack(TFCItems.gemRuby, 1, 2));
			items.add(new ItemStack(TFCItems.gemSapphire, 1, 2));
			items.add(new ItemStack(TFCItems.gemTourmaline, 1, 2));
			items.add(new ItemStack(TFCItems.gemTopaz, 1, 2));

			is = (ItemStack) items.toArray()[random.nextInt(items.toArray().length)];
		}
		else if (random.nextInt(4000) == 0)
		{
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			items.add(new ItemStack(TFCItems.gemAgate, 1, 3));
			items.add(new ItemStack(TFCItems.gemAmethyst, 1, 3));
			items.add(new ItemStack(TFCItems.gemBeryl, 1, 3));
			items.add(new ItemStack(TFCItems.gemEmerald, 1, 3));
			items.add(new ItemStack(TFCItems.gemGarnet, 1, 3));
			items.add(new ItemStack(TFCItems.gemJade, 1, 3));
			items.add(new ItemStack(TFCItems.gemJasper, 1, 3));
			items.add(new ItemStack(TFCItems.gemOpal, 1, 3));
			items.add(new ItemStack(TFCItems.gemRuby, 1, 3));
			items.add(new ItemStack(TFCItems.gemSapphire, 1, 3));
			items.add(new ItemStack(TFCItems.gemTourmaline, 1, 3));
			items.add(new ItemStack(TFCItems.gemTopaz, 1, 3));

			is = (ItemStack) items.toArray()[random.nextInt(items.toArray().length)];
		}
		else if (random.nextInt(8000) == 0)
		{
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			items.add(new ItemStack(TFCItems.gemAgate, 1, 4));
			items.add(new ItemStack(TFCItems.gemAmethyst, 1, 4));
			items.add(new ItemStack(TFCItems.gemBeryl, 1, 4));
			items.add(new ItemStack(TFCItems.gemEmerald, 1, 4));
			items.add(new ItemStack(TFCItems.gemGarnet, 1, 4));
			items.add(new ItemStack(TFCItems.gemJade, 1, 4));
			items.add(new ItemStack(TFCItems.gemJasper, 1, 4));
			items.add(new ItemStack(TFCItems.gemOpal, 1, 4));
			items.add(new ItemStack(TFCItems.gemRuby, 1, 4));
			items.add(new ItemStack(TFCItems.gemSapphire, 1, 4));
			items.add(new ItemStack(TFCItems.gemTourmaline, 1, 4));
			items.add(new ItemStack(TFCItems.gemTopaz, 1, 4));

			is = (ItemStack) items.toArray()[random.nextInt(items.toArray().length)];
		}
		return is;
	}

	public static void surroundWithLeaves(World world, BlockPos pos, IBlockState state)
	{
		for (int y = 2; y >= -2; y--)
		{
			for (int x = 2; x >= -2; x--)
			{
				for (int z = 2; z >= -2; z--)
				{
				    BlockPos offsetPos = pos.east(x).south(z).up(y);
					if(world.isAirBlock(offsetPos))
						world.setBlockState(pos, state);
				}
			}
		}
	}

	public static void setupWorld(World world)
	{
		long seed = world.getSeed();
		Random r = new Random(seed);
		world.provider.registerWorld(world);
		Recipes.registerAnvilRecipes(r, world);
		TFC_Time.updateTime(world);
		// TerraFirmaCraft.proxy.registerSkyProvider(world.provider);
	}

	public static void setupWorld(World w, long seed)
	{
		try
		{
			// ReflectionHelper.setPrivateValue(WorldInfo.class,
			// w.getWorldInfo(), "randomSeed", seed);
			ReflectionHelper.setPrivateValue(WorldInfo.class, w.getWorldInfo(), seed, 0);
			setupWorld(w);
		}
		catch (Exception ex)
		{
		}
	}

	public static boolean isRawStone(World world, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		return block == TFCBlocks.stoneIgEx
				|| block == TFCBlocks.stoneIgIn
				|| block == TFCBlocks.stoneSed
				|| block == TFCBlocks.stoneMM;
	}

	public static boolean isSmoothStone(World world, BlockPos pos)
	{
	    IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		return block == TFCBlocks.stoneIgExSmooth
				|| block == TFCBlocks.stoneIgInSmooth
				|| block == TFCBlocks.stoneSedSmooth
				|| block == TFCBlocks.stoneMMSmooth;
	}

	public static boolean isSmoothStone(IBlockState state)
	{
	    Block block = state.getBlock();
		return block == TFCBlocks.stoneIgExSmooth
				|| block == TFCBlocks.stoneIgInSmooth
				|| block == TFCBlocks.stoneSedSmooth
				|| block == TFCBlocks.stoneMMSmooth;
	}

	public static boolean isBrickStone(IBlockState state)
	{
	    Block block = state.getBlock();
		return block == TFCBlocks.stoneIgExBrick
				|| block == TFCBlocks.stoneIgInBrick
				|| block == TFCBlocks.stoneSedBrick
				|| block == TFCBlocks.stoneMMBrick;
	}

	public static boolean isRawStone(IBlockState state)
	{
	    Block block = state.getBlock();
		return block == TFCBlocks.stoneIgEx
				|| block == TFCBlocks.stoneIgIn
				|| block == TFCBlocks.stoneSed
				|| block == TFCBlocks.stoneMM;
	}

	public static boolean isOreStone(IBlockState state)
	{
	    Block block = state.getBlock();
		return block == TFCBlocks.ore
				|| block == TFCBlocks.ore2
				|| block == TFCBlocks.ore3;
	}

	public static boolean isNaturalStone(IBlockState state)
	{
		return isRawStone( state ) || isOreStone( state );
	}

	public static boolean isCobbleStone(IBlockState state)
	{
	    Block block = state.getBlock();
		return block == TFCBlocks.stoneIgExCobble
				|| block == TFCBlocks.stoneIgInCobble
				|| block == TFCBlocks.stoneSedCobble
				|| block == TFCBlocks.stoneMMCobble;
	}

	public static boolean isStoneIgEx(IBlockState state)
	{
	    Block block = state.getBlock();
		return block == TFCBlocks.stoneIgEx
				|| block == TFCBlocks.stoneIgExCobble
				|| block == TFCBlocks.stoneIgExSmooth
				|| block == TFCBlocks.stoneIgExBrick
				|| block == TFCBlocks.wallRawIgEx
				|| block == TFCBlocks.wallCobbleIgEx
				|| block == TFCBlocks.wallBrickIgEx
				|| block == TFCBlocks.wallSmoothIgEx;
	}

	public static boolean isStoneIgIn(IBlockState state)
	{
	    Block block = state.getBlock();
		return block == TFCBlocks.stoneIgIn
				|| block == TFCBlocks.stoneIgInCobble
				|| block == TFCBlocks.stoneIgInSmooth
				|| block == TFCBlocks.stoneIgInBrick
				|| block == TFCBlocks.wallRawIgIn
				|| block == TFCBlocks.wallCobbleIgIn
				|| block == TFCBlocks.wallBrickIgIn
				|| block == TFCBlocks.wallSmoothIgIn;
	}

	public static boolean isStoneSed(IBlockState state)
	{
	    Block block = state.getBlock();
		return block == TFCBlocks.stoneSed
				|| block == TFCBlocks.stoneSedCobble
				|| block == TFCBlocks.stoneSedSmooth
				|| block == TFCBlocks.stoneSedBrick
				|| block == TFCBlocks.wallRawSed
				|| block == TFCBlocks.wallCobbleSed
				|| block == TFCBlocks.wallBrickSed
				|| block == TFCBlocks.wallSmoothSed;
	}

	public static boolean isStoneMM(IBlockState state)
	{
	    Block block = state.getBlock();
		return block == TFCBlocks.stoneMM
				|| block == TFCBlocks.stoneMMCobble
				|| block == TFCBlocks.stoneMMSmooth
				|| block == TFCBlocks.stoneMMBrick
				|| block == TFCBlocks.wallRawMM
				|| block == TFCBlocks.wallCobbleMM
				|| block == TFCBlocks.wallBrickMM
				|| block == TFCBlocks.wallSmoothMM;
	}

	public static boolean isDirt(IBlockState state)
	{
	    Block block = state.getBlock();
		return block == TFCBlocks.dirt
				|| block == TFCBlocks.dirt2;
	}

	public static boolean isFarmland(IBlockState state)
	{
		Block block = state.getBlock();
		return block == TFCBlocks.tilledSoil
				|| block == TFCBlocks.tilledSoil2;
	}

	public static boolean isGrass(IBlockState state)
	{
		Block block = state.getBlock();
		return block == TFCBlocks.grass
				|| block == TFCBlocks.grass2
				|| block == TFCBlocks.clayGrass
				|| block == TFCBlocks.clayGrass2
				|| block == TFCBlocks.peatGrass
				|| block == TFCBlocks.dryGrass
				|| block == TFCBlocks.dryGrass2;
	}

	public static boolean isGrassNormal(IBlockState state)
	{
	    Block block = state.getBlock();
		return block == TFCBlocks.grass
				|| block == TFCBlocks.grass2;
	}


	public static boolean isLushGrass(IBlockState state)
	{
        Block block = state.getBlock();
		return block == TFCBlocks.grass
				|| block == TFCBlocks.grass2
				|| block == TFCBlocks.clayGrass
				|| block == TFCBlocks.clayGrass2
				|| block == TFCBlocks.peatGrass;
	}

	public static boolean isClayGrass(IBlockState state)
	{
        Block block = state.getBlock();
		return block == TFCBlocks.clayGrass
				|| block == TFCBlocks.clayGrass2;
	}

	public static boolean isPeatGrass(IBlockState state)
	{
        Block block = state.getBlock();
		return block == TFCBlocks.peatGrass;
	}

	public static boolean isDryGrass(IBlockState state)
	{
        Block block = state.getBlock();
		return block == TFCBlocks.dryGrass
				|| block == TFCBlocks.dryGrass2;
	}

	public static boolean isGrassType1(IBlockState state)
	{
        Block block = state.getBlock();
		return block == TFCBlocks.grass
				|| block == TFCBlocks.clayGrass
				|| block == TFCBlocks.dryGrass;
	}

	public static boolean isGrassType2(IBlockState state)
	{
        Block block = state.getBlock();
		return block == TFCBlocks.grass2
				|| block == TFCBlocks.clayGrass2
				|| block == TFCBlocks.dryGrass2;
	}

	public static boolean isClay(IBlockState state)
	{
	    Block block = state.getBlock();
		return block == TFCBlocks.clay || block == TFCBlocks.clay2;
	}

	public static boolean isSand(IBlockState state)
	{
        Block block = state.getBlock();
		return block == TFCBlocks.sand
				|| block == TFCBlocks.sand2;
	}

	public static boolean isPeat(IBlockState state)
	{
        Block block = state.getBlock();
		return block == TFCBlocks.peat;
	}

	public static boolean isHotWater(IBlockState state)
	{
        Block block = state.getBlock();
		return block == TFCBlocks.hotWater || block == TFCBlocks.hotWaterStationary;
	}

	public static boolean isWater(IBlockState block)
	{
		return isSaltWater(block)
				|| isFreshWater(block);
	}

	public static boolean isWaterFlowing(IBlockState state)
	{
        Block block = state.getBlock();
		return block == TFCBlocks.saltWater || block == TFCBlocks.freshWater;
	}

	public static boolean isSaltWater(IBlockState state)
	{
	    Block block = state.getBlock();
		return block == TFCBlocks.saltWater || block == TFCBlocks.saltWaterStationary;
	}

	public static boolean isSaltWaterIncludeIce(Block block, int meta, Material mat)
	{
		return block == TFCBlocks.saltWater || block == TFCBlocks.saltWaterStationary
				|| mat == Material.ICE && meta == 0;
	}

	public static boolean isFreshWater(IBlockState state)
	{
	    Block block = state.getBlock();
		return block == TFCBlocks.freshWater || block == TFCBlocks.freshWaterStationary;
	}

	public static boolean isFreshWaterIncludeIce(Block block, int meta)
	{
		return block == TFCBlocks.freshWater || block == TFCBlocks.freshWaterStationary
				|| block == TFCBlocks.ice && meta != 0;
	}

	public static boolean isFreshWaterIncludeIce(Block block, int meta, Material mat)
	{
		return block == TFCBlocks.freshWater || block == TFCBlocks.freshWaterStationary
				|| mat == Material.ICE && meta != 0;
	}

	public static boolean isSoil(IBlockState block)
	{
		return isGrass(block)
				|| isDirt(block)
				|| isClay(block)
				|| isPeat(block);
	}

	public static boolean isSoilOrGravel(IBlockState block)
	{
		return isGrass(block)
				|| isDirt(block)
				|| isClay(block)
				|| isPeat(block)
				|| isGravel(block);
	}

	public static boolean isGravel(IBlockState state)
	{
        Block block = state.getBlock();
		return block == TFCBlocks.gravel || block == TFCBlocks.gravel2;
	}

	public static boolean isGround(IBlockState state)
	{
		return 	   isSoilOrGravel(state)
				|| isRawStone(state)
				|| isSand(state);
	}

	public static boolean isGroundType1(IBlockState state)
	{
        Block block = state.getBlock();
		return isGrassType1(state) || block == TFCBlocks.dirt || block == TFCBlocks.gravel || block == TFCBlocks.sand;
	}

	public static boolean isSoilWAILA(IBlockState state)
	{
		return isDirt(state) || isGravel(state) || isSand(state) || isGrassNormal(state) || isDryGrass(state);
	}

	public static int getSoilMetaFromStone(Block inBlock, int inMeta)
	{
		if(inBlock == TFCBlocks.stoneIgIn)
			return inMeta;
		else if(inBlock == TFCBlocks.stoneSed)
			return inMeta+3;
		else if(inBlock == TFCBlocks.stoneIgEx)
			return inMeta+11;
		else
		{
			if (inMeta == 0)
				return inMeta + 15;
			return inMeta-1;
		}
	}

	public static int getSoilMeta( int inMeta)
	{
		return inMeta & 15;
	}

	public static int getItemMetaFromStone(Block inBlock, int inMeta)
	{
		if(inBlock == TFCBlocks.stoneIgIn)
			return inMeta;
		else if(inBlock == TFCBlocks.stoneSed)
			return inMeta+3;
		else if(inBlock == TFCBlocks.stoneIgEx)
			return inMeta+11;
		else if(inBlock == TFCBlocks.stoneMM)
			return inMeta+15;
		else
			return 0;
	}

	public static Block getTypeForGrassWithRain(int inMeta, float rain)
	{
		if (rain >= 500)
			return getTypeForGrass(inMeta);
		return getTypeForDryGrass(inMeta);
	}

	public static Block getTypeForGrassWithRainByBlock(Block block, float rain)
	{
		if(rain >= 500)
			return getTypeForGrassFromSoil(block);
		return getTypeForDryGrassFromSoil(block);
	}

	public static Block getTypeForGrass(int inMeta)
	{
		if(inMeta < 16)
			return TFCBlocks.grass;
		return TFCBlocks.grass2;
	}

	public static Block getTypeForGrassFromDirt(Block block)
	{
		if(block == TFCBlocks.dirt)
			return TFCBlocks.grass;
		return TFCBlocks.grass2;
	}

	public static Block getTypeForDryGrass(int inMeta)
	{
		if(inMeta < 16)
			return TFCBlocks.dryGrass;
		return TFCBlocks.dryGrass2;
	}

	public static Block getTypeForDryGrassFromSoil(Block block)
	{
		if(block == TFCBlocks.grass)
			return TFCBlocks.dryGrass;
		else if(block == TFCBlocks.dirt)
			return TFCBlocks.dryGrass;
		return TFCBlocks.dryGrass2;
	}

	public static Block getTypeForGrassFromSoil(Block block)
	{
		if(block == TFCBlocks.dryGrass)
			return TFCBlocks.grass;
		else if(block == TFCBlocks.dryGrass2)
			return TFCBlocks.grass2;
		else if(block == TFCBlocks.dirt)
			return TFCBlocks.grass;
		return TFCBlocks.grass2;
	}

	public static Block getTypeForClayGrass(int inMeta)
	{
		if(inMeta < 16)
			return TFCBlocks.clayGrass;
		return TFCBlocks.clayGrass2;
	}

	public static Block getTypeForClayGrass(IBlockState state)
	{
		if (TFC_Core.isGroundType1(state))
			return TFCBlocks.clayGrass;
		return TFCBlocks.clayGrass2;
	}

	public static Block getTypeForDirt(int inMeta)
	{
		if(inMeta < 16)
			return TFCBlocks.dirt;
		return TFCBlocks.dirt2;
	}

	public static Block getTypeForDirtFromGrass(IBlockState state)
	{
        Block block = state.getBlock();
		if(TFC_Core.isDirt(state))
			return state.getBlock();
		if (block == TFCBlocks.grass || block == TFCBlocks.dryGrass)
			return TFCBlocks.dirt;
		return TFCBlocks.dirt2;
	}

	public static Block getTypeForSoil(IBlockState state)
	{
		if (TFC_Core.isGrass(state))
		{
			if (TFC_Core.isGrassType1(state))
				return TFCBlocks.dirt;
			else if (TFC_Core.isGrassType2(state))
				return TFCBlocks.dirt2;
			else if (TFC_Core.isPeatGrass(state))
				return TFCBlocks.peat;
		}

		return state.getBlock();
	}

	public static Block getTypeForClay(int inMeta)
	{
		if(inMeta < 16)
			return TFCBlocks.clay;
		return TFCBlocks.clay2;
	}

	public static Block getTypeForClay(IBlockState state)
	{
		if (TFC_Core.isGroundType1(state))
			return TFCBlocks.clay;
		return TFCBlocks.clay2;
	}

	public static Block getTypeForSand(int inMeta)
	{
		if(inMeta < 16)
			return TFCBlocks.sand;
		return TFCBlocks.sand2;
	}

	public static Block getTypeForGravel(int inMeta)
	{
		if(inMeta < 16)
			return TFCBlocks.gravel;
		return TFCBlocks.gravel2;
	}

	public static int getRockLayerFromHeight(World world, BlockPos pos)
	{
		ChunkData cd = TFC_Core.getCDM(world).getData(pos.getX() >> 4, pos.getZ() >> 4);
		if (cd != null)
		{
			int[] hm = cd.heightmap;
			int localX = pos.getX() & 15;
			int localZ = pos.getZ() & 15;
			int localY = localX + localZ * 16;
			if (pos.getY() <= TFCOptions.rockLayer3Height + hm[localY])
				return 2;
			else if (pos.getY() <= TFCOptions.rockLayer2Height + hm[localY])
				return 1;
			else
				return 0;
		}
		return 0;
	}

	public static boolean convertGrassToDirt(World world, BlockPos pos)
	{
		IBlockState block = world.getBlockState(pos);
		if(TFC_Core.isGrass(block))
		{
			if(TFC_Core.isGrassType1(block))
			{
				world.setBlockState(pos, TFCBlocks.dirt, meta, 2);
				return true;
			}
			else if(TFC_Core.isGrassType2(block))
			{
				world.setBlockState(pos, TFCBlocks.dirt2, meta, 2);
				return true;
			}
		}
		return false;
	}

	public static EnumFuelMaterial getFuelMaterial(ItemStack is)
	{
		if(is.getItem() == Item.getItemFromBlock(TFCBlocks.peat))
			return EnumFuelMaterial.PEAT;
		if(is.getItem() == TFCItems.coal && is.getItemDamage() == 0)
			return EnumFuelMaterial.COAL;
		else if(is.getItem() == TFCItems.coal && is.getItemDamage() == 1)
			return EnumFuelMaterial.CHARCOAL;
		if(is.getItemDamage() == 0)
			return EnumFuelMaterial.ASH;
		else if (is.getItemDamage() == 1)
			return EnumFuelMaterial.ASPEN;
		else if (is.getItemDamage() == 2)
			return EnumFuelMaterial.BIRCH;
		else if (is.getItemDamage() == 3)
			return EnumFuelMaterial.CHESTNUT;
		else if (is.getItemDamage() == 4)
			return EnumFuelMaterial.DOUGLASFIR;
		else if (is.getItemDamage() == 5)
			return EnumFuelMaterial.HICKORY;
		else if (is.getItemDamage() == 6)
			return EnumFuelMaterial.MAPLE;
		else if (is.getItemDamage() == 7)
			return EnumFuelMaterial.OAK;
		else if (is.getItemDamage() == 8)
			return EnumFuelMaterial.PINE;
		else if (is.getItemDamage() == 9)
			return EnumFuelMaterial.REDWOOD;
		else if (is.getItemDamage() == 10)
			return EnumFuelMaterial.SPRUCE;
		else if (is.getItemDamage() == 11)
			return EnumFuelMaterial.SYCAMORE;
		else if (is.getItemDamage() == 12)
			return EnumFuelMaterial.WHITECEDAR;
		else if (is.getItemDamage() == 13)
			return EnumFuelMaterial.WHITEELM;
		else if (is.getItemDamage() == 14)
			return EnumFuelMaterial.WILLOW;
		else if (is.getItemDamage() == 15)
			return EnumFuelMaterial.KAPOK;
		else if(is.getItemDamage() == 16)
			return EnumFuelMaterial.ACACIA;
		return EnumFuelMaterial.ASPEN;
	}

	public static boolean showShiftInformation()
	{
		return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
	}

	public static boolean showCtrlInformation()
	{
		return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
	}

	public static FoodStatsTFC getPlayerFoodStats(EntityPlayer player)
	{
		FoodStatsTFC foodstats = new FoodStatsTFC(player);
		foodstats.readNBT(player.getEntityData());
		return foodstats;
	}

	public static void setPlayerFoodStats(EntityPlayer player, FoodStatsTFC foodstats)
	{
		foodstats.writeNBT(player.getEntityData());
	}

	public static BodyTempStats getBodyTempStats(EntityPlayer player)
	{
		BodyTempStats body = new BodyTempStats();
		body.readNBT(player.getEntityData());
		return body;
	}

	public static void setBodyTempStats(EntityPlayer player, BodyTempStats tempStats)
	{
		tempStats.writeNBT(player.getEntityData());
	}

	public static SkillStats getSkillStats(EntityPlayer player)
	{
		SkillStats skills = new SkillStats(player);
		skills.readNBT(player.getEntityData());
		return skills;
	}

	public static void setSkillStats(EntityPlayer player, SkillStats skills)
	{
		skills.writeNBT(player.getEntityData());
	}

	public static boolean isTopFaceSolid(World world, BlockPos pos)
	{
		if(world.getBlockState(pos).isNormalCube())
			return true;
		else if(world.getBlockState(pos).getBlock() == TFCBlocks.metalSheet)
		{
			TEMetalSheet te = (TEMetalSheet) world.getTileEntity(pos);
			if(te.topExists())
				return true;
		}
		return world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.UP);
	}

	public static boolean isBottomFaceSolid(World world, BlockPos pos)
	{
		if(world.getBlockState(pos).isNormalCube())
			return true;
		else if(world.getBlockState(pos) == TFCBlocks.metalSheet)
		{
			TEMetalSheet te = (TEMetalSheet) world.getTileEntity(pos);
			if(te.bottomExists())
				return true;
		}
		return world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.DOWN);
	}

	public static boolean isNorthFaceSolid(World world, BlockPos pos)
	{
		IBlockState bid = world.getBlockState(pos);
		if(bid.isNormalCube())
			return true;
		else if(bid == TFCBlocks.metalSheet)
		{
			TEMetalSheet te = (TEMetalSheet) world.getTileEntity(pos);
			if(te.northExists())
				return true;
		}
		return world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.NORTH);
	}

	public static boolean isSouthFaceSolid(World world, BlockPos pos)
	{
		if(world.getBlockState(pos).isNormalCube())
			return true;
		else if(world.getBlockState(pos) == TFCBlocks.metalSheet)
		{
			TEMetalSheet te = (TEMetalSheet) world.getTileEntity(pos);
			if(te.southExists())
				return true;
		}
		return world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.SOUTH);
	}

	public static boolean isEastFaceSolid(World world, BlockPos pos)
	{
		if(world.getBlockState(pos).isNormalCube())
			return true;
		else if(world.getBlockState(pos) == TFCBlocks.metalSheet)
		{
			TEMetalSheet te = (TEMetalSheet) world.getTileEntity(pos);
			if(te.eastExists())
				return true;
		}
		return world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.EAST);
	}

	public static boolean isWestFaceSolid(World world, BlockPos pos)
	{
		if(world.getBlockState(pos).isNormalCube())
			return true;
		else if(world.getBlockState(pos) == TFCBlocks.metalSheet)
		{
			TEMetalSheet te = (TEMetalSheet) world.getTileEntity(pos);
			if(te.westExists())
				return true;
		}
		return world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.WEST);
	}

	public static boolean isSurroundedSolid(World world, BlockPos pos)
	{
		return TFC_Core.isNorthFaceSolid(world, pos.south()) &&
				TFC_Core.isSouthFaceSolid(world, pos.north()) &&
				TFC_Core.isEastFaceSolid(world, pos.west()) &&
				TFC_Core.isWestFaceSolid(world, pos.east()) &&
				TFC_Core.isTopFaceSolid(world, pos.down());
	}

	public static boolean isSurroundedStone(World world, BlockPos pos)
	{
		return world.getBlockState(pos.south()).getMaterial() == Material.ROCK &&
				world.getBlockState(pos.north()).getMaterial() == Material.ROCK &&
				world.getBlockState(pos.west()).getMaterial() == Material.ROCK &&
				world.getBlockState(pos.east()).getMaterial() == Material.ROCK &&
				world.getBlockState(pos.down()).getMaterial() == Material.ROCK;
	}

	public static boolean isOreIron(ItemStack is)
	{
		return is.getItem() instanceof ItemOre && ((ItemOre) is.getItem()).getMetalType(is) == Global.PIGIRON;
	}

	public static float getEntityMaxHealth(EntityLivingBase entity)
	{
		return (float) entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
	}

	public static float getPercentGrown(IAnimal animal)
	{
		float birth = animal.getBirthDay();
		float time = TFC_Time.getTotalDays();
		float percent = (time - birth) / animal.getNumberOfDaysToAdult();
		return Math.min(percent, 1f);
	}

	public static void bindTexture(ResourceLocation texture)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
	}

	public static boolean isPlayerInDebugMode(EntityPlayer player)
	{
		return TFCOptions.enableDebugMode;
	}

	/**
	 * Adds exhaustion to the player. 0.001 is a standard amount.
	 */
	public static void addPlayerExhaustion(EntityPlayer player, float exhaustion)
	{
		FoodStatsTFC foodstats = TFC_Core.getPlayerFoodStats(player);
		foodstats.addFoodExhaustion(exhaustion);
		//foodstats.addWaterExhaustion(exhaustion);
		TFC_Core.setPlayerFoodStats(player, foodstats);
	}

	public static float getEnvironmentalDecay(float temp)
	{
		if (temp > 0)
		{
			float tempFactor = 1f - (15f / (15f + temp));
			return tempFactor * 2;
		}
		else
			return 0;
	}

	/**
	 * This is the default item ticking method for use by all containers. Call
	 * this if you don't want to do custom environmental decay math.
	 */
	public static void handleItemTicking(IInventory iinv, World world, BlockPos pos)
	{
		handleItemTicking(iinv, world, pos, 1);
	}

	/**
	 * This is the default item ticking method for use by all containers. Call
	 * this if you don't want to do custom environmental decay math.
	 */
	public static void handleItemTicking(ItemStack[] iinv, World world, BlockPos pos)
	{
		handleItemTicking(iinv, world, pos, 1);
	}

	/**
	 * This version of the method assumes that the environmental decay modifier
	 * has already been calculated.
	 */
	public static void handleItemTicking(IInventory iinv, World world, BlockPos pos, float environmentalDecayFactor)
	{
		for (int i = 0; !world.isRemote && i < iinv.getSizeInventory(); i++)
		{
			ItemStack is = iinv.getStackInSlot(i);
			if (is != null && iinv.getStackInSlot(i).getCount() <= 0)
				iinv.setInventorySlotContents(i, null);

			if (is != null)
			{
				if(is.getCount() == 0)
				{
					iinv.setInventorySlotContents(i, null);
					continue;
				}
				if (is.getItem() instanceof ItemTerra && ((ItemTerra) is.getItem()).onUpdate(is, world, pos))
					continue;
				else if (is.getItem() instanceof ItemTerraBlock && ((ItemTerraBlock) is.getItem()).onUpdate(is, world, pos))
					continue;
				is = tickDecay(is, world, pos, environmentalDecayFactor, 1f);
				if(is != null)
					TFC_ItemHeat.handleItemHeat(is);
				iinv.setInventorySlotContents(i, is);
			}

		}
	}

	//Takes a small float in the range of 0.5 to 1.5. The resulting float would be of the form [0 0111111 [the byte] 0..0], such that the byte returned
	//is the only unknown value
	public static byte getByteFromSmallFloat(float f){
		MathHelper.clamp(f, 0.5f, 1.5f);
		return (byte)((Float.floatToIntBits(f) >> 16) & 0xff);
	}

	public static float getSmallFloatFromByte(byte b)
	{
		return ByteBuffer.wrap(new byte[]{(byte)63, b,(byte)(0),(byte)0}).getFloat();
	}

	/**
	 * This version of the method assumes that the environmental decay modifier
	 * has already been calculated.
	 */
	public static void handleItemTicking(IInventory iinv, World world, BlockPos pos, float environmentalDecayFactor, float baseDecayMod)
	{
		for (int i = 0; !world.isRemote && i < iinv.getSizeInventory(); i++)
		{
			ItemStack is = iinv.getStackInSlot(i);
			if (is != null && iinv.getStackInSlot(i).getCount() <= 0)
				iinv.setInventorySlotContents(i, null);

			if (is != null)
			{
				if (is.getItem() instanceof ItemTerra && ((ItemTerra) is.getItem()).onUpdate(is, world, pos))
					continue;
				else if (is.getItem() instanceof ItemTerraBlock && ((ItemTerraBlock) is.getItem()).onUpdate(is, world, pos))
					continue;
				is = tickDecay(is, world, pos, environmentalDecayFactor, baseDecayMod);
				if(is != null)
					TFC_ItemHeat.handleItemHeat(is);
				iinv.setInventorySlotContents(i, is);
			}
		}
	}

	/**
	 * This version of the method assumes that the environmental decay modifier
	 * has already been calculated.
	 */
	public static void handleItemTicking(ItemStack[] iinv, World world, BlockPos pos, float environmentalDecayFactor)
	{
		for (int i = 0; !world.isRemote && i < iinv.length; i++)
		{
			ItemStack is = iinv[i];
			if (is != null && iinv[i].getCount() <= 0)
				iinv[i] = null;

			if (is != null)
			{
				if (is.getItem() instanceof ItemTerra && ((ItemTerra) is.getItem()).onUpdate(is, world, pos))
					continue;
				else if (is.getItem() instanceof ItemTerraBlock && ((ItemTerraBlock) is.getItem()).onUpdate(is, world, pos))
					continue;
				is = tickDecay(is, world, pos, environmentalDecayFactor, 1);
				if(is != null)
					TFC_ItemHeat.handleItemHeat(is);
				iinv[i] = is;
			}

		}
	}

	public static ItemStack tickDecay(ItemStack is, World world, BlockPos pos, float environmentalDecayFactor, float baseDecayMod)
	{
		NBTTagCompound nbt = is.getTagCompound();
		if (nbt == null || !nbt.hasKey(Food.WEIGHT_TAG) || !nbt.hasKey(Food.DECAY_TAG))
			return is;

		int decayTimer = Food.getDecayTimer(is);
		// if the tick timer is up then we cause decay.
		if (decayTimer < TFC_Time.getTotalHours())
		{
			int timeDiff = (int) (TFC_Time.getTotalHours() - decayTimer);
			float protMult = 1;

			if(TFCOptions.useDecayProtection)
			{
				if(timeDiff > TFCOptions.decayProtectionDays * 24)
				{
					decayTimer = (int) TFC_Time.getTotalHours() - 24;
				}
				else if(timeDiff > 24)
				{
					protMult = 1-(timeDiff/(TFCOptions.decayProtectionDays * 24));
				}
			}

			float decay = Food.getDecay(is);
			float thisDecayRate = 1.0f;
			// Get the base food decay rate
			if (is.getItem() instanceof IFood)
				thisDecayRate = ((IFood) is.getItem()).getDecayRate(is);
			// check if the food has a specially applied decay rate in its nbt (meals, sandwiches, salads)
			else
				thisDecayRate = Food.getDecayRate(is);

			/*
			 * Here we calculate the decayRate based on the environment. We do
			 * this before everything else so that its only done once per
			 * inventory
			 */
			//int day = TFC_Time.getDayOfYearFromDays(TFC_Time.getDayFromTotalHours(nbt.getInteger(Food.DECAY_TIMER_TAG)));
			//float temp = TFC_Climate.getHeightAdjustedTempSpecificDay(world,day,nbt.getInteger(Food.DECAY_TIMER_TAG), pos);
			float temp = getCachedTemp(world, pos, decayTimer);
			float environmentalDecay = getEnvironmentalDecay(temp) * environmentalDecayFactor;

			if (decay < 0)
			{
				float d = 1 * (thisDecayRate * baseDecayMod * environmentalDecay);
				if (decay + d < 0)
					decay += d;
				else
					decay = 0;
			}
			else if (decay == 0)
			{
				decay = (Food.getWeight(is) * (world.rand.nextFloat() * 0.005f)) * TFCOptions.decayMultiplier;
			}
			else
			{
				double fdr = TFCOptions.foodDecayRate - 1;
				fdr *= thisDecayRate * baseDecayMod * environmentalDecay * protMult * TFCOptions.decayMultiplier;
				decay *= 1 + fdr;
			}
			Food.setDecayTimer(is, decayTimer + 1);
			Food.setDecay(is, decay);
		}

		if (Food.getDecay(is) / Food.getWeight(is) > 0.9f)
		{
			if(is.getItem() instanceof IFood)
				is = ((IFood)is.getItem()).onDecayed(is, world, pos);
			else
				is.setCount(0);
		}

		return is;
	}

	public static float getCachedTemp(World world, BlockPos pos, int th)
	{
		float cacheTemp = TFC_Climate.getCacheManager(world).getTemp(pos.getX(), pos.getZ(), th);
		if(cacheTemp != Float.MIN_VALUE)
		{
			return cacheTemp;
		}
		float temp = TFC_Climate.getHeightAdjustedTempSpecificDay(world,TFC_Time.getDayFromTotalHours(th), TFC_Time.getHourOfDayFromTotalHours(th), pos);
		addCachedTemp(world, pos.getX(), pos.getZ(), th, temp);
		return temp;
	}

	public static void addCachedTemp(World world, int x, int z, int th, float temp)
	{
		TFC_Climate.getCacheManager(world).addTemp(x, z, th, temp);
	}

	public static void animalDropMeat(Entity e, Item i, float foodWeight)
	{
		Random r;
		ItemStack is = ItemFoodTFC.createTag(new ItemStack(i, 1), foodWeight);
		r = new Random(e.getUniqueID().getLeastSignificantBits() + e.getUniqueID().getMostSignificantBits());
		Food.adjustFlavor(is, r);
		e.capturedDrops.add(new EntityItem(e.getEntityWorld(), e.posX, e.posY, e.posZ, is));
	}

	public static void giveItemToPlayer(ItemStack is, EntityPlayer player)
	{
		if(player.getEntityWorld().isRemote)
			return;
		EntityItem ei = player.entityDropItem(is, 1);
		ei.setNoPickupDelay();
	}

	public static boolean isFence(Block b)
	{
		return b == TFCBlocks.fence || b == TFCBlocks.fence2;
	}

	public static boolean isVertSupport(Block b)
	{
		return b == TFCBlocks.woodSupportV || b == TFCBlocks.woodSupportV2;
	}

	public static boolean isHorizSupport(Block b)
	{
		return b == TFCBlocks.woodSupportH || b == TFCBlocks.woodSupportH2;
	}

	public static boolean isOceanicBiome(Biome biome)
	{
		return biome == TFCBiome.OCEAN || biome == TFCBiome.DEEP_OCEAN;
	}

	public static boolean isMountainBiome(Biome biome)
	{
		return biome == TFCBiome.MOUNTAINS || biome == TFCBiome.MOUNTAINS_EDGE;
	}

	public static boolean isBeachBiome(Biome biome)
	{
		return biome == TFCBiome.BEACH || biome == TFCBiome.GRAVEL_BEACH;
	}

	public static boolean isValidCharcoalPitCover(IBlockState state)
	{
	    Block block = state.getBlock();
		if(Blocks.FIRE.getFlammability(block) > 0 && block != TFCBlocks.logPile) return false;

		return block == TFCBlocks.logPile
				|| isCobbleStone(state)
				|| isBrickStone(state)
				|| isSmoothStone(state)
				|| isGround(state)
				|| block == Blocks.GLASS
				|| block == Blocks.STAINED_GLASS
				|| block == TFCBlocks.metalTrapDoor
				|| block == Blocks.IRON_DOOR
				|| state.isOpaqueCube();
	}

	public static void writeInventoryToNBT(NBTTagCompound nbt, ItemStack[] storage)
	{
		writeInventoryToNBT(nbt, storage, "Items");
	}

	public static void writeInventoryToNBT(NBTTagCompound nbt, ItemStack[] storage, String name)
	{
		NBTTagList nbttaglist = new NBTTagList();
		for(int i = 0; i < storage.length; i++)
		{
			if(storage[i] != null)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte)i);
				storage[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		nbt.setTag(name, nbttaglist);
	}

	public static void readInventoryFromNBT(NBTTagCompound nbt, ItemStack[] storage)
	{
		readInventoryFromNBT(nbt, storage, "Items");
	}

	public static void readInventoryFromNBT(NBTTagCompound nbt, ItemStack[] storage, String name)
	{
		NBTTagList nbttaglist = nbt.getTagList(name, 10);
		for(int i = 0; i < nbttaglist.tagCount(); i++)
		{
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte byte0 = nbttagcompound1.getByte("Slot");
			if(byte0 >= 0 && byte0 < storage.length)
				storage[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
		}
	}

	public static ItemStack getItemInInventory(Item item, IInventory iinv)
	{
		for(int i = 0; i < iinv.getSizeInventory(); i++)
		{
			iinv.getStackInSlot(i);
			if(iinv.getStackInSlot(i) != null && iinv.getStackInSlot(i).getItem() == item)
			{
				return iinv.getStackInSlot(i);
			}
		}
		return null;
	}

	public static void destroyBlock(World world, BlockPos pos)
	{
	    IBlockState state = world.getBlockState(pos);
	    Block block = state.getBlock();
		if(block != Blocks.AIR)
		{
			block.dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}
	}

	public static boolean areItemsEqual(ItemStack is1, ItemStack is2)
	{
		Item i1 = null; int d1 = 0;
		Item i2 = null; int d2 = 0;
		if(is1 != null)
		{
			i1 = is1.getItem(); d1 = is1.getItemDamage();
		}
		if(is2 != null)
		{
			i2 = is2.getItem(); d2 = is2.getItemDamage();
		}
		return i1 == i2 && d1 == d2;
	}

	public static boolean setBlockWithDrops(World world, BlockPos pos, Block b, int meta)
	{
		IBlockState state = world.getBlockState(pos);

		if (state.getMaterial() != Material.AIR)
		{
			int l = state.getBlock().getMetaFromState(state);
			world.playEvent(2001, pos, Block.getIdFromBlock(state.getBlock()) + (l << 12));
			state.getBlock().dropBlockAsItem(world, pos, state, 0);
		}
		return world.setBlockState(pos, b, meta, 3);
	}

	/**
	 * This is a wrapper method for the vanilla world method with no MCP mapping
	 */
	public static boolean setBlockToAirWithDrops(World world, BlockPos pos)
	{
		return world.destroyBlock(pos, true);
	}

	public static boolean isWaterBiome(Biome b)
	{
		return TFC_Core.isBeachBiome(b) || TFC_Core.isOceanicBiome(b) || b == TFCBiome.LAKE || b == TFCBiome.RIVER;
	}

	public static String translate(String s)
	{
		return StatCollector.translateToLocal(s);
	}

	public static void sendInfoMessage(EntityPlayer player, IChatComponent text)
	{
		text.getChatStyle().setColor(ChatFormatting.GRAY).setItalic(true);
		player.addChatComponentMessage(text);
	}

	public static long getSuperSeed(World w)
	{
		return w.getSeed()+w.getWorldInfo().getPlayerNBTTagCompound().getLong("superseed");
	}
	
	public static boolean isExposedToRain(World world, BlockPos pos)
	{
		BlockPos highest = world.getPrecipitationHeight(pos).down();
		boolean isExposed = true;
		if (world.canBlockSeeSky(pos.up())) // Either no blocks, or transparent blocks above.
		{
			// Glass blocks, or blocks with a solid top or bottom block the rain.
            Block highestBlock = world.getBlockState(highest).getBlock();
			if (highestBlock instanceof BlockGlass
					|| highestBlock instanceof BlockStainedGlass
					|| world.isSideSolid(highest, EnumFacing.UP)
					|| world.isSideSolid(highest, EnumFacing.DOWN))
				isExposed = false;
		}
		else // Can't see the sky
			isExposed = false;

		return world.isRaining() && isExposed;
	}
}
