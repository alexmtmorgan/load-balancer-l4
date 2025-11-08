package com.morgan.alexander.loadbalancer;

import com.morgan.alexander.datatransfer.SocketDataTransferService;
import com.morgan.alexander.server.model.Server;
import com.morgan.alexander.server.registry.ServerRegistry;
import com.morgan.alexander.socket.ServerSocketFactory;
import com.morgan.alexander.socket.SocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class L4LoadBalancerImpl implements L4LoadBalancer {

    private static final Logger LOGGER = LoggerFactory.getLogger(L4LoadBalancerImpl.class);
    public static final int CLIENT_PORT = 9_876;

    private final ExecutorService dataTransferPool;

    private final ServerSocketFactory serverSocketFactory;
    private final ServerRegistry serverRegistry;
    private final SocketFactory socketFactory;
    private final SocketDataTransferService socketDataTransferService;

    public L4LoadBalancerImpl(final ExecutorService dataTransferPool,
                              final ServerSocketFactory serverSocketFactory,
                              final ServerRegistry serverRegistry,
                              final SocketFactory socketFactory,
                              final SocketDataTransferService socketDataTransferService) {
        this.dataTransferPool = dataTransferPool;
        this.serverSocketFactory = serverSocketFactory;
        this.serverRegistry = serverRegistry;
        this.socketFactory = socketFactory;
        this.socketDataTransferService = socketDataTransferService;
    }

    /**
     * Does the following:
     * <ol>
     *     <li>Creates a ServerSocket to accept incoming client connections</li>
     *     <li>Accepts incoming client connection</li>
     *     <li>Retrieves load-balanced server</li>
     *     <li>Creates socket connection to server</li>
     *     <li>Initiates bi-directional data transfer</li>
     * </ol>
     *
     * @throws IOException in case of i/o error creating the client server socket connection
     * @implNote does not support multiple client connections.
     */
    @Override
    public void start() throws IOException {
        LOGGER.info("Opening up port {} to listen to client connections: ", CLIENT_PORT);
        try (final ServerSocket clientServerSocket = this.serverSocketFactory.create(CLIENT_PORT)) {
            LOGGER.info("Accepting connections on port: {}", CLIENT_PORT);
            final Socket clientSocket = clientServerSocket.accept();
            LOGGER.debug("Accepted connection from client: {}:{}",
                    clientSocket.getInetAddress(), clientSocket.getPort());
            final Server server = serverRegistry.next();

            // do not use try-with-resources or socket is closed once thread is submitted
            try {
                final Socket serverSocket = socketFactory.create(server.host(), server.port());
                dataTransferPool.execute(transferData(clientSocket, serverSocket));
                dataTransferPool.execute(transferData(serverSocket, clientSocket));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Runnable transferData(final Socket in,
                                  final Socket out) {
        return () -> {
            try {
                socketDataTransferService.transferData(in, out);
            } catch (IOException e) {
                LOGGER.error("Error occurred during data transfer from {}:{} to {}:{}",
                        in.getInetAddress(), in.getPort(), out.getInetAddress(), out.getPort(), e);
            }
        };
    }

}
