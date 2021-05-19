package com.marshoepial.ziplines.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class ItemRegistrar {
    public static final Item ROPE = new RopeItem(new Item.Settings().group(ItemGroup.MATERIALS));
}
