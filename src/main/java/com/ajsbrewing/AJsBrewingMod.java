package com.ajsbrewing;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AJsBrewingMod implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("AJsBrewing");
	public static final VialItem VIAL = new VialItem(new FabricItemSettings()
			.maxDamage(16)
			.rarity(Rarity.UNCOMMON)
	);
	public static final EmptyVialItem EMPTY_VIAL = new EmptyVialItem(new FabricItemSettings());

	public static final StatusEffect EXP = new ExpStatusEffect();

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Mod");

 		Registry.register(Registries.ITEM, new Identifier("ajsbrewing", "vial"), VIAL);
		Registry.register(Registries.ITEM, new Identifier("ajsbrewing", "empty_vial"), EMPTY_VIAL);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
			content.add(VIAL);
			content.add(EMPTY_VIAL);
		});

		LOGGER.info("Items Registered");


		Registry.register(Registries.STATUS_EFFECT, new Identifier("ajsbrewing", "exp"), EXP);


		LOGGER.info("Mod Finished Initializing");
	}
}