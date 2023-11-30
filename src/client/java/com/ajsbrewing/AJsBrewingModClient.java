package com.ajsbrewing;

import com.ajsbrewing.blocks.CookingPot;
import com.ajsbrewing.blocks.CookingPotEntity;
import com.ajsbrewing.items.VialItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.potion.PotionUtil;

public class AJsBrewingModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
					if (tintIndex == 0)
						return VialItem.getColor( PotionUtil.getPotionEffects(stack));
					return 0xFFFFFF;
				}
				, VialItem.INSTANCE);
		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
			if (view.getBlockEntity(pos) instanceof CookingPotEntity pot) {
				return pot.getColor();

			}
			return 0xFFFFFF;
		}, CookingPot.INSTANCE);
	}
}