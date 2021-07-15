/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.home;

import io.github.nucleuspowered.nucleus.api.module.home.data.Home;
import io.github.nucleuspowered.nucleus.api.module.home.exception.HomeException;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Provides information about user homes.
 */
public interface NucleusHomeService {

    /**
     * The default home name.
     */
    String DEFAULT_HOME_NAME = "home";

    /**
     * The name of the permission option that will contain the number of homes a player
     * may have.
     */
    String HOME_COUNT_OPTION = "home-count";

    /**
     * The pattern that all home names must follow.
     */
    Pattern HOME_NAME_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]{1,15}$");

    /**
     * Gets the number of homes the player currently has.
     *
     * @param user The {@link UUID} of the player
     * @return The number of homes.
     */
    default int getHomeCount(final UUID user) {
        return this.getHomes(user).size();
    }

    /**
     * Gets the {@link Home}s for the specified user, identified by their UUID.
     *
     * @param user The {@link UUID}
     * @return The homes.
     */
    List<Home> getHomes(UUID user);

    /**
     * Gets the {@link Home}s for the specified user, identified by their UUID.
     *
     * @param user The {@link UUID}
     * @return The homes.
     */
    default List<Home> getHomes(final User user) {
        return this.getHomes(user.uniqueId());
    }

    /**
     * Gets a specified home of the user, if it exists.
     *
     * @param user The {@link UUID} of the user to get the home for.
     * @param name The name of the home.
     * @return The {@link Home}, if it exists.
     */
    Optional<Home> getHome(UUID user, String name);

    /**
     * Creates a home. This is subject to Nucleus' standard checks.
     *
     * <p>
     *     Home names must follow the regex defined by {@link #HOME_NAME_PATTERN}.
     * </p>
     *
     * @param user The {@link UUID} of the user to create the home for.
     * @param name The name of the home to create.
     * @param location The location of the home.
     * @param rotation The rotation of the player when they return to this home.
     * @throws HomeException if the home could not be created as the name is incorrect,
     *                       could not be created due to home limits, or if a plugin
     *                       cancelled the creation event.
     */
    void createHome(final UUID user, final String name, final ServerLocation location, final Vector3d rotation) throws HomeException;

    /**
     * Modifies a home's location.
     *
     * @param user The {@link UUID} of the user to modify the home for.
     * @param name The name of the home to modify.
     * @param location The location of the home.
     * @param rotation The rotation of the player when they return to this home.
     * @throws HomeException if the home could not be found or if a plugin cancelled
     *                      the event.
     */
    void modifyHome(final UUID user, final String name, final ServerLocation location, final Vector3d rotation) throws HomeException;

    /**
     * Modifies a home's location.
     *
     * @param home The {@link Home} to modify.
     * @param location The location of the home.
     * @param rotation The rotation of the player when they return to this home.
     * @throws HomeException if the home could not be found, or a plugin cancelled the event.
     */
    void modifyHome(Home home, ServerLocation location, Vector3d rotation) throws HomeException;

    /**
     * Modifies a home's location, if it exists, otherwise creates a home. This is subject to Nucleus' standard checks.
     *
     * @param user The {@link UUID} of the user to modify the home for.
     * @param name The name of the home to modify or create.
     * @param location The location of the home.
     * @param rotation The rotation of the player when they return to this home.
     * @throws HomeException if the home could not be created, due to home limits, or a plugin cancelled the event.
     */
    default void modifyOrCreateHome(final UUID user, final String name, final ServerLocation location, final Vector3d rotation) throws HomeException {
        if (this.getHome(user, name).isPresent()) {
            this.modifyHome(user, name, location, rotation);
        } else {
            this.createHome(user, name, location, rotation);
        }
    }

    /**
     * Removes a home.
     *
     * @param user The {@link UUID} of the user to remove the home of.
     * @param name The name of the home to remove.
     * @throws HomeException if the home could not be found, or a plugin cancelled the event.
     */
    void removeHome(final UUID user, final String name) throws HomeException;

    /**
     * Returns the maximum number of homes the player can have.
     *
     * @param uuid The {@link UUID} of the player.
     * @return The number of homes, or {@link Integer#MAX_VALUE} if unlimited.
     *
     * @throws IllegalArgumentException if the user cannot be found
     */
    int getMaximumHomes(UUID uuid) throws IllegalArgumentException;

    /**
     * Returns the maximum number of homes the player can have.
     *
     * @param user The {@link User}.
     * @return The number of homes, or {@link Integer#MAX_VALUE} if unlimited.
     */
    int getMaximumHomes(User user);
}
