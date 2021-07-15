/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.placeholder;

import org.spongepowered.api.placeholder.PlaceholderParser;

public class PlaceholderMetadata {
    private final String token;
    private final PlaceholderParser parser;
    private final boolean document;
    private final boolean isDuplicate;

    PlaceholderMetadata(final String token, final PlaceholderParser parser, final boolean document, final boolean isDuplicate) {
        this.token = token;
        this.parser = parser;
        this.document = document;
        this.isDuplicate = isDuplicate;
    }

    public String getToken() {
        return this.token;
    }

    public PlaceholderParser getParser() {
        return this.parser;
    }

    public boolean isDocument() {
        return this.document;
    }

    public boolean isDuplicate() {
        return this.isDuplicate;
    }
}
