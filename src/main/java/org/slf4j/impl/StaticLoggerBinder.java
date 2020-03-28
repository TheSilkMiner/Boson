/*
 * Copyright (C) 2020  TheSilkMiner
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
