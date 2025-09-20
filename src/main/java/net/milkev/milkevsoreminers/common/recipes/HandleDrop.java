package net.milkev.milkevsoreminers.common.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.List;

public class HandleDrop {
    public static ItemStack handleDrop(List<String> output) {
        List<ItemStack> stacks = new java.util.ArrayList<>(List.of());
        output.iterator().forEachRemaining(string -> {
            ItemStack stack = Registries.ITEM.get(Identifier.of(string.split(";")[1])).getDefaultStack();
            if(stack != null) {
                for(int i = 0; i < Integer.valueOf(string.split(";")[0]) ; i++)  {
                    stacks.add(stack);
                }
            }
        });
        return stacks.get(Random.create().nextBetween(0, stacks.size() - 1));
    }
}
