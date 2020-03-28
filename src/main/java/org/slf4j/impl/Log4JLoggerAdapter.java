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

import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.message.FormattedMessage;
import org.apache.logging.log4j.util.MessageSupplier;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

public final class Log4JLoggerAdapter implements Logger, LocationAwareLogger, Serializable {
    private static final long serialVersionUID = 6182834493563598289L;

    private final transient org.apache.logging.log4j.Logger apacheLogger;
    private final String name;

    Log4JLoggerAdapter(@Nonnull final org.apache.logging.log4j.Logger apacheLogger) {
        this.apacheLogger = apacheLogger;
        this.name = apacheLogger.getName();
    }

    @Override
    public void log(@Nullable final Marker marker, @Nonnull final String fqcn, final int level, @Nonnull final String message, @Nullable final Object[] argArray, @Nullable final Throwable t) {
        final MessageSupplier apacheMessage = this.toApacheMessage(fqcn + ": " + message, argArray, t);
        final org.apache.logging.log4j.Marker apacheMarker = this.toApacheMarker(marker);
        switch (level) {
            case LocationAwareLogger.DEBUG_INT:
                if (apacheMarker == null) this.apacheLogger.debug(apacheMessage); else this.apacheLogger.debug(apacheMarker, apacheMessage);
                break;
            case LocationAwareLogger.ERROR_INT:
                if (apacheMarker == null) this.apacheLogger.error(apacheMessage); else this.apacheLogger.error(apacheMarker, apacheMessage);
                break;
            case LocationAwareLogger.INFO_INT:
                if (apacheMarker == null) this.apacheLogger.info(apacheMessage); else this.apacheLogger.info(apacheMarker, apacheMessage);
                break;
            case LocationAwareLogger.TRACE_INT:
                if (apacheMarker == null) this.apacheLogger.trace(apacheMessage); else this.apacheLogger.trace(apacheMarker, apacheMessage);
                break;
            case LocationAwareLogger.WARN_INT:
                if (apacheMarker == null) this.apacheLogger.warn(apacheMessage); else this.apacheLogger.warn(apacheMarker, apacheMessage);
                break;
            default:
                throw new IllegalStateException(level + " unrecognized");
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isTraceEnabled() {
        return this.apacheLogger.isTraceEnabled();
    }

    @Override
    public void trace(@Nonnull final String msg) {
        this.apacheLogger.trace(msg);
    }

    @Override
    public void trace(@Nonnull final String format, @Nullable final Object arg) {
        this.apacheLogger.trace(format, arg);
    }

    @Override
    public void trace(@Nonnull final String format, @Nullable final Object arg1, @Nullable final Object arg2) {
        this.apacheLogger.trace(format, arg1, arg2);
    }

    @Override
    public void trace(@Nonnull final String format, @Nonnull final Object... arguments) {
        if (this.isTraceEnabled()) this.apacheLogger.trace(format, arguments);
    }

    @Override
    public void trace(@Nonnull final String msg, @Nullable final Throwable t) {
        this.apacheLogger.trace(msg, t);
    }

    @Override
    public boolean isTraceEnabled(@Nonnull final Marker marker) {
        return this.apacheLogger.isTraceEnabled(this.toApacheMarker(marker));
    }

    @Override
    public void trace(@Nonnull final Marker marker, @Nonnull final String msg) {
        this.apacheLogger.trace(this.toApacheMarker(marker), msg);
    }

    @Override
    public void trace(@Nonnull final Marker marker, @Nonnull final String format, @Nullable final Object arg) {
        this.apacheLogger.trace(this.toApacheMarker(marker), format, arg);
    }

    @Override
    public void trace(@Nonnull final Marker marker, @Nonnull final String format, @Nullable final Object arg1, @Nullable final Object arg2) {
        this.apacheLogger.trace(this.toApacheMarker(marker), format, arg1, arg2);
    }

    @Override
    public void trace(@Nonnull final Marker marker, @Nonnull final String format, @Nonnull final Object... argArray) {
        if (this.isTraceEnabled(marker)) this.apacheLogger.trace(this.toApacheMarker(marker), format, argArray);
    }

    @Override
    public void trace(@Nonnull final Marker marker, @Nonnull final String msg, @Nullable final Throwable t) {
        this.apacheLogger.trace(this.toApacheMarker(marker), msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.apacheLogger.isDebugEnabled();
    }

    @Override
    public void debug(@Nonnull final String msg) {
        this.apacheLogger.debug(msg);
    }

    @Override
    public void debug(@Nonnull final String format, @Nullable final Object arg) {
        this.apacheLogger.debug(format, arg);
    }

    @Override
    public void debug(@Nonnull final String format, @Nullable final Object arg1, @Nullable final Object arg2) {
        this.apacheLogger.debug(format, arg1, arg2);
    }

    @Override
    public void debug(@Nonnull final String format, @Nonnull final Object... arguments) {
        if (this.isDebugEnabled()) this.apacheLogger.debug(format, arguments);
    }

    @Override
    public void debug(@Nonnull final String msg, @Nullable final Throwable t) {
        this.apacheLogger.debug(msg, t);
    }

    @Override
    public boolean isDebugEnabled(@Nonnull final Marker marker) {
        return this.apacheLogger.isDebugEnabled(this.toApacheMarker(marker));
    }

    @Override
    public void debug(@Nonnull final Marker marker, @Nonnull final String msg) {
        this.apacheLogger.debug(this.toApacheMarker(marker), msg);
    }

    @Override
    public void debug(@Nonnull final Marker marker, @Nonnull final String format, @Nullable final Object arg) {
        this.apacheLogger.debug(this.toApacheMarker(marker), format, arg);
    }

    @Override
    public void debug(@Nonnull final Marker marker, @Nonnull final String format, @Nullable final Object arg1, @Nullable final Object arg2) {
        this.apacheLogger.debug(this.toApacheMarker(marker), format, arg1, arg2);
    }

    @Override
    public void debug(@Nonnull final Marker marker, @Nonnull final String format, @Nonnull final Object... argArray) {
        if (this.isDebugEnabled(marker)) this.apacheLogger.debug(this.toApacheMarker(marker), format, argArray);
    }

    @Override
    public void debug(@Nonnull final Marker marker, @Nonnull final String msg, @Nullable final Throwable t) {
        this.apacheLogger.debug(this.toApacheMarker(marker), msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.apacheLogger.isInfoEnabled();
    }

    @Override
    public void info(@Nonnull final String msg) {
        this.apacheLogger.info(msg);
    }

    @Override
    public void info(@Nonnull final String format, @Nullable final Object arg) {
        this.apacheLogger.info(format, arg);
    }

    @Override
    public void info(@Nonnull final String format, @Nullable final Object arg1, @Nullable final Object arg2) {
        this.apacheLogger.info(format, arg1, arg2);
    }

    @Override
    public void info(@Nonnull final String format, @Nonnull final Object... arguments) {
        if (this.isInfoEnabled()) this.apacheLogger.info(format, arguments);
    }

    @Override
    public void info(@Nonnull final String msg, @Nullable final Throwable t) {
        this.apacheLogger.info(msg, t);
    }

    @Override
    public boolean isInfoEnabled(@Nonnull final Marker marker) {
        return this.apacheLogger.isInfoEnabled(this.toApacheMarker(marker));
    }

    @Override
    public void info(@Nonnull final Marker marker, @Nonnull final String msg) {
        this.apacheLogger.info(this.toApacheMarker(marker), msg);
    }

    @Override
    public void info(@Nonnull final Marker marker, @Nonnull final String format, @Nullable final Object arg) {
        this.apacheLogger.info(this.toApacheMarker(marker), format, arg);
    }

    @Override
    public void info(@Nonnull final Marker marker, @Nonnull final String format, @Nullable final Object arg1, @Nullable final Object arg2) {
        this.apacheLogger.info(this.toApacheMarker(marker), format, arg1, arg2);
    }

    @Override
    public void info(@Nonnull final Marker marker, @Nonnull final String format, @Nonnull final Object... argArray) {
        if (this.isInfoEnabled(marker)) this.apacheLogger.info(this.toApacheMarker(marker), format, argArray);
    }

    @Override
    public void info(@Nonnull final Marker marker, @Nonnull final String msg, @Nullable final Throwable t) {
        this.apacheLogger.info(this.toApacheMarker(marker), msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.apacheLogger.isWarnEnabled();
    }

    @Override
    public void warn(@Nonnull final String msg) {
        this.apacheLogger.warn(msg);
    }

    @Override
    public void warn(@Nonnull final String format, @Nullable final Object arg) {
        this.apacheLogger.warn(format, arg);
    }

    @Override
    public void warn(@Nonnull final String format, @Nullable final Object arg1, @Nullable final Object arg2) {
        this.apacheLogger.warn(format, arg1, arg2);
    }

    @Override
    public void warn(@Nonnull final String format, @Nonnull final Object... arguments) {
        if (this.isWarnEnabled()) this.apacheLogger.warn(format, arguments);
    }

    @Override
    public void warn(@Nonnull final String msg, @Nullable final Throwable t) {
        this.apacheLogger.warn(msg, t);
    }

    @Override
    public boolean isWarnEnabled(@Nonnull final Marker marker) {
        return this.apacheLogger.isWarnEnabled(this.toApacheMarker(marker));
    }

    @Override
    public void warn(@Nonnull final Marker marker, @Nonnull final String msg) {
        this.apacheLogger.warn(this.toApacheMarker(marker), msg);
    }

    @Override
    public void warn(@Nonnull final Marker marker, @Nonnull final String format, @Nullable final Object arg) {
        this.apacheLogger.warn(this.toApacheMarker(marker), format, arg);
    }

    @Override
    public void warn(@Nonnull final Marker marker, @Nonnull final String format, @Nullable final Object arg1, @Nullable final Object arg2) {
        this.apacheLogger.warn(this.toApacheMarker(marker), format, arg1, arg2);
    }

    @Override
    public void warn(@Nonnull final Marker marker, @Nonnull final String format, @Nonnull final Object... argArray) {
        if (this.isWarnEnabled(marker)) this.apacheLogger.warn(this.toApacheMarker(marker), format, argArray);
    }

    @Override
    public void warn(@Nonnull final Marker marker, @Nonnull final String msg, @Nullable final Throwable t) {
        this.apacheLogger.warn(this.toApacheMarker(marker), msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.apacheLogger.isErrorEnabled();
    }

    @Override
    public void error(@Nonnull final String msg) {
        this.apacheLogger.error(msg);
    }

    @Override
    public void error(@Nonnull final String format, @Nullable final Object arg) {
        this.apacheLogger.error(format, arg);
    }

    @Override
    public void error(@Nonnull final String format, @Nullable final Object arg1, @Nullable final Object arg2) {
        this.apacheLogger.error(format, arg1, arg2);
    }

    @Override
    public void error(@Nonnull final String format, @Nonnull final Object... arguments) {
        if (this.isErrorEnabled()) this.apacheLogger.error(format, arguments);
    }

    @Override
    public void error(@Nonnull final String msg, @Nullable final Throwable t) {
        this.apacheLogger.error(msg, t);
    }

    @Override
    public boolean isErrorEnabled(@Nonnull final Marker marker) {
        return this.apacheLogger.isErrorEnabled(this.toApacheMarker(marker));
    }

    @Override
    public void error(@Nonnull final Marker marker, @Nonnull final String msg) {
        this.apacheLogger.error(this.toApacheMarker(marker), msg);
    }

    @Override
    public void error(@Nonnull final Marker marker, @Nonnull final String format, @Nullable final Object arg) {
        this.apacheLogger.error(this.toApacheMarker(marker), format, arg);
    }

    @Override
    public void error(@Nonnull final Marker marker, @Nonnull final String format, @Nullable final Object arg1, @Nullable final Object arg2) {
        this.apacheLogger.error(this.toApacheMarker(marker), format, arg1, arg2);
    }

    @Override
    public void error(@Nonnull final Marker marker, @Nonnull final String format, @Nonnull final Object... argArray) {
        if (this.isErrorEnabled(marker)) this.apacheLogger.error(this.toApacheMarker(marker), format, argArray);
    }

    @Override
    public void error(@Nonnull final Marker marker, @Nonnull final String msg, @Nullable final Throwable t) {
        this.apacheLogger.error(this.toApacheMarker(marker), msg, t);
    }

    @Nullable
    private org.apache.logging.log4j.Marker toApacheMarker(@Nullable final Marker marker) {
        if (marker == null) return null;
        return MarkerManager.getMarker(marker.getName());
    }

    @Nonnull
    private MessageSupplier toApacheMessage(@Nonnull final String message, @Nullable final Object[] arguments, @Nullable final Throwable t) {
        return () -> new FormattedMessage(message, arguments, t);
    }
}
