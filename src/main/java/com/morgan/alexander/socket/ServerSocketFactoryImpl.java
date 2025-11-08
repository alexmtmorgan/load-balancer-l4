package com.morgan.alexander.socket;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Simple implementation of {@link ServerSocketFactory}.
 */
public class ServerSocketFactoryImpl implements ServerSocketFactory {
    @Override
    public ServerSocket create(int port) throws IOException {
        return new ServerSocket(port);
    }
}
