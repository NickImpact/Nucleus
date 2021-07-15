/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.gradle.task

import org.gradle.api.tasks.AbstractExecTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets

open class StdOutExec : AbstractExecTask<StdOutExec>(StdOutExec::class.java) {

    @Internal
    val variableWriter: ByteArrayOutputStream = ByteArrayOutputStream()

    init {
        super.setStandardOutput(this.variableWriter)
    }

    @Internal
    var result: String? = null

    @TaskAction
    override fun exec() {
        super.exec()
        result = variableWriter.toString(StandardCharsets.UTF_8.toString()).trim()
        variableWriter.reset()
    }

    override fun setStandardOutput(outputStream: OutputStream): StdOutExec {
        // noop
        return this
    }

}