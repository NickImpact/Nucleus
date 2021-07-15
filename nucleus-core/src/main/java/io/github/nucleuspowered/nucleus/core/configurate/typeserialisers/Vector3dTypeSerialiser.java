/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.configurate.typeserialisers;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.math.vector.Vector3d;

import java.lang.reflect.Type;

public class Vector3dTypeSerialiser implements TypeSerializer<Vector3d> {

    @Override
    public Vector3d deserialize(final Type type, final ConfigurationNode value) {
        return new Vector3d(value.node("rotx").getDouble(), value.node("roty").getDouble(), value.node("rotz").getDouble());
    }

    @Override
    public void serialize(final Type type, final Vector3d obj, final ConfigurationNode value) throws SerializationException {
        if (obj != null) {
            value.node("rotx").set(obj.x());
            value.node("roty").set(obj.y());
            value.node("rotz").set(obj.z());
        }
    }

}
