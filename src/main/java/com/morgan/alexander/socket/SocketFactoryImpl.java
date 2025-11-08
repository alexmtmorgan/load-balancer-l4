package com.morgan.alexander.socket;

import java.io.IOException;
import java.net.Socket;

/**
 * Simple implementation of {@link SocketFactory}.
 */
public class SocketFactoryImpl implements SocketFactory {

    @Override
    public Socket create(final String host,
                         final int port) throws IOException {
        return new Socket(host, port);
    }
}
