package com.morgan.alexander.loadbalancer;

import com.morgan.alexander.server.Server;
import com.morgan.alexander.socket.ServerSocketFactory;
import com.morgan.alexander.socket.SocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class L4LoadBalancerImpl implements L4LoadBalancer {

    private static final Logger LOGGER = LoggerFactory.getLogger(L4LoadBalancerImpl.class);
    public static final int CLIENT_PORT = 9_876;

    private final ServerSocketFactory serverSocketFactory;
    private final SocketFactory socketFactory;

    public L4LoadBalancerImpl(final ServerSocketFactory serverSocketFactory,
                              final SocketFactory socketFactory) {
        this.serverSocketFactory = serverSocketFactory;
        this.socketFactory = socketFactory;
    }

    @Override
    public void start() throws IOException {
        LOGGER.info("Opening up port {} to listen to client connections: ", CLIENT_PORT);
        try (final ServerSocket clientServerSocket = this.serverSocketFactory.create(CLIENT_PORT)) {
            LOGGER.info("Accepting connections on port: {}", CLIENT_PORT);
            final Socket clientSocket = clientServerSocket.accept();
            LOGGER.debug("Accepted connection from client: {}", clientSocket.getInetAddress());
            // TODO get the server to connect to
            try(final Socket serverSocket = socketFactory.create("127.0.0.1", 5432)) {

                try (final InputStream input = clientSocket.getInputStream();
                     final OutputStream output = serverSocket.getOutputStream()) {
                    LOGGER.info("Transferring data from client: {} to server: {}",
                            clientSocket.getInetAddress(), serverSocket.getInetAddress());
                    input.transferTo(output);
                }
            }
        }
    }
}
