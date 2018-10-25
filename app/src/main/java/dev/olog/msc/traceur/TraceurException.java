package dev.olog.msc.traceur;

import java.util.ArrayList;
import java.util.List;

public class TraceurException extends RuntimeException {

    private final boolean shouldFilterStackTraces;
    private final Traceur.LogLevel logLevel;

    /**
     * Creates an instance of a {@code TraceurException}, using the current {@link TraceurConfig}
     *
     * @return The exception created with the current config
     */
    public static TraceurException create() {
        final TraceurConfig config = Traceur.getConfig();
        return new TraceurException(config.shouldFilterStackTraces(), config.getLogLevel());
    }

    TraceurException(boolean shouldFilterStackTraces, Traceur.LogLevel logLevel) {
        super("Debug Exception generated at call site");
        this.shouldFilterStackTraces = shouldFilterStackTraces;
        this.logLevel = logLevel;
        this.setStackTrace(createStackTrace());
    }

    /**
     * Adds this as the root cause of the given exception.
     * If the current root cause is already a {@code TraceurException}, and the log level is set to
     * {@link Traceur.LogLevel#SHOW_ONLY_FIRST}, then this will not be appended to the exception
     *
     * @param throwable The exception to append to
     * @return The original exception (with this appended as the root cause)
     */
    public Throwable appendTo(Throwable throwable) {
        Throwable t = throwable;
        while (t.getCause() != null) {
            t = t.getCause();

            // Won't be able to init the cause of this with self
            if (t == this) {
                return throwable;
            }

            if (logLevel == Traceur.LogLevel.SHOW_ONLY_FIRST && t instanceof TraceurException) {
                return throwable;
            }
        }

        t.initCause(this);

        return throwable;
    }

    private StackTraceElement[] createStackTrace() {
        final StackTraceElement[] realStackTrace = Thread.currentThread().getStackTrace();
        if (!shouldFilterStackTraces) {
            return realStackTrace;
        }

        final List<StackTraceElement> filtered = new ArrayList<>(realStackTrace.length);

        for (StackTraceElement element : realStackTrace) {
            if (filterLine(element)) {
                filtered.add(element);
            }
        }

        return filtered.toArray(new StackTraceElement[filtered.size()]);
    }

    /**
     * @param element Stack trace line to potentially filter
     * @return true if the element should be shown in the stacktrace
     */
    private boolean filterLine(final StackTraceElement element) {
        final String className = element.getClassName();

        // Remove references to Traceur & RxJavaPlugins
        if (className.contains(".Traceur")
                || className.contains("OnAssembly")
                || className.endsWith(".RxJavaPlugins")) {
            return false;
        }

        return true;
    }
}
