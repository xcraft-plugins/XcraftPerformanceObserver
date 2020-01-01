/*
 * Decompiled with CFR 0.139.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 */
package de.xcraft.voronwe.XcraftCore.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.bukkit.Material;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface Event {
    public String event();

    public String[] permission() default {};

    public String permissionMessage() default "";

    public boolean ignoreCancelled() default false;

    public Material[] block() default {};

    public boolean enabled() default true;
}

