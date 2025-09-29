package net.milkev.milkevsoreminers.common;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.milkev.milkevsmultiblocklibrary.common.MilkevsMultiBlockLibrary;
import net.milkev.milkevsoreminers.common.blockEntities.AdvancedSifterBlockEntity;
import net.milkev.milkevsoreminers.common.blockEntities.SifterBlockEntity;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.BasicMiningRigBlockEntity;
import net.milkev.milkevsoreminers.common.blocks.AdvancedSifterBlock;
import net.milkev.milkevsoreminers.common.blocks.MiningRigBlock;
import net.milkev.milkevsoreminers.common.blocks.SifterBlock;
import net.milkev.milkevsoreminers.common.gui.AdvancedSifterScreenHandler;
import net.milkev.milkevsoreminers.common.recipes.AdvancedSifterRecipe;
import net.milkev.milkevsoreminers.common.recipes.SifterRecipe;
import net.milkev.milkevsoreminers.common.recipes.miningRig.BasicMiningRigRecipe;
import net.milkev.milkevsoreminers.common.util.BlockPosPayload;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.TintedGlassBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import team.reborn.energy.api.EnergyStorage;

import java.util.HashMap;
import java.util.Map;

public class MilkevsOreMiners implements ModInitializer {

	public static final String MOD_ID = "milkevsoreminers";
	
