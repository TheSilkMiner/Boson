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
