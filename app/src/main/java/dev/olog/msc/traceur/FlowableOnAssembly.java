/*
 * Copyright 2016-2017 David Karnok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.olog.msc.traceur;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import io.reactivex.Flowable;
import io.reactivex.internal.fuseable.ConditionalSubscriber;
import io.reactivex.internal.fuseable.QueueSubscription;
import io.reactivex.internal.subscribers.BasicFuseableConditionalSubscriber;
import io.reactivex.internal.subscribers.BasicFuseableSubscriber;

/**
 * Wraps a Publisher and inject the assembly info.
 *
 * @param <T> the value type
 */
final class FlowableOnAssembly<T> extends Flowable<T> {

    final Publisher<T> source;

    final TraceurException assembled;

    FlowableOnAssembly(Publisher<T> source) {
        this.source = source;
        this.assembled = TraceurException.create();
    }

    @Override
    protected void subscribeActual(Subscriber<? super T> s) {
        if (s instanceof ConditionalSubscriber) {
            source.subscribe(new OnAssemblyConditionalSubscriber<>((ConditionalSubscriber<? super T>) s, assembled));
        } else {
            source.subscribe(new OnAssemblySubscriber<>(s, assembled));
        }
    }

    static final class OnAssemblySubscriber<T> extends BasicFuseableSubscriber<T, T> {

        final TraceurException assembled;

        OnAssemblySubscriber(Subscriber<? super T> actual, TraceurException assembled) {
            super(actual);
            this.assembled = assembled;
        }

        @Override
        public void onNext(T t) {
            downstream.onNext(t);
        }

        @Override
        public void onError(Throwable t) {
            downstream.onError(assembled.appendTo(t));
        }

        @Override
        public int requestFusion(int mode) {
            QueueSubscription<T> qs = this.qs;
            if (qs != null) {
                int m = qs.requestFusion(mode);
                sourceMode = m;
                return m;
            }
            return NONE;
        }

        @Override
        public T poll() throws Exception {
            return qs.poll();
        }
    }


    public static final class OnAssemblyConditionalSubscriber<T> extends BasicFuseableConditionalSubscriber<T, T> {

        final TraceurException assembled;

        OnAssemblyConditionalSubscriber(ConditionalSubscriber<? super T> actual, TraceurException assembled) {
            super(actual);
            this.assembled = assembled;
        }

        @Override
        public void onNext(T t) {
            downstream.onNext(t);
        }

        @Override
        public boolean tryOnNext(T t) {
            return downstream.tryOnNext(t);
        }

        @Override
        public void onError(Throwable t) {
            downstream.onError(assembled.appendTo(t));
        }

        @Override
        public int requestFusion(int mode) {
            QueueSubscription<T> qs = this.qs;
            if (qs != null) {
                int m = qs.requestFusion(mode);
                sourceMode = m;
                return m;
            }
            return NONE;
        }

        @Override
        public T poll() throws Exception {
            return qs.poll();
        }
    }
}
