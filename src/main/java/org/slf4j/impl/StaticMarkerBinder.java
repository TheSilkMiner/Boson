/*
 * Copyright (C) 2021  TheSilkMiner
 *
 * This file is part of Boson.
 *
 * Boson is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Boson is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Boson.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact information:
 * E-mail: thesilkminer <at> outlook <dot> com
 */

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
