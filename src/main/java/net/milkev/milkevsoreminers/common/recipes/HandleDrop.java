package net.milkev.milkevsoreminers.common.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

public class HandleDrop {
    public static ItemStack handleDrop(List<String> output) {
        return Registries.ITEM.get(Identifier.of(output.get(0).split(";")[1])).getDefaultStack();
    }
}
