package com.ajsbrewing;

import com.ajsbrewing.blocks.CookingPot;
import com.ajsbrewing.blocks.CookingPotEntity;
import com.ajsbrewing.items.VialItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.potion.PotionUtil;

public class AJsBrewingModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

		ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.OPEN_BOOK_PACKET_ID, (client, handler, buf, responseSender) -> {
			client.execute(() -> {
				client.setScreen(new WitchcraftTomeScreen(buf));
			});
//			AJsBrewingMod.LOGGER.info("Received packet to open book");
		});

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