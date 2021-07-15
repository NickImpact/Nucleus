/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.logging;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class AbstractLoggingHandler implements IReloadableService.Reloadable {

    private static final DateTimeFormatter formatter = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault());
    private final IMessageProviderService messageProviderService;
    private final Logger slogger;
    protected DateRotatableFileLogger logger;
    private final List<String> queueEntry = new ArrayList<>();
    private final String directoryName;
    private final String filePrefix;
    private final Object locking = new Object();

    @Inject
    public AbstractLoggingHandler(final String directoryName,
            final String filePrefix,
            final IMessageProviderService messageProviderService,
            final Logger logger) {
        this.directoryName = directoryName;
        this.filePrefix = filePrefix;
        this.messageProviderService = messageProviderService;
        this.slogger = logger;
    }

    public void queueEntry(final String s) {
        if (this.logger != null) {
            synchronized (this.locking) {
                this.queueEntry.add(s);
            }
        }
    }

    public void onServerShutdown() throws IOException {
        this.onShutdown();
    }

    protected void onShutdown() throws IOException {
        if (this.logger != null) {
            this.logger.close();
            this.logger = null;
        }
    }

    protected abstract boolean enabledLog();

    public void onTick() {
        if (this.queueEntry.isEmpty()) {
            return;
        }

        final List<String> l;
        synchronized (this.locking) {
            l = new ArrayList<>(this.queueEntry);
            this.queueEntry.clear();
        }

        if (this.logger == null) {
            if (this.enabledLog()) {
                try {
                    this.createLogger();
                } catch (final IOException e) {
                    this.slogger.warn(this.messageProviderService.getMessageString("commandlog.couldnotwrite"));
                    e.printStackTrace();
                    return;
                }
            } else {
                return;
            }
        }

        try {
            this.writeEntry(l);
        } catch (final IOException e) {
            this.slogger.warn(this.messageProviderService.getMessageString("commandlog.couldnotwrite"));
            e.printStackTrace();
        }
    }

    protected void createLogger() throws IOException {
        this.logger = new DateRotatableFileLogger(this.directoryName, this.filePrefix, s -> "[" +
            formatter.format(Instant.now().atZone(ZoneOffset.systemDefault())) +
            "] " + s);
    }

    private void writeEntry(final Iterable<String> entry) throws IOException {
        if (this.logger != null) {
            this.logger.logEntry(entry);
        }
    }
}
