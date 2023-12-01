package com.ajsbrewing.effects;

import net.minecraft.block.PowderSnowBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.random.LocalRandom;

import java.util.Objects;

public class ChillStatusEffect extends StatusEffect {

    public static final ChillStatusEffect INSTANCE = new ChillStatusEffect();
    public static final LocalRandom RANDOM = new LocalRandom(123);

    public ChillStatusEffect() {

        super(
                StatusEffectCategory.HARMFUL,
                0x14a1de); // color in RGB
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean isInstant() {
        return false;
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        entity.setFrozenTicks(10);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        int amp = (1+amplifier) * 2;
        if (entity.getFrozenTicks() > 0) {
            for (int i = 0; i < amp; i++) {
                double theta = RANDOM.nextFloat() * Math.PI * 2;
                double phi = RANDOM.nextFloat() * Math.PI * 2;
                double sinTheta = Math.sin(theta);


                entity.getWorld().addParticle(ParticleTypes.SNOWFLAKE,
                        entity.getX() + (amp * sinTheta * Math.cos(phi)),
                        entity.getY() + (amp * sinTheta * Math.sin(phi)) + 1,
                        entity.getZ() + (amp * Math.cos(theta)),
                        0.0, 0.0, 0.0);
            }

            entity.getWorld().getEntitiesByClass(LivingEntity.class,
                    entity.getBoundingBox().expand(amp)
            , (e) -> e.getUuid()!= entity.getUuid()).forEach(e -> {
                e.setFrozenTicks(e.getFrozenTicks()+10);
                if(e.isOnFire()){
                    e.extinguish();
                    if(e.hasStatusEffect(FieryStatusEffect.INSTANCE)){
                        e.removeStatusEffect(FieryStatusEffect.INSTANCE);
                    }
                }
                if(e.hasStatusEffect(FieryStatusEffect.INSTANCE)){
                    e.removeStatusEffect(FieryStatusEffect.INSTANCE);
                    e.removeStatusEffect(INSTANCE);
                }
                if(e.getStatusEffect(StatusEffects.SLOWNESS) == null){
                    e.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, amp));
                }
            });
        }
        if(!entity.isOnFire())
            entity.setFrozenTicks(entity.getFrozenTicks()+3);
    }
}