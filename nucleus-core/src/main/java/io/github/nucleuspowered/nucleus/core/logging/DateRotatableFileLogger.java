/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.logging;

import io.github.nucleuspowered.nucleus.core.Util;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

public class DateRotatableFileLogger implements Closeable {

    private final static Path nucleusBase = Paths.get("logs/nucleus");

    private final Path directory;
    private final String filenamePrefix;
    private Instant currentDate;
    private LogFile file;
    private final Function<String, String> formatter;
    private boolean isClosed = false;

    public DateRotatableFileLogger(final String directory, final String filenamePrefix, final Function<String, String> formatter) throws IOException {
        Objects.requireNonNull(directory);
        Objects.requireNonNull(filenamePrefix);

        this.directory = nucleusBase.resolve(directory);
        this.filenamePrefix = filenamePrefix;
        this.formatter = formatter == null ? s -> s : formatter;
        Files.createDirectories(this.directory);
    }

    private void openFile() throws IOException {
        if (this.isClosed) {
            throw new IllegalStateException();
        }

        if (this.file != null && !this.file.isClosed()) {
            try {
                this.file.close();
            } finally {
                this.file = null;
            }
        }

        int count = 0;
        boolean go = false;
        String fileName;
        do {
            count++;
            fileName = this.directory.toString() + "/" + this.filenamePrefix + "-" + DateTimeFormatter.ofPattern("yyyy-MM-dd").format(Instant.now().atZone(ZoneId.systemDefault())) + "-" + count + ".log";
            final Path nextFile = Paths.get(fileName);
            if (Files.exists(nextFile)) {
                try {
                    Util.compressAndDeleteFile(nextFile);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            } else if (!Files.exists(Paths.get(fileName + ".gz"))) {
                this.file = new LogFile(nextFile, this.formatter);
                go = true;
            }
        } while(!go);

        this.currentDate = Instant.now().truncatedTo(ChronoUnit.DAYS);
    }

    public void logEntry(final String entry) throws IOException {
        if (this.isClosed) {
            throw new IllegalStateException();
        }

        this.logEntry(Collections.singleton(entry), true);
    }

    public void logEntry(final Iterable<String> entry) throws IOException {
        if (this.isClosed) {
            throw new IllegalStateException();
        }

        this.logEntry(entry, true);
    }

    private void logEntry(final Iterable<String> entry, final boolean retryOnError) throws IOException {
        if (this.file == null || this.file.isClosed() || Instant.now().truncatedTo(ChronoUnit.DAYS).isAfter(this.currentDate)) {
            this.openFile();
        }

        try {
            final Iterator<String> iterator = entry.iterator();
            while (iterator.hasNext()) {
                this.file.writeLine(iterator.next());
                iterator.remove();
            }

            this.file.flush();
        } catch (final IOException e) {
            if (retryOnError) {
                this.logEntry(entry, false);
            } else {
                throw e;
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (this.isClosed) {
            return;
        }

        if (this.file != null && !this.file.isClosed()) {
            this.file.close();
            this.file = null;
            this.isClosed = true;
        }
    }
}
