package org.slf4j.impl;

import org.apache.logging.log4j.LogManager;
import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public final class StaticLoggerBinder implements LoggerFactoryBinder {
    private static final class Lazy {
        private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

        static {
            LogManager.getLogger("Boson/SLF4J Static Logger Binder").info("Successfully initialized SLF4J Logger binder: Exposed should be ready!");
        }
    }

    public static String REQUESTED_API_VERSION = "1.6.99";
    private final ILoggerFactory factory;

    private StaticLoggerBinder() {
        this.factory = new Log4JLoggerFactory();
    }

    @Nonnull
    public static StaticLoggerBinder getSingleton() {
        return Lazy.SINGLETON;
    }

    @Nonnull
    @Override
    public ILoggerFactory getLoggerFactory() {
        return this.factory;
    }

    @Nonnull
    @Override
    public String getLoggerFactoryClassStr() {
        return Log4JLoggerFactory.class.getName();
    }
}
