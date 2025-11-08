package com.morgan.alexander.socket;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Factory for producing a new {@link ServerSocket}.
 */
public interface ServerSocketFactory {

    ServerSocket create(int port) throws IOException;
}
