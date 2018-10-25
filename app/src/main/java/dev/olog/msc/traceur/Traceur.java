package dev.olog.msc.traceur;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.functions.Function;
import io.reactivex.internal.fuseable.ScalarCallable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.parallel.ParallelFlowable;
import io.reactivex.plugins.RxJavaPlugins;

public class Traceur {

    private static TraceurConfig config;

    private Traceur() {
        throw new IllegalStateException("No instances!");
    }

    /**
     * Enable RxJava2 call-site logging with the default config.
     *
     * @see #enableLogging(TraceurConfig)
     */
    public static void enableLogging() {
        enableLogging(new TraceurConfig(true));
    }

    /**
     * Enable RxJava2 call-site logging with the given config.
     * Note: This will overwrite all {@code RxJavaPlugins.setXXXAssembly} plugins.
     *
     * @param config The configuration to use
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public synchronized static void enableLogging(TraceurConfig config) {
        Traceur.config = config;

        RxJavaPlugins.setOnFlowableAssembly(new Function<Flowable, Flowable>() {
            @Override
            public Flowable apply(Flowable f) throws Exception {
                if (f instanceof Callable) {
                    if (f instanceof ScalarCallable) {
                        return new FlowableOnAssemblyScalarCallable(f);
                    }
                    return new FlowableOnAssemblyCallable(f);
                }
                return new FlowableOnAssembly(f);
            }
        });

        RxJavaPlugins.setOnConnectableFlowableAssembly(new Function<ConnectableFlowable, ConnectableFlowable>() {
            @Override
            public ConnectableFlowable apply(ConnectableFlowable f) throws Exception {
                return new FlowableOnAssemblyConnectable(f);
            }
        });

        RxJavaPlugins.setOnObservableAssembly(new Function<Observable, Observable>() {
            @Override
            public Observable apply(Observable f) throws Exception {
                if (f instanceof Callable) {
                    if (f instanceof ScalarCallable) {
                        return new ObservableOnAssemblyScalarCallable(f);
                    }
                    return new ObservableOnAssemblyCallable(f);
                }
                return new ObservableOnAssembly(f);
            }
        });

        RxJavaPlugins.setOnConnectableObservableAssembly(new Function<ConnectableObservable, ConnectableObservable>() {
            @Override
            public ConnectableObservable apply(ConnectableObservable f) throws Exception {
                return new ObservableOnAssemblyConnectable(f);
            }
        });

        RxJavaPlugins.setOnSingleAssembly(new Function<Single, Single>() {
            @Override
            public Single apply(Single f) throws Exception {
                if (f instanceof Callable) {
                    if (f instanceof ScalarCallable) {
                        return new SingleOnAssemblyScalarCallable(f);
                    }
                    return new SingleOnAssemblyCallable(f);
                }
                return new SingleOnAssembly(f);
            }
        });

        RxJavaPlugins.setOnCompletableAssembly(new Function<Completable, Completable>() {
            @Override
            public Completable apply(Completable f) throws Exception {
                if (f instanceof Callable) {
                    if (f instanceof ScalarCallable) {
                        return new CompletableOnAssemblyScalarCallable(f);
                    }
                    return new CompletableOnAssemblyCallable(f);
                }
                return new CompletableOnAssembly(f);
            }
        });

        RxJavaPlugins.setOnMaybeAssembly(new Function<Maybe, Maybe>() {
            @Override
            public Maybe apply(Maybe f) throws Exception {
                if (f instanceof Callable) {
                    if (f instanceof ScalarCallable) {
                        return new MaybeOnAssemblyScalarCallable(f);
                    }
                    return new MaybeOnAssemblyCallable(f);
                }
                return new MaybeOnAssembly(f);
            }
        });

        RxJavaPlugins.setOnParallelAssembly(new Function<ParallelFlowable, ParallelFlowable>() {
            @Override
            public ParallelFlowable apply(ParallelFlowable t) throws Exception {
                return new ParallelFlowableOnAssembly(t);
            }
        });
    }

    /**
     * Disables call-site tracking.  Note that observables assembled before this is called will still have the debug
     * info attached.
     */
    public static synchronized void disableLogging() {
        RxJavaPlugins.setOnCompletableAssembly(null);
        RxJavaPlugins.setOnSingleAssembly(null);
        RxJavaPlugins.setOnMaybeAssembly(null);

        RxJavaPlugins.setOnObservableAssembly(null);
        RxJavaPlugins.setOnFlowableAssembly(null);
        RxJavaPlugins.setOnConnectableObservableAssembly(null);
        RxJavaPlugins.setOnConnectableFlowableAssembly(null);

        RxJavaPlugins.setOnParallelAssembly(null);
        Traceur.config = null;
    }

    public static TraceurConfig getConfig() {
        return config;
    }

    public enum LogLevel {
        SHOW_ALL,
        SHOW_ONLY_FIRST,
    }
}
