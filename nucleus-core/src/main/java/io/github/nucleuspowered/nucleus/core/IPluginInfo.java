/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core;

public interface IPluginInfo {

    String id();

    String name();

    String version();

    String[] validVersions();

    String description();

    String url();

    String gitHash();
}
