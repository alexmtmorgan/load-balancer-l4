package com.morgan.alexander.socket;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Factory for producing new {@link ServerSocket}(s).
 */
@FunctionalInterface
public interface ServerSocketFactory {

    /**
     * Creates a {@link ServerSocket} bound to the provided port.
     *
     * @param port the port to use
     * @return the {@link  ServerSocket}
     * @throws IOException in case of failure to create the {@link ServerSocket}
     */
    ServerSocket create(int port) throws IOException;
}
