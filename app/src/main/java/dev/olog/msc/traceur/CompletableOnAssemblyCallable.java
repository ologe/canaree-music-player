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

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.exceptions.Exceptions;

/**
 * Wraps a CompletableSource and inject the assembly info.
 */
final class CompletableOnAssemblyCallable extends Completable implements Callable<Object> {

    final CompletableSource source;

    final TraceurException assembled;

    CompletableOnAssemblyCallable(CompletableSource source) {
        this.source = source;
        this.assembled = TraceurException.create();
    }

    @Override
    protected void subscribeActual(CompletableObserver s) {
        source.subscribe(new CompletableOnAssembly.OnAssemblyCompletableObserver(s, assembled));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object call() throws Exception {
        try {
            return ((Callable<Object>)source).call();
        } catch (Exception ex) {
            Exceptions.throwIfFatal(ex);
            throw (Exception)assembled.appendTo(ex);
        }
    }
}
