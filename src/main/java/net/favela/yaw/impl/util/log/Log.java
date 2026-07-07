package net.favela.yaw.impl.util.log;

import net.favela.yaw.EntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Log {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntryPoint.name());

    private Log() {}

    public static void trace(String msg, Object... args) { LOGGER.trace(msg, args); }
    public static void debug(String msg, Object... args) { LOGGER.debug(msg, args); }
    public static void info(String msg, Object... args) { LOGGER.info(msg, args); }
    public static void warn(String msg, Object... args) { LOGGER.warn(msg, args); }
    public static void error(String msg, Object... args) { LOGGER.error(msg, args); }
    public static void error(String msg, Throwable t) { LOGGER.error(msg, t); }
}