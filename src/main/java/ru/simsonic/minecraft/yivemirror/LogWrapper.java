package ru.simsonic.minecraft.yivemirror;

import org.apache.maven.plugin.logging.Log;

public class LogWrapper {

    private final Log log;

    public LogWrapper(Log log) {
        this.log = log;
    }

    public void debug(String message) {
        log.debug(String.format("%s", message));
    }

    public void info(String message) {
        log.info(String.format("%s", message));
    }

    public void warn(String message) {
        log.warn(String.format("%s", message));
    }

    public void error(String message) {
        log.error(String.format("%s", message));
    }

    public void debug(String format, Object... args) {
        log.debug(String.format(format, args));
    }

    public void info(String format, Object... args) {
        log.info(String.format(format, args));
    }

    public void warn(String format, Object... args) {
        log.warn(String.format(format, args));
    }

    public void error(String format, Object... args) {
        log.error(String.format(format, args));
    }
}
