/*
 * Copyright 2025 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel.uring;

import io.netty.channel.IoExecutionContext;
import io.netty.channel.IoEventLoop;
import io.netty.channel.IoHandle;
import io.netty.channel.IoHandler;
import io.netty.channel.IoHandlerFactory;
import io.netty.channel.IoRegistration;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.util.concurrent.Future;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class IoUringIoHandlerTest {

    @BeforeAll
    public static void loadJNI() {
        assumeTrue(IoUring.isAvailable());
    }

    @Test
    public void testOptions() {
        TestIoEventLoop eventLoop = new TestIoEventLoop();
        IoUringIoHandlerConfiguration config = new IoUringIoHandlerConfiguration();
        config.setMaxBoundedWorker(2)
                .setMaxUnboundedWorker(2);
        IoHandlerFactory ioHandlerFactory = IoUringIoHandler.newFactory(config);
        IoHandler handler = ioHandlerFactory.newHandler(eventLoop);
        handler.initialize();
        handler.destroy();
        eventLoop.shutdownGracefully();
    }

    private static final class TestIoEventLoop extends SingleThreadEventLoop implements IoEventLoop {
        TestIoEventLoop() {
            super(null, Executors.defaultThreadFactory(), true);
        }

        @Override
        public IoEventLoop next() {
            return this;
        }

        @Override
        public Future<IoRegistration> register(IoHandle handle) {
            return newFailedFuture(new UnsupportedOperationException());
        }

        @Override
        public boolean isCompatible(Class<? extends IoHandle> handleType) {
            return false;
        }

        @Override
        public boolean isIoType(Class<? extends IoHandler> handlerType) {
            return false;
        }

        @Override
        protected void run() {
            // Do nothing.
        }
    }
}
