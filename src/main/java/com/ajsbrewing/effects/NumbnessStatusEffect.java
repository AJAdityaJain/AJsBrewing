package com.ajsbrewing.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class NumbnessStatusEffect extends StatusEffect {

    public static final NumbnessStatusEffect INSTANCE = new NumbnessStatusEffect();

    public NumbnessStatusEffect() {

        super(
                StatusEffectCategory.HARMFUL,
                0xde1443); // color in RGB
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }


}