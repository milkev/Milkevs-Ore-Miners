package net.milkev.milkevsoreminers.common.recipes;

import com.mojang.datafixers.util.Either;
import net.milkev.milkevsoreminers.common.MilkevsOreMiners;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class RecipeUtils {
    public static ItemStack handleDrop(List<String> output, World world) {
        List<ItemStack> stacks = new java.util.ArrayList<>(List.of());
        output.iterator().forEachRemaining(string -> { 
            if(string.charAt(0) == '#') {
                switch(string.substring(1).split(";")[0]) {
                    case "milkevsoreminers:sifter":
                        ItemStack stack = Registries.ITEM.get(Identifier.of(string.substring(1).split(";")[1])).getDefaultStack();
                        if (stack != null) {
                            Optional<RecipeEntry<SifterRecipe>> matches = world.getRecipeManager().getFirstMatch(MilkevsOreMiners.SIFTER_RECIPE_TYPE, new MilkevsSingleRecipeInput.Single(stack), world);
                            if(matches.isPresent()) {
                                matches.get().value().output().iterator().forEachRemaining(string1 -> {
                                    addStack(string1, stacks);
                                });
                            }
                        }
                        break; 
                    case "milkevsoreminers:advanced_sifter":
                        break;
                }
            } 
            else if(string.contains(";")) {
                addStack(string, stacks);
            }
        });
        return stacks.get(Random.create().nextBetween(0, stacks.size() - 1));
    }
    
    private static void addStack(String output, List<ItemStack> list) {
        //if use tag instead of item identifier, will just select the first item in the tag
        if(output.split(";")[1].charAt(0) == '#') {
            TagKey<Item> tagKey = TagKey.of(RegistryKeys.ITEM, Identifier.of(output.split(";")[1].substring(1)));
            List<Either<RegistryKey<Item>, Item>> tagList = Registries.ITEM.getEntryList(tagKey).map(entryList -> entryList.stream().map(RegistryEntry::getKeyOrValue).toList()).orElse(new ArrayList<>());
            tagList.get(0).ifLeft(left -> 
                    addStack(output.split(";")[0].concat(";").concat(left.getValue().toString()), list));
            
        } else {
            ItemStack stack = Registries.ITEM.get(Identifier.of(output.split(";")[1])).getDefaultStack();
            //if the item was not found in the registry, silently skip the entry
            if (stack != ItemStack.EMPTY && !Objects.equals(stack.getRegistryEntry().getIdAsString(), "minecraft:air")) {
                for (int i = 0; i < Integer.valueOf(output.split(";")[0]); i++) {
                    list.add(stack);
                }
            }
        }
    }
}
