/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.datatypes;

import io.github.nucleuspowered.nucleus.api.util.data.NamedLocation;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;

import java.text.MessageFormat;
import java.util.Optional;

public class LocationData implements NamedLocation {

    private final String warpName;
    private final ResourceKey resourceKey;
    private final Vector3d position;
    private final Vector3d rotation;

    public LocationData(final String name, final ResourceKey world, final Vector3d position, final Vector3d rotation) {
        this.rotation = rotation;
        this.position = position;
        this.warpName = name;
        this.resourceKey = world;
    }

    public String getName() {
        return this.warpName;
    }

    @Override
    public ResourceKey getResourceKey() {
        return this.resourceKey;
    }

    @Override public Optional<ServerWorld> getWorld() {
        return Sponge.server().worldManager().world(this.resourceKey);
    }

    public Vector3d getRotation() {
        return this.rotation;
    }

    @Override public Vector3d getPosition() {
        return this.position;
    }

    @Override public Optional<ServerLocation> getLocation() {
        return Sponge.server().worldManager().world(this.resourceKey).map(x -> x.location(this.position));
    }

    public String toLocationString() {
        return MessageFormat.format("name: {0}, world: {1}, x: {2}, y: {3}, z: {4}", this.warpName, this.resourceKey.asString(),
            (int) this.position.x(), (int) this.position.y(), (int) this.position.z());
    }
}
