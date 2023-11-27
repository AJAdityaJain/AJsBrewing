package com.ajsbrewing.mixin.client;

import com.ajsbrewing.AJsBrewingMod;
import com.ajsbrewing.effects.NumbnessStatusEffect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class HudClientMixin {

    @Unique
    private static final Identifier NUMBNESS_HEARTS = new Identifier(AJsBrewingMod.MOD_ID, "textures/gui/numbness_hearts.png");

    @Inject(method = "drawHeart", at = @At("HEAD"), cancellable = true)
    private void drawCustomHeart(DrawContext context, @Coerce Object type, int x, int y, boolean hardcore, boolean blinking, boolean half, CallbackInfo ci) {
        if (
                !blinking &&
                        MinecraftClient.getInstance().cameraEntity instanceof PlayerEntity player &&
                        (player.hasStatusEffect(NumbnessStatusEffect.INSTANCE))
        ) {

            if(!type.toString().equals("CONTAINER")) {

                if (half)
                    context.drawTexture(NUMBNESS_HEARTS, x, y, 9, 0, 9, 9);
                else
                    context.drawTexture(NUMBNESS_HEARTS, x, y, 0, 0, 9, 9);
                ci.cancel();
            }
        }
    }
}

