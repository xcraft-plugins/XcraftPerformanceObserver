/*
 * Decompiled with CFR 0.139.
 */
package de.xcraft.voronwe.XcraftCore.command;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface Command {
    public String command();

    public String description() default "";

    public String[] permission() default {};

    public String[] aliases() default {};

    public String usage() default "";

    public String permissionMessage() default "";

    public boolean enabled() default true;
}

