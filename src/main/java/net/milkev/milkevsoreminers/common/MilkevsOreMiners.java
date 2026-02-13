package net.milkev.milkevsoreminers.common;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.milkev.milkevsmultiblocklibrary.common.MilkevsMultiBlockLibrary;
import net.milkev.milkevsoreminers.common.blockEntities.AdvancedSifterBlockEntity;
import net.milkev.milkevsoreminers.common.blockEntities.SifterBlockEntity;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.AdvancedMiningRigBlockEntity;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.BasicMiningRigBlockEntity;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.Slottable.BaseMiningRigStorageBlockEntity;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.Slottable.MiningRigStorageBlockEntityAdvanced;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.Slottable.MiningRigStorageBlockEntityBasic;
import net.milkev.milkevsoreminers.common.blocks.AdvancedSifterBlock;
import net.milkev.milkevsoreminers.common.blocks.miningRig.AdvancedMiningRigBlock;
import net.milkev.milkevsoreminers.common.blocks.miningRig.BasicMiningRigBlock;
import net.milkev.milkevsoreminers.common.blocks.miningRig.MiningRigBlock;
import net.milkev.milkevsoreminers.common.blocks.SifterBlock;
import net.milkev.milkevsoreminers.common.blocks.miningRig.Slottable.MiningRigStorageBlock;
import net.milkev.milkevsoreminers.common.gui.AdvancedSifterScreenHandler;
import net.milkev.milkevsoreminers.common.gui.BasicMiningRigSceenHandler;
import net.milkev.milkevsoreminers.common.recipes.AdvancedSifterRecipe;
import net.milkev.milkevsoreminers.common.recipes.SifterRecipe;
import net.milkev.milkevsoreminers.common.recipes.miningRig.AdvancedMiningRigRecipe;
import net.milkev.milkevsoreminers.common.recipes.miningRig.BasicMiningRigRecipe;
import net.milkev.milkevsoreminers.common.util.BlockPosPayload;
import net.minecraft.block.AbstractBlock;
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
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import team.reborn.energy.api.EnergyStorage;

import java.util.HashMap;
import java.util.Map;

public class MilkevsOreMiners implements ModInitializer {

	public static final String MOD_ID = "milkevsoreminers";
	
