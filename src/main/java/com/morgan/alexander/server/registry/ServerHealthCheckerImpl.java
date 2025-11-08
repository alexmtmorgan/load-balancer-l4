package com.morgan.alexander.server.registry;

import com.morgan.alexander.socket.SocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerHealthCheckerImpl implements ServerHealthChecker {

    public static final int SOCKET_TIMEOUT = 1_000;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHealthCheckerImpl.class);

    private final SocketFactory socketFactory;

    public ServerHealthCheckerImpl(final SocketFactory socketFactory) {
        this.socketFactory = socketFactory;

    }

    /**
     * {@inheritDoc}
     *
     * @implNote socket timeout is {@link #SOCKET_TIMEOUT}
     */
    @Override
    public boolean isAlive(final String host,
                           final int port) {
        try (final Socket socket = socketFactory.create()) {
            socket.connect(new InetSocketAddress(host, port), SOCKET_TIMEOUT);
            return true;
        } catch (final IOException e) {
            LOGGER.warn("Server at {}:{} is not available", host, port, e);
            return false;
        }
    }
}
