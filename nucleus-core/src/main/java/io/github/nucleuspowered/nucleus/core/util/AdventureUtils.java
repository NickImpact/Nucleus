/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public final class AdventureUtils {

    public static boolean isEmpty(final Component component) {
        return component == Component.empty();
    }

    public static String getContent(final Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

}
