package dev.olog.msc.traceur;

public class TraceurConfig {

    private final boolean shouldFilterStackTraces;
    private final Traceur.LogLevel logLevel;

    public TraceurConfig(boolean shouldFilterStackTraces) {
        this.shouldFilterStackTraces = shouldFilterStackTraces;
        this.logLevel = Traceur.LogLevel.SHOW_ALL;
    }

    public boolean shouldFilterStackTraces() {
        return shouldFilterStackTraces;
    }

    public Traceur.LogLevel getLogLevel() {
        return logLevel;
    }
}
