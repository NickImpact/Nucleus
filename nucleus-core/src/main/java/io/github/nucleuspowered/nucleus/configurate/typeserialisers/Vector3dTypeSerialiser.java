/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.configurate.typeserialisers;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.math.vector.Vector3d;

import java.lang.reflect.Type;

public class Vector3dTypeSerialiser implements TypeSerializer<Vector3d> {

    @Override
    public Vector3d deserialize(final Type type, final ConfigurationNode value) {
        return new Vector3d(value.getNode("rotx").getDouble(), value.getNode("roty").getDouble(), value.getNode("rotz").getDouble());
    }

    @Override
    public void serialize(final Type type, final Vector3d obj, final ConfigurationNode value) {
        if (obj != null) {
            value.getNode("rotx").setValue(obj.getX());
            value.getNode("roty").setValue(obj.getY());
            value.getNode("rotz").setValue(obj.getZ());
        }
    }

}
