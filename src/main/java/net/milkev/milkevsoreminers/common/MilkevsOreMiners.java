package net.milkev.milkevsoreminers.common;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.milkev.milkevsmultiblocklibrary.common.MilkevsMultiBlockLibrary;
import net.milkev.milkevsoreminers.common.blockEntities.AdvancedSifterBlockEntity;
import net.milkev.milkevsoreminers.common.blockEntities.SifterBlockEntity;
import net.milkev.milkevsoreminers.common.blockEntities.miningRig.MiningRigTier1BlockEntity;
import net.milkev.milkevsoreminers.common.blocks.AdvancedSifterBlock;
import net.milkev.milkevsoreminers.common.blocks.MiningRigBlock;
import net.milkev.milkevsoreminers.common.blocks.SifterBlock;
import net.milkev.milkevsoreminers.common.recipes.AdvancedSifterRecipe;
import net.milkev.milkevsoreminers.common.recipes.SifterRecipe;
import net.milkev.milkevsoreminers.common.recipes.miningRig.MiningRigTier1Recipe;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.HashMap;
import java.util.Map;

public class MilkevsOreMiners implements ModInitializer {

	public static final String MOD_ID = "milkevsoreminers";
	
	//Sifter
	public static final SifterBlock SIFTER_BLOCK = new SifterBlock(AbstractBlock.Settings.create().strength(50));
	public static final BlockEntityType<SifterBlockEntity> SIFTER_BLOCK_ENTITY = BlockEntityType.Builder.create(SifterBlockEntity::new, SIFTER_BLOCK).build();
	public static final RecipeSerializer<SifterRecipe> SIFTER_RECIPE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, id("sifter"), new SifterRecipe.MyRecipeSerializer());
	public static final RecipeType<SifterRecipe> SIFTER_RECIPE_TYPE = new RecipeType<>() {
		@Override
		public String toString() {
			return "sifter";
		}
	};
			
	
	
	public static final AdvancedSifterBlock ADVANCED_SIFTER_BLOCK = new AdvancedSifterBlock(AbstractBlock.Settings.create().strength(50));
	public static final BlockEntityType<AdvancedSifterBlockEntity> ADVANCED_SIFTER_BLOCK_ENTITY = BlockEntityType.Builder.create(AdvancedSifterBlockEntity::new, ADVANCED_SIFTER_BLOCK).build();
	public static final RecipeSerializer<AdvancedSifterRecipe> ADVANCED_SIFTER_RECIPE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, id("advanced_sifter"), new AdvancedSifterRecipe.MyRecipeSerializer());
	public static final RecipeType<AdvancedSifterRecipe> ADVANCED_SIFTER_RECIPE_TYPE = new RecipeType<>() {
		@Override
		public String toString() {return "advanced_sifter";}
	};
	
	
	//Tier 1
	public static final MiningRigBlock MINING_RIG_TIER_1_BLOCK = new MiningRigBlock(AbstractBlock.Settings.create().strength(50), 1);
	public static final BlockEntityType<MiningRigTier1BlockEntity> MINING_RIG_TIER_1_BLOCK_ENTITY = BlockEntityType.Builder.create(MiningRigTier1BlockEntity::new, MINING_RIG_TIER_1_BLOCK).build();
	public static final RecipeSerializer<MiningRigTier1Recipe> MINING_RIG_TIER_1_RECIPE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, id("mining_rig_tier_1"), new MiningRigTier1Recipe.MyRecipeSerializer());
	public static final RecipeType<MiningRigTier1Recipe> MINING_RIG_TIER_1_RECIPE_TYPE = new RecipeType<MiningRigTier1Recipe>() {
		@Override
		public String toString() {return "mining_rig_tier_1";}
	};
	/*
	//Tier 2
	public static final MiningRigBlock MINING_RIG_TIER_2_BLOCK = new MiningRigBlock(AbstractBlock.Settings.create().strength(50), 2);
	public static final BlockEntityType<MiningRigTier2BlockEntity> MINING_RIG_TIER_2_BLOCK_ENTITY = BlockEntityType.Builder.create(MiningRigTier2BlockEntity::new, MINING_RIG_TIER_2_BLOCK).build();
	//Tier 3
	//public static final MiningRigBlock MINING_RIG_TIER_3_BLOCK = new MiningRigBlock(FabricBlockSettings.create().strength(50), 3);
	//Tier 4
	//public static final MiningRigBlock MINING_RIG_TIER_4_BLOCK = new MiningRigBlock(FabricBlockSettings.create().strength(50), 4);
	
	//Mining Rig Walls
	public static final Block RIG_WALL_PINK = new Block(AbstractBlock.Settings.create().strength(50));
	//walls; colored versions?
	
	//Mining Rig; beaminizer, glass, highlight, io, upgrades
	
	
	 */
	
	public static ModConfig config;
	public static Map<String, Integer> miningRigPowerUse = new HashMap<>();

	@Override
	public void onInitialize() {

		AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
		miningRigPowerUse.put("1", config.tier1MiningRigPowerUse);
		
		RegisterBlock("sifter", SIFTER_BLOCK, Rarity.UNCOMMON, ItemGroups.TOOLS);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, id("sifter_block_entity"), SIFTER_BLOCK_ENTITY);
		registerRecipe("sifter", SIFTER_RECIPE_TYPE);
		
		
		RegisterBlock("advanced_sifter", ADVANCED_SIFTER_BLOCK, Rarity.RARE, ItemGroups.TOOLS);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, id("advanced_sifter_block_entity"), ADVANCED_SIFTER_BLOCK_ENTITY);
		registerRecipe("advanced_sifter", ADVANCED_SIFTER_RECIPE_TYPE);
		//EnergyStorage.SIDED.registerForBlockEntity((AdvancedSifterBlockEntity, direction) -> AdvancedSifterBlockEntity.energyStorage, ADVANCED_SIFTER_BLOCK_ENTITY);

		
		//Tier 1
		RegisterBlock("mining_rig_tier_1", MINING_RIG_TIER_1_BLOCK, Rarity.RARE, ItemGroups.TOOLS);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, id("mining_rig_tier_1_block_entity"), MINING_RIG_TIER_1_BLOCK_ENTITY);
		registerRecipe("mining_rig_tier_1", MINING_RIG_TIER_1_RECIPE_TYPE);
		MilkevsMultiBlockLibrary.typeList.add(MINING_RIG_TIER_1_BLOCK_ENTITY);
		//EnergyStorage.SIDED.registerForBlockEntity((MiningRigTier1BlockEntity, direction) -> MiningRigTier1BlockEntity.energyStorage, MINING_RIG_TIER_1_BLOCK_ENTITY);
		/*
		//Tier 2
		Registry.register(Registries.BLOCK_ENTITY_TYPE, id("mining_rig_tier_2_block_entity"), MINING_RIG_TIER_2_BLOCK_ENTITY);
		RegisterBlock("mining_rig_tier_2", MINING_RIG_TIER_2_BLOCK, Rarity.RARE, ItemGroups.TOOLS);
		//Registry.register(Registries.RECIPE_SERIALIZER, MiningRigTier2RecipeSerializer.id, MiningRigTier2RecipeSerializer.INSTANCE);
		EnergyStorage.SIDED.registerForBlockEntity((MiningRigTier2BlockEntity, direction) -> MiningRigTier2BlockEntity.energyStorage, MINING_RIG_TIER_2_BLOCK_ENTITY);
		
		RegisterBlock("rig_wall_pink", RIG_WALL_PINK, Rarity.COMMON, ItemGroups.BUILDING_BLOCKS);
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

	public void RegisterBlock(String ID, Block block, Rarity rarity, RegistryKey<ItemGroup> group) {
		Registry.register(Registries.BLOCK, id(ID), block);
		Registry.register(Registries.ITEM, id(ID), new BlockItem(block, new Item.Settings().rarity(rarity)));
		AddToGroup(group, block);
	}

	public void AddToGroup(RegistryKey<ItemGroup> group, ItemConvertible item) {
		ItemGroupEvents.modifyEntriesEvent(group).register(content -> {
			content.add(item);
		});
	}
	
	static <T extends Recipe<?>> RecipeType<T> registerRecipe(final String string, RecipeType<T> recipeType) {
        return Registry.register(Registries.RECIPE_TYPE, id(string), recipeType);
    }

}
