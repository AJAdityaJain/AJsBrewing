package com.ajsbrewing.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.random.LocalRandom;

import java.util.Objects;

public class FieryStatusEffect extends StatusEffect {

    public static final FieryStatusEffect INSTANCE = new FieryStatusEffect();
    public static final LocalRandom RANDOM = new LocalRandom(123);

    public FieryStatusEffect() {

        super(
                StatusEffectCategory.HARMFUL,
                0xde4314); // color in RGB
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
        entity.setOnFireFor(1);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        int amp = (amplifier +2);
        if (entity.isOnFire() && !entity.isFireImmune()) {
            for (int i = 0; i < amp; i++) {
                double theta = RANDOM.nextFloat() * Math.PI * 2;
                double phi = RANDOM.nextFloat() * Math.PI * 2;
                double sinTheta = Math.sin(theta);


                entity.getWorld().addParticle(ParticleTypes.FLAME,
                        entity.getX() + (amp * sinTheta * Math.cos(phi)),
                        entity.getY() + (amp * sinTheta * Math.sin(phi)) + 1,
                        entity.getZ() + (amp * Math.cos(theta)),
                        0.0, 0.0, 0.0);
            }

            entity.getWorld().getEntitiesByClass(LivingEntity.class,
                    entity.getBoundingBox().expand(amplifier)
            , Objects::nonNull).forEach(e -> {
                if(e.hasStatusEffect(ChillStatusEffect.INSTANCE)){
                    e.removeStatusEffect(ChillStatusEffect.INSTANCE);
                    e.removeStatusEffect(INSTANCE);
                }

                e.setOnFireFor(1);
            });
        }
    }
}