package com.zxl.haze.core.listener;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;


/**
 * LogContextListener
 */

public class CustomLogContextListener extends ContextAwareBase implements LoggerContextListener, LifeCycle {

    /**
     * 存储日志路径标识
     */
    public static final String LOG_PATH_KEY = "LOG_PATH";

    @Override
    public boolean isResetResistant() {
        return false;
    }

    @Override
    public void onStart(LoggerContext loggerContext) {

    }

    @Override
    public void onReset(LoggerContext loggerContext) {
    }

    @Override
    public void onStop(LoggerContext loggerContext) {
    }

    @Override
    public void onLevelChange(Logger logger, Level level) {
    }

    @Override
    public void start() {
        String logPath = System.getProperty("user.dir") + "/logs";
        System.setProperty(LOG_PATH_KEY, logPath);
        Context context = getContext();
        context.putProperty(LOG_PATH_KEY, logPath);
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isStarted() {
        return false;
    }
}