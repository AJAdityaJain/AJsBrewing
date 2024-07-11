package com.ajsbrewing.blocks;

public enum Preparers {
    UNKNOWN(0),
    MAGMA(2),
    FIRE(3),
    SOUL_FIRE(4),
    LAVA(5);

    public final int value;

    Preparers(int value) {
        this.value = value;
    }
}
