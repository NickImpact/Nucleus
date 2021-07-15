/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class NucleusJavaProperties {

    public static final boolean DEBUG_MODE = NucleusJavaProperties.isPropertyActive("nucleus.debug-mode");
    public static final @Nullable String DOCGEN_PATH = System.getProperty("nucleus.docgen");
    public static final boolean RUN_DOCGEN = DOCGEN_PATH != null;

    public static boolean isPropertyActive(final String property) {
        return System.getProperty(property, "false").equals("true");
    }

    private NucleusJavaProperties() { }

}
