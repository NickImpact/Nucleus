/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.interfaces;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.services.impl.docgen.DocumentationGenerationService;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;
import java.nio.file.Path;

@ImplementedBy(DocumentationGenerationService.class)
public interface IDocumentationGenerationService {

    void generate(Path directory) throws IOException, ObjectMappingException;

}
