package com.ajsbrewing;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

public class AJsBrewingModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
					if (tintIndex == 0)
						return VialItem.getColor(stack);
					return 0xFFFFFF;
				}
				, AJsBrewingMod.VIAL);
	}
}