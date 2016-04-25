/*
 * Copyright (c) 2015, 2016, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.truffle.r.engine.interop;

import java.util.List;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.r.engine.TruffleRLanguage;
import com.oracle.truffle.r.nodes.function.CallMatcherNode;
import com.oracle.truffle.r.runtime.ArgumentsSignature;
import com.oracle.truffle.r.runtime.RArguments;
import com.oracle.truffle.r.runtime.data.RFunction;

class RInteropExecuteNode extends RootNode {

    private static final FrameDescriptor emptyFrameDescriptor = new FrameDescriptor();

    @Child private CallMatcherNode callMatcher = CallMatcherNode.create(false, true);

    private final ArgumentsSignature suppliedSignature;

    RInteropExecuteNode(int argumentsLength) {
        super(TruffleRLanguage.class, null, null);
        suppliedSignature = ArgumentsSignature.empty(argumentsLength);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        RFunction function = (RFunction) ForeignAccess.getReceiver(frame);
        List<Object> arguments = ForeignAccess.getArguments(frame);

        Object[] dummyFrameArgs = RArguments.createUnitialized();
        VirtualFrame dummyFrame = Truffle.getRuntime().createVirtualFrame(dummyFrameArgs, emptyFrameDescriptor);

        return callMatcher.execute(dummyFrame, suppliedSignature, arguments.toArray(), function, null, null);
    }
}