	//Sifter
	public static final SifterBlock SIFTER_BLOCK = new SifterBlock(AbstractBlock.Settings.create().strength(4, 5).nonOpaque());
	public static final BlockEntityType<SifterBlockEntity> SIFTER_BLOCK_ENTITY = BlockEntityType.Builder.create(SifterBlockEntity::new, SIFTER_BLOCK).build();
	public static final RecipeSerializer<SifterRecipe> SIFTER_RECIPE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, id("sifter"), new SifterRecipe.MyRecipeSerializer());
	public static final RecipeType<SifterRecipe> SIFTER_RECIPE_TYPE = new RecipeType<>() {
		@Override
		public String toString() {
			return "sifter";
		}
	};
	
	
	//Advanced Sifter
	public static final AdvancedSifterBlock ADVANCED_SIFTER_BLOCK = new AdvancedSifterBlock(AbstractBlock.Settings.create().strength(4, 5));
	public static final BlockEntityType<AdvancedSifterBlockEntity> ADVANCED_SIFTER_BLOCK_ENTITY = BlockEntityType.Builder.create(AdvancedSifterBlockEntity::new, ADVANCED_SIFTER_BLOCK).build();
	public static final RecipeSerializer<AdvancedSifterRecipe> ADVANCED_SIFTER_RECIPE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, id("advanced_sifter"), new AdvancedSifterRecipe.MyRecipeSerializer());
	public static final RecipeType<AdvancedSifterRecipe> ADVANCED_SIFTER_RECIPE_TYPE = new RecipeType<>() {
		@Override
		public String toString() {return "advanced_sifter";}
	};
	//screen
	public static final ScreenHandlerType<AdvancedSifterScreenHandler> ADVANCED_SIFTER_SCREEN_HANDLER = registerScreen("advanced_sifter", AdvancedSifterScreenHandler::new, BlockPosPayload.PACKET_CODEC);
	
	static public class MINING_RIG {
		static public class UPGRADES {
		}
		static public class BASIC {
			public static final String ID = "basic_mining_rig";
			public static final MiningRigBlock CONTROLLER = new BasicMiningRigBlock(AbstractBlock.Settings.create().strength(5, 6));
			public static final BlockEntityType<BasicMiningRigBlockEntity> BLOCK_ENTITY = BlockEntityType.Builder.create(BasicMiningRigBlockEntity::new, CONTROLLER).build();
			public static final ScreenHandlerType<BasicMiningRigSceenHandler> SCREEN_HANDLER = registerScreen(ID, BasicMiningRigSceenHandler::new, BlockPosPayload.PACKET_CODEC);
			public static final RecipeSerializer<BasicMiningRigRecipe> RECIPE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, id(ID), new BasicMiningRigRecipe.MyRecipeSerializer());
			public static final RecipeType<BasicMiningRigRecipe> RECIPE_TYPE = new RecipeType<BasicMiningRigRecipe>() {
				@Override
				public String toString() {
					return ID;
				}
			};
			public static final Block WALL = new Block(AbstractBlock.Settings.create().strength(5, 6));
			public static final Block GLASS = new TintedGlassBlock(AbstractBlock.Settings.create().nonOpaque().strength(3, 6));
			public static final MiningRigStorageBlock ITEM_STORAGE = new MiningRigStorageBlock(AbstractBlock.Settings.create().strength(5, 6), 1);
			public static final BlockEntityType<MiningRigStorageBlockEntityBasic> ITEM_STORAGE_BLOCK_ENTITY = BlockEntityType.Builder.create(MiningRigStorageBlockEntityBasic::new, ITEM_STORAGE).build();
		}
		static public class ADVANCED {
			public static final String ID = "advanced_mining_rig";
			public static final MiningRigBlock CONTROLLER = new AdvancedMiningRigBlock(AbstractBlock.Settings.create().strength(5.5f, 7));
			public static final BlockEntityType<AdvancedMiningRigBlockEntity> BLOCK_ENTITY = BlockEntityType.Builder.create(AdvancedMiningRigBlockEntity::new, CONTROLLER).build();
			public static final RecipeSerializer<AdvancedMiningRigRecipe> RECIPE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, id(ID), new AdvancedMiningRigRecipe.MyRecipeSerializer());
			public static final RecipeType<AdvancedMiningRigRecipe> RECIPE_TYPE = new RecipeType<AdvancedMiningRigRecipe>() {
				@Override
				public String toString() {return ID;}};
			public static final Block WALL = new Block(AbstractBlock.Settings.create().strength(5.5f, 7));
			public static final Block GLASS = new TintedGlassBlock(AbstractBlock.Settings.create().nonOpaque().strength(3.5f, 7));
			public static final MiningRigStorageBlock ITEM_STORAGE = new MiningRigStorageBlock(AbstractBlock.Settings.create().strength(5, 6), 2);
			public static final BlockEntityType<MiningRigStorageBlockEntityAdvanced> ITEM_STORAGE_BLOCK_ENTITY = BlockEntityType.Builder.create(MiningRigStorageBlockEntityAdvanced::new, ITEM_STORAGE).build();
		}
		static public class ELITE {
			public static final String ID = "elite_mining_rig";
			//public static final MiningRigBlock MINING_RIG_TIER_3_BLOCK = new MiningRigBlock(FabricBlockSettings.create().strength(50), 3);
		}
		static public class ULTIMATE {
			public static final String ID = "ultimate_mining_rig";
			//public static final MiningRigBlock MINING_RIG_TIER_4_BLOCK = new MiningRigBlock(FabricBlockSettings.create().strength(50), 4);
		}
	}
	
	public static ModConfig config;
	public static Map<String, Long> PowerCapacity = new HashMap<>();
	public static int validStructureCheckTimer;

	@Override
	public void onInitialize() {

		AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
		//idk, cloth config isnt letting me access any of the configs anywhere except in here so... if anyone knows a fix make a pull request or issue?
		PowerCapacity.put("sifter", config.advancedSifterPowerCapacity);
		PowerCapacity.put(MINING_RIG.BASIC.ID, config.basicMiningRigPowerCapacity);
		PowerCapacity.put(MINING_RIG.ADVANCED.ID, config.advancedMiningRigPowerCapacity);
		PowerCapacity.put(MINING_RIG.ELITE.ID, config.eliteMiningRigPowerCapacity);
		PowerCapacity.put(MINING_RIG.ULTIMATE.ID, config.ultimateMiningRigPowerCapacity);
		validStructureCheckTimer = config.validStructureCheckTimer;
		if(validStructureCheckTimer <= 0) {
			System.out.println("Milkevs Ore Miners Valid Structure Check Timer is disabled, please enable before reporting any issues.");
		}
		
		//sifter
			registerBlockItemEntity("sifter", SIFTER_BLOCK, Rarity.UNCOMMON, ItemGroups.TOOLS, SIFTER_BLOCK_ENTITY);
			//Registry.register(Registries.BLOCK_ENTITY_TYPE, id("sifter_block_entity"), SIFTER_BLOCK_ENTITY);
			registerRecipe("sifter", SIFTER_RECIPE_TYPE);
		
		//advanced sifter
			registerBlockItemEntity("advanced_sifter", ADVANCED_SIFTER_BLOCK, Rarity.RARE, ItemGroups.TOOLS, ADVANCED_SIFTER_BLOCK_ENTITY);
			registerRecipe("advanced_sifter", ADVANCED_SIFTER_RECIPE_TYPE);
			EnergyStorage.SIDED.registerForBlockEntity((AdvancedSifterBlockEntity, direction) -> AdvancedSifterBlockEntity.energyStorage, ADVANCED_SIFTER_BLOCK_ENTITY);
			
		//mining rig upgrades
		
		//basic mining rig
			registerBlockItemEntity("basic_mining_rig", MINING_RIG.BASIC.CONTROLLER, Rarity.RARE, ItemGroups.TOOLS, MINING_RIG.BASIC.BLOCK_ENTITY);
			registerRecipe("basic_mining_rig", MINING_RIG.BASIC.RECIPE_TYPE);
			MilkevsMultiBlockLibrary.registerMultiblock(MINING_RIG.BASIC.BLOCK_ENTITY);
			EnergyStorage.SIDED.registerForBlockEntity((basicMiningRigBlockEntity, direction) -> basicMiningRigBlockEntity.energyStorage, MINING_RIG.BASIC.BLOCK_ENTITY);
			registerBlockItem("basic_mining_rig_wall", MINING_RIG.BASIC.WALL, Rarity.RARE, ItemGroups.TOOLS);
			registerBlockItem("basic_mining_rig_glass", MINING_RIG.BASIC.GLASS, Rarity.RARE, ItemGroups.TOOLS);
			registerBlockItemEntity("io_storage_basic", MINING_RIG.BASIC.ITEM_STORAGE, Rarity.RARE, ItemGroups.TOOLS, MINING_RIG.BASIC.ITEM_STORAGE_BLOCK_ENTITY);
		
		//advanced mining rig
			registerBlockItemEntity("advanced_mining_rig", MINING_RIG.ADVANCED.CONTROLLER, Rarity.RARE, ItemGroups.TOOLS, MINING_RIG.ADVANCED.BLOCK_ENTITY);
			registerRecipe("advanced_mining_rig", MINING_RIG.ADVANCED.RECIPE_TYPE);
			MilkevsMultiBlockLibrary.registerMultiblock(MINING_RIG.ADVANCED.BLOCK_ENTITY);
			EnergyStorage.SIDED.registerForBlockEntity((advancedMiningRigBlockEntity, direction) -> advancedMiningRigBlockEntity.energyStorage, MINING_RIG.ADVANCED.BLOCK_ENTITY);
			registerBlockItem("advanced_mining_rig_wall", MINING_RIG.ADVANCED.WALL, Rarity.RARE, ItemGroups.TOOLS);
			registerBlockItem("advanced_mining_rig_glass", MINING_RIG.ADVANCED.GLASS, Rarity.RARE, ItemGroups.TOOLS);
			registerBlockItemEntity("io_storage_advanced", MINING_RIG.ADVANCED.ITEM_STORAGE, Rarity.RARE, ItemGroups.TOOLS, MINING_RIG.ADVANCED.ITEM_STORAGE_BLOCK_ENTITY);
			
		//elite mining rig
			
		//ultimate mining rig
		
		System.out.println(MOD_ID + " Initialized");
	}

	public static Identifier id(String id) {
		return Identifier.of(MOD_ID, id);
	}

	public void registerItem(String id, Item object, RegistryKey<ItemGroup> group) {
		Registry.register(Registries.ITEM, id(id), object);
		addToGroup(group, object);
	}

	public void registerBlockItem(String ID, Block block, Rarity rarity, RegistryKey<ItemGroup> group) {
		Registry.register(Registries.BLOCK, id(ID), block);
		registerItem(ID, new BlockItem(block, new Item.Settings().rarity(rarity)), group);
	}
	
	public void registerBlockItemEntity(String ID, Block block, Rarity rarity, RegistryKey<ItemGroup> group, BlockEntityType<?> blockEntityType) {
		registerBlockItem(ID, block, rarity, group);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, ID + "_block_entity", blockEntityType);
	}

	public void addToGroup(RegistryKey<ItemGroup> group, ItemConvertible item) {
		ItemGroupEvents.modifyEntriesEvent(group).register(content -> {
			content.add(item);
		});
	}
	
	static <T extends Recipe<?>> RecipeType<T> registerRecipe(final String string, RecipeType<T> recipeType) {
        return Registry.register(Registries.RECIPE_TYPE, id(string), recipeType);
    }
	
	public static MutableText makeTranslation(String string) {
		return net.minecraft.text.Text.translatable(MOD_ID + "." + string);
	}
	
	public static <T extends ScreenHandler, D extends CustomPayload> ExtendedScreenHandlerType<T, D> registerScreen(String name, ExtendedScreenHandlerType.ExtendedFactory<T, D> factory, PacketCodec<? super RegistryByteBuf, D> codec) {
		return Registry.register(Registries.SCREEN_HANDLER, id(name), new ExtendedScreenHandlerType<>(factory, codec));
	}

}
