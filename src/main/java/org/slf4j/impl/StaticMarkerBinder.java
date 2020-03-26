package org.slf4j.impl;

import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MarkerFactoryBinder;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public final class StaticMarkerBinder implements MarkerFactoryBinder {
    private static final class Lazy {
        private static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();
    }

    private final IMarkerFactory factory;

    private StaticMarkerBinder() {
        this.factory = new BasicMarkerFactory();
    }

    @Nonnull
    public static StaticMarkerBinder getSingleton() {
        return Lazy.SINGLETON;
    }

    @Nonnull
    @Override
    public IMarkerFactory getMarkerFactory() {
        return this.factory;
    }

    @Nonnull
    @Override
    public String getMarkerFactoryClassStr() {
        return BasicMarkerFactory.class.getName();
    }
}
