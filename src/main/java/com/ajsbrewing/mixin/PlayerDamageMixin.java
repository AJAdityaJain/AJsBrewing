package com.ajsbrewing.mixin;

import com.ajsbrewing.data.PlayerData;
import com.ajsbrewing.data.StateSaverAndLoader;
import com.ajsbrewing.effects.NumbnessStatusEffect;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// PlayerDamageMixin.java
@Mixin(PlayerEntity.class)
public abstract class PlayerDamageMixin {
  @Inject(
            method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z",
            at = @At("HEAD"),
            cancellable = true)
    private void onPlayerDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {

        PlayerEntity entity = (PlayerEntity) (Object) this;

        if (entity.hasStatusEffect(NumbnessStatusEffect.INSTANCE)) {
            PlayerData playerState = StateSaverAndLoader.getPlayerState(entity);
            playerState.numbness += amount;
            cir.setReturnValue(false);
            cir.cancel();
        } else {
            PlayerData playerState = StateSaverAndLoader.getPlayerState(entity);
            if(playerState.numbness > 0){
                entity.setHealth(entity.getHealth() - ((playerState.numbness*2)+amount));
                playerState.numbness = 0;
            }
        }
    }
}