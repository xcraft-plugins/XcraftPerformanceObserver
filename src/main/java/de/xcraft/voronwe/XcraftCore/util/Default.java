/*
 * Decompiled with CFR 0.139.
 */
package de.xcraft.voronwe.XcraftCore.util;

import java.util.Optional;

public class Default<T> {
    private T value;

    public Default(T input, T fallback) {
        this.value = input == null ? fallback : input;
    }

    public Default(Optional<T> input, T fallback) {
        this.value = input.orElse(fallback);
    }

    public T get() {
        return this.value;
    }
}

