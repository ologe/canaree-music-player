package dev.olog.shared;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.FlowableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.annotations.BackpressureKind;
import io.reactivex.annotations.BackpressureSupport;
import io.reactivex.annotations.SchedulerSupport;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.SerializedSubscriber;

public final class FlowableFirstThenDebounce<T> extends Flowable<T>
        implements FlowableTransformer<T, T> {

    @BackpressureSupport(BackpressureKind.PASS_THROUGH)
    @SchedulerSupport(SchedulerSupport.CUSTOM)
    public static <T> FlowableTransformer<T, T> get(long timeout, TimeUnit unit) {
        ObjectHelper.requireNonNull(unit, "unit is null");
        return new FlowableFirstThenDebounce<>(null, timeout, unit, Schedulers.computation());
    }

    final Publisher<T> source;

    final long timeout;

    final TimeUnit unit;

    final Scheduler scheduler;

    FlowableFirstThenDebounce(Publisher<T> source, long timeout, TimeUnit unit, Scheduler scheduler) {
        this.source = source;
        this.timeout = timeout;
        this.unit = unit;
        this.scheduler = scheduler;
    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return new FlowableFirstThenDebounce<>(upstream, timeout, unit, scheduler);
    }

    @Override
    protected void subscribeActual(Subscriber<? super T> s) {
        source.subscribe(new DebounceFirstSubscriber<T>(
                new SerializedSubscriber<>(s), timeout, unit, scheduler.createWorker())
        );
    }

    static final class DebounceFirstSubscriber<T> extends AtomicLong
            implements FlowableSubscriber<T>, Subscription {

        private static final long serialVersionUID = -9102637559663639004L;
        final Subscriber<? super T> downstream;
        final long timeout;
        final TimeUnit unit;
        final Scheduler.Worker worker;

        Subscription upstream;

        Disposable timer;

        volatile long index;

        boolean done;

        AtomicBoolean emittedOnce = new AtomicBoolean(false);

        DebounceFirstSubscriber(Subscriber<? super T> actual, long timeout, TimeUnit unit, Scheduler.Worker worker) {
            this.downstream = actual;
            this.timeout = timeout;
            this.unit = unit;
            this.worker = worker;
        }

        @Override
        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                downstream.onSubscribe(this);
                s.request(Long.MAX_VALUE);
            }
        }

        @Override
        public void onNext(T t) {
            if (done) {
                return;
            }

            long idx = index + 1;
            index = idx;

            if (emittedOnce.compareAndSet(false, true)){
                downstream.onNext(t);
                return;
            }

            Disposable d = timer;
            if (d != null) {
                d.dispose();
            }

            DebounceEmitter<T> de = new DebounceEmitter<T>(t, idx, this);
            timer = de;
            d = worker.schedule(de, timeout, unit);
            de.setResource(d);
        }

        @Override
        public void onError(Throwable t) {
            if (done) {
                RxJavaPlugins.onError(t);
                return;
            }
            done = true;
            Disposable d = timer;
            if (d != null) {
                d.dispose();
            }
            downstream.onError(t);
            worker.dispose();
        }

        @Override
        public void onComplete() {
            if (done) {
                return;
            }
            done = true;

            Disposable d = timer;
            if (d != null) {
                d.dispose();
            }

            @SuppressWarnings("unchecked")
            DebounceEmitter<T> de = (DebounceEmitter<T>)d;
            if (de != null) {
                de.emit();
            }

            downstream.onComplete();
            worker.dispose();
        }

        @Override
        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.add(this, n);
            }
        }

        @Override
        public void cancel() {
            upstream.cancel();
            worker.dispose();
        }

        void emit(long idx, T t, DebounceEmitter<T> emitter) {
            if (idx == index) {
                long r = get();
                if (r != 0L) {
                    downstream.onNext(t);
                    BackpressureHelper.produced(this, 1);

                    emitter.dispose();
                } else {
                    cancel();
                    downstream.onError(new MissingBackpressureException("Could not deliver value due to lack of requests"));
                }
            }
        }
    }

    static final class DebounceEmitter<T> extends AtomicReference<Disposable> implements Runnable, Disposable {

        private static final long serialVersionUID = 6812032969491025141L;

        final T value;
        final long idx;
        final DebounceFirstSubscriber<T> parent;

        final AtomicBoolean once = new AtomicBoolean();

        DebounceEmitter(T value, long idx, DebounceFirstSubscriber<T> parent) {
            this.value = value;
            this.idx = idx;
            this.parent = parent;
        }

        @Override
        public void run() {
            emit();
        }

        void emit() {
            if (once.compareAndSet(false, true)) {
                parent.emit(idx, value, this);
            }
        }

        @Override
        public void dispose() {
            DisposableHelper.dispose(this);
        }

        @Override
        public boolean isDisposed() {
            return get() == DisposableHelper.DISPOSED;
        }

        public void setResource(Disposable d) {
            DisposableHelper.replace(this, d);
        }
    }

}