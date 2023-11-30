package com.ajsbrewing;

import com.ajsbrewing.blocks.CookingPot;
import com.ajsbrewing.blocks.CookingPotEntity;
import com.ajsbrewing.effects.NumbnessStatusEffect;
import com.ajsbrewing.items.EmptyVialItem;
import com.ajsbrewing.items.VialItem;
import com.ajsbrewing.recipe.cooking.potion.PotionCookingRecipe;
import com.ajsbrewing.recipe.cooking.potion.PotionCookingRecipeSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AJsBrewingMod implements ModInitializer {

	public static final String MOD_ID = "ajsbrewing";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final BlockEntityType<CookingPotEntity> COOKING_POT_ENTITY_TYPE = Registry.register(
			Registries.BLOCK_ENTITY_TYPE,
			new Identifier(MOD_ID, "cooking_pot_entity"),
			FabricBlockEntityTypeBuilder.create(CookingPotEntity::new, CookingPot.INSTANCE).build()
	);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Mod");

		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "vial"), VialItem.INSTANCE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "empty_vial"), EmptyVialItem.INSTANCE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "cooking_pot"), CookingPot.ITEM_INSTANCE);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
			content.add(VialItem.INSTANCE);
			content.add(EmptyVialItem.INSTANCE);
			content.add(CookingPot.ITEM_INSTANCE);
		});

		LOGGER.info("Items Registered");

		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "cooking_pot"), CookingPot.INSTANCE);

		LOGGER.info("Blocks Registered");

		Registry.register(Registries.STATUS_EFFECT, new Identifier(MOD_ID, "numbness"), NumbnessStatusEffect.INSTANCE);
		LOGGER.info("Status Effects Registered");

		Registry.register(Registries.RECIPE_SERIALIZER, PotionCookingRecipeSerializer.ID, PotionCookingRecipeSerializer.INSTANCE);
		Registry.register(Registries.RECIPE_TYPE, new Identifier("ajsbrewing", PotionCookingRecipe.Type.ID), PotionCookingRecipe.Type.INSTANCE);

		LOGGER.info("Recipes Registered");

		LOGGER.info("Mod Finished Initializing");
	}
}