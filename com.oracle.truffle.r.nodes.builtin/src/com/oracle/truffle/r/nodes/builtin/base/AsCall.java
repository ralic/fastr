/*
 * Copyright (c) 2014, 2014, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.truffle.r.nodes.builtin.base;

import static com.oracle.truffle.r.runtime.RBuiltinKind.*;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.r.nodes.access.*;
import com.oracle.truffle.r.nodes.builtin.*;
import com.oracle.truffle.r.nodes.function.*;
import com.oracle.truffle.r.runtime.*;
import com.oracle.truffle.r.runtime.data.*;

@RBuiltin(name = "as.call", kind = PRIMITIVE, parameterNames = {"x"})
public abstract class AsCall extends RBuiltinNode {

    @Specialization
    protected RLanguage asCallFunction(RList x) {
        return Call.makeCall((RFunction) x.getDataAt(0), makeNamesAndValues(x));
    }

    @Specialization
    protected RLanguage asCallFunction(RExpression x) {
        String f;
        if (x.getDataAt(0) instanceof RSymbol) {
            f = ((RSymbol) x.getDataAt(0)).getName();
        } else {
            RLanguage l = (RLanguage) x.getDataAt(0);
            f = ((ReadVariableNode) ((WrapArgumentNode) l.getRep()).getOperand()).getSymbol().getName();
        }
        return Call.makeCall(f, makeNamesAndValues(x.getList()));
    }

    private static RArgsValuesAndNames makeNamesAndValues(RList x) {
        int length = x.getLength() - 1;
        Object[] values = new Object[length];
        String[] names = new String[length];
        System.arraycopy(x.getDataWithoutCopying(), 1, values, 0, length);
        if (x.getNames() != RNull.instance) {
            RStringVector ns = (RStringVector) x.getNames();
            System.arraycopy(ns.getDataWithoutCopying(), 1, names, 0, length);
        }
        return new RArgsValuesAndNames(values, names);
    }

}