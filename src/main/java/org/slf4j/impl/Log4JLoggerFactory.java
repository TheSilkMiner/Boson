package org.slf4j.impl;

import org.apache.logging.log4j.LogManager;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Log4JLoggerFactory implements ILoggerFactory {
    private final Map<String, Logger> loggerMap;

    Log4JLoggerFactory() {
        this.loggerMap = new ConcurrentHashMap<>();
        LogManager.getRootLogger();
    }

    @Nonnull
    @Override
    public Logger getLogger(@Nonnull final String name) {
        return this.loggerMap.computeIfAbsent(name, this::createNewLogger);
    }

    @Nonnull
    private Logger createNewLogger(@Nonnull final String name) {
        return new Log4JLoggerAdapter(this.createByName(name));
    }

    @Nonnull
    private org.apache.logging.log4j.Logger createByName(@Nonnull final String name) {
        if (Logger.ROOT_LOGGER_NAME.equals(name)) return LogManager.getRootLogger();
        return LogManager.getLogger(name);
    }
}
