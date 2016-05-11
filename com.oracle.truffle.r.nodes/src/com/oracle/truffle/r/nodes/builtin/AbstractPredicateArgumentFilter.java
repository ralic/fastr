/*
 * Copyright (c) 2013, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.truffle.r.nodes.builtin;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.oracle.truffle.r.nodes.builtin.ArgumentFilter.NarrowingArgumentFilter;
import com.oracle.truffle.r.nodes.unary.CastNode.Samples;
import com.oracle.truffle.r.nodes.unary.CastNode.TypeExpr;
import com.oracle.truffle.r.runtime.data.RNull;

public abstract class AbstractPredicateArgumentFilter<T, R extends T> implements NarrowingArgumentFilter<T, R> {

    private final Predicate<? super T> valuePredicate;
    private final Set<? extends R> positiveSamples;
    private final Set<?> negativeSamples;
    private final TypeExpr allowedTypes;
    private final boolean isNullable;

    public AbstractPredicateArgumentFilter(Predicate<? super T> valuePredicate, Set<? extends R> positiveSamples, Set<?> negativeSamples, Set<Class<?>> allowedTypeSet, boolean isNullable) {
        this.valuePredicate = valuePredicate;
        this.allowedTypes = allowedTypeSet.isEmpty() ? TypeExpr.ANYTHING : TypeExpr.union(allowedTypeSet);
        this.positiveSamples = positiveSamples;
        this.negativeSamples = negativeSamples;
        this.isNullable = isNullable;

        assert positiveSamples.stream().allMatch(x -> valuePredicate.test(x));
    }

    @Override
    public boolean test(T arg) {
        if (!isNullable && (arg == RNull.instance || arg == null)) {
            return false;
        } else {
            return valuePredicate.test(arg);
        }
    }

    @Override
    public TypeExpr allowedTypes() {
        return allowedTypes;
    }

    public Samples<R> collectSamples(Samples<? extends R> downStreamSamples) {
        Set<R> allowedPositiveValues = downStreamSamples.positiveSamples().stream().filter(x -> test(x)).collect(Collectors.toSet());
        Set<R> forbiddenPositiveValues = downStreamSamples.positiveSamples().stream().filter(x -> !test(x)).collect(Collectors.toSet());

        Set<Object> negativeAndForbiddenSamples = new HashSet<>(forbiddenPositiveValues);
        negativeAndForbiddenSamples.addAll(downStreamSamples.negativeSamples());

        return new Samples<>(allowedPositiveValues, negativeAndForbiddenSamples).and(new Samples<>(positiveSamples, negativeSamples));
    }
}
