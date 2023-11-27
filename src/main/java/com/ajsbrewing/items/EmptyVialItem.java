package com.ajsbrewing.items;

import net.minecraft.item.Item;

public class EmptyVialItem extends Item{
    public static EmptyVialItem INSTANCE = new EmptyVialItem(new Item.Settings());
    public EmptyVialItem(Settings settings) {
        super(settings);
    }
}