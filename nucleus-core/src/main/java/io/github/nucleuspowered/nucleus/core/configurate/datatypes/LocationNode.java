/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.configurate.datatypes;

import io.github.nucleuspowered.nucleus.api.core.exception.NoSuchWorldException;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.math.vector.Vector3d;

import java.util.Optional;

/**
 * We use this class in order to not just accidentally delete data when a world isn't available.
 */
@ConfigSerializable
public class LocationNode {

    @Setting private double x;

    @Setting private double y;

    @Setting private double z;

    @Setting private double rotx;

    @Setting private double roty;

    @Setting private double rotz;

    @Setting
    private ResourceKey world;

    public LocationNode() { }

    public LocationNode(final ServerLocation location) {
        this(location, Vector3d.ZERO);
    }

    public LocationNode(final ServerLocation location, final Vector3d rotation) {
        this(location.worldKey(), location.position(), rotation);
    }

    public LocationNode(final ResourceKey world, final Vector3d length, final Vector3d rotation) {
        this.x = length.x();
        this.y = length.y();
        this.z = length.z();
        this.rotx = rotation.x();
        this.roty = rotation.y();
        this.rotz = rotation.z();
        this.world = world;
    }

    public LocationNode copy() {
        return new LocationNode(this.world, this.getPosition(), this.getRotation());
    }

    public Vector3d getPosition() {
        return new Vector3d(this.x, this.y, this.z);
    }

    public ResourceKey getWorld() {
        return this.world;
    }

    /**
     * Gets a {@link Location} from the node.
     */
    public Optional<ServerLocation> getLocationIfExists() {
        return Sponge.server().worldManager().world(this.world).map(r -> ServerLocation.of(r, this.getPosition()));
    }

    /**
     * Gets a {@link Location} from the node.
     *
     * @return The Location
     */
    public ServerLocation getLocation() {
        return this.getLocationIfExists().orElseThrow(IllegalStateException::new);
    }

    public Vector3d getRotation() {
        return new Vector3d(this.rotx, this.roty, this.rotz);
    }
}