	//Sifter
	public static final SifterBlock SIFTER_BLOCK = new SifterBlock(AbstractBlock.Settings.create().strength(50).nonOpaque());
	public static final BlockEntityType<SifterBlockEntity> SIFTER_BLOCK_ENTITY = BlockEntityType.Builder.create(SifterBlockEntity::new, SIFTER_BLOCK).build();
	public static final RecipeSerializer<SifterRecipe> SIFTER_RECIPE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, id("sifter"), new SifterRecipe.MyRecipeSerializer());
	public static final RecipeType<SifterRecipe> SIFTER_RECIPE_TYPE = new RecipeType<>() {
		@Override
		public String toString() {
			return "sifter";
		}
	};
	
	
	//Advanced Sifter
	public static final AdvancedSifterBlock ADVANCED_SIFTER_BLOCK = new AdvancedSifterBlock(AbstractBlock.Settings.create().strength(50));
	public static final BlockEntityType<AdvancedSifterBlockEntity> ADVANCED_SIFTER_BLOCK_ENTITY = BlockEntityType.Builder.create(AdvancedSifterBlockEntity::new, ADVANCED_SIFTER_BLOCK).build();
	public static final RecipeSerializer<AdvancedSifterRecipe> ADVANCED_SIFTER_RECIPE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, id("advanced_sifter"), new AdvancedSifterRecipe.MyRecipeSerializer());
	public static final RecipeType<AdvancedSifterRecipe> ADVANCED_SIFTER_RECIPE_TYPE = new RecipeType<>() {
		@Override
		public String toString() {return "advanced_sifter";}
	};
	//screen
	public static final ScreenHandlerType<AdvancedSifterScreenHandler> ADVANCED_SIFTER_SCREEN_HANDLER = registerScreen("advanced_sifter", AdvancedSifterScreenHandler::new, BlockPosPayload.PACKET_CODEC);
	
	static public class MINING_RIG {
		static public class BASIC {
			public static final MiningRigBlock CONTROLLER = new MiningRigBlock(AbstractBlock.Settings.create().strength(50), 1);
			public static final BlockEntityType<BasicMiningRigBlockEntity> BLOCK_ENTITY = BlockEntityType.Builder.create(BasicMiningRigBlockEntity::new, CONTROLLER).build();
			public static final RecipeSerializer<BasicMiningRigRecipe> RECIPE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, id("basic_mining_rig"), new BasicMiningRigRecipe.MyRecipeSerializer());
			public static final RecipeType<BasicMiningRigRecipe> RECIPE_TYPE = new RecipeType<BasicMiningRigRecipe>() {
				@Override
				public String toString() {
					return "basic_mining_rig";
				}
			};
			public static final Block WALL = new Block(AbstractBlock.Settings.create().strength(50));
			public static final Block GLASS = new TintedGlassBlock(AbstractBlock.Settings.create().nonOpaque().strength(50));
			public static final Block IO = new BarrelBlock(AbstractBlock.Settings.create().strength(50));
		}
	/*
	//Advanced Mining Rig
	public static final MiningRigBlock MINING_RIG_TIER_2_BLOCK = new MiningRigBlock(AbstractBlock.Settings.create().strength(50), 2);
	public static final BlockEntityType<MiningRigTier2BlockEntity> MINING_RIG_TIER_2_BLOCK_ENTITY = BlockEntityType.Builder.create(MiningRigTier2BlockEntity::new, MINING_RIG_TIER_2_BLOCK).build();
	
	
	//Elite Mining Rig
	//public static final MiningRigBlock MINING_RIG_TIER_3_BLOCK = new MiningRigBlock(FabricBlockSettings.create().strength(50), 3);
	
	
	//Ultimate Mining Rig
	//public static final MiningRigBlock MINING_RIG_TIER_4_BLOCK = new MiningRigBlock(FabricBlockSettings.create().strength(50), 4);
	 */
	}
	
	public static ModConfig config;
	public static Map<String, Integer> miningRigPowerUse = new HashMap<>();

	@Override
	public void onInitialize() {

		AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
		//idk, cloth config isnt letting me access any of the configs anywhere except in here so... if anyone knows a fix make a pull request or issue?
		miningRigPowerUse.put("1", config.tier1MiningRigPowerUse);
		miningRigPowerUse.put("2", config.tier2MiningRigPowerUse);
		miningRigPowerUse.put("3", config.tier3MiningRigPowerUse);
		miningRigPowerUse.put("4", config.tier4MiningRigPowerUse);
		
		//sifter
		RegisterBlockItem("sifter", SIFTER_BLOCK, Rarity.UNCOMMON, ItemGroups.TOOLS);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, id("sifter_block_entity"), SIFTER_BLOCK_ENTITY);
		registerRecipe("sifter", SIFTER_RECIPE_TYPE);
		
		//advanced sifter
		RegisterBlockItem("advanced_sifter", ADVANCED_SIFTER_BLOCK, Rarity.RARE, ItemGroups.TOOLS);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, id("advanced_sifter_block_entity"), ADVANCED_SIFTER_BLOCK_ENTITY);
		registerRecipe("advanced_sifter", ADVANCED_SIFTER_RECIPE_TYPE);
		EnergyStorage.SIDED.registerForBlockEntity((AdvancedSifterBlockEntity, direction) -> AdvancedSifterBlockEntity.energyStorage, ADVANCED_SIFTER_BLOCK_ENTITY);

		
		//basic mining rig
		RegisterBlockItem("basic_mining_rig", MINING_RIG.BASIC.CONTROLLER, Rarity.RARE, ItemGroups.TOOLS);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, id("basic_mining_rig_block_entity"), MINING_RIG.BASIC.BLOCK_ENTITY);
		registerRecipe("basic_mining_rig", MINING_RIG.BASIC.RECIPE_TYPE);
		MilkevsMultiBlockLibrary.typeList.add(MINING_RIG.BASIC.BLOCK_ENTITY);
		EnergyStorage.SIDED.registerForBlockEntity((MiningRigTier1BlockEntity, direction) -> MiningRigTier1BlockEntity.energyStorage, MINING_RIG.BASIC.BLOCK_ENTITY);
		RegisterBlockItem("basic_mining_rig_wall", MINING_RIG.BASIC.WALL, Rarity.RARE, ItemGroups.TOOLS);
		RegisterBlockItem("basic_mining_rig_glass", MINING_RIG.BASIC.GLASS, Rarity.RARE, ItemGroups.TOOLS);
		RegisterBlockItem("basic_mining_rig_io", MINING_RIG.BASIC.IO, Rarity.RARE, ItemGroups.TOOLS);
		/*
		//advanced mining rig
		Registry.register(Registries.BLOCK_ENTITY_TYPE, id("mining_rig_tier_2_block_entity"), MINING_RIG_TIER_2_BLOCK_ENTITY);
		RegisterBlock("mining_rig_tier_2", MINING_RIG_TIER_2_BLOCK, Rarity.RARE, ItemGroups.TOOLS);
		//Registry.register(Registries.RECIPE_SERIALIZER, MiningRigTier2RecipeSerializer.id, MiningRigTier2RecipeSerializer.INSTANCE);
		EnergyStorage.SIDED.registerForBlockEntity((MiningRigTier2BlockEntity, direction) -> MiningRigTier2BlockEntity.energyStorage, MINING_RIG_TIER_2_BLOCK_ENTITY);
		
		//elite mining rig
		
		//ultimate mining rig
		
		*/
		
		System.out.println(MOD_ID + " Initialized");
	}

	public static Identifier id(String id) {
		return Identifier.of(MOD_ID, id);
	}

	public void RegisterItem(String id, Item object, RegistryKey<ItemGroup> group) {
		Registry.register(Registries.ITEM, id(id), object);
		AddToGroup(group, object);
	}

	public void RegisterBlockItem(String ID, Block block, Rarity rarity, RegistryKey<ItemGroup> group) {
		Registry.register(Registries.BLOCK, id(ID), block);
		RegisterItem(ID, new BlockItem(block, new Item.Settings().rarity(rarity)), group);
	}
	
	public void RegisterColoredBlock(String ID, Block block, Rarity rarity, RegistryKey<ItemGroup> group) {
		RegisterBlockItem(ID + "_white", block, rarity, group);
		RegisterBlockItem(ID + "_brown", block, rarity, group);
		RegisterBlockItem(ID + "_black", block, rarity, group);
		RegisterBlockItem(ID + "_blue", block, rarity, group);
		RegisterBlockItem(ID + "_cyan", block, rarity, group);
		RegisterBlockItem(ID + "_gray", block, rarity, group);
		RegisterBlockItem(ID + "_green", block, rarity, group);
		RegisterBlockItem(ID + "_light_blue", block, rarity, group);
		RegisterBlockItem(ID + "_light_gray", block, rarity, group);
		RegisterBlockItem(ID + "_lime", block, rarity, group);
		RegisterBlockItem(ID + "_magenta", block, rarity, group);
		RegisterBlockItem(ID + "_orange", block, rarity, group);
		RegisterBlockItem(ID + "_pink", block, rarity, group);
		RegisterBlockItem(ID + "_purple", block, rarity, group);
		RegisterBlockItem(ID + "_red", block, rarity, group);
		RegisterBlockItem(ID + "_yellow", block, rarity, group);
	}

	public void AddToGroup(RegistryKey<ItemGroup> group, ItemConvertible item) {
		ItemGroupEvents.modifyEntriesEvent(group).register(content -> {
			content.add(item);
		});
	}
	
	static <T extends Recipe<?>> RecipeType<T> registerRecipe(final String string, RecipeType<T> recipeType) {
        return Registry.register(Registries.RECIPE_TYPE, id(string), recipeType);
    }
	
	public static String makeTranslation(String string) {
		return MOD_ID + "." + string;
	}
	
	public static <T extends ScreenHandler, D extends CustomPayload> ExtendedScreenHandlerType<T, D> registerScreen(String name, ExtendedScreenHandlerType.ExtendedFactory<T, D> factory, PacketCodec<? super RegistryByteBuf, D> codec) {
		return Registry.register(Registries.SCREEN_HANDLER, id(name), new ExtendedScreenHandlerType<>(factory, codec));
	}

}
