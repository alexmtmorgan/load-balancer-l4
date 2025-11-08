package com.morgan.alexander.loadbalancer;

import com.morgan.alexander.socket.ServerSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class L4LoadBalancerImpl implements L4LoadBalancer, AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(L4LoadBalancerImpl.class);
    public static final int CLIENT_PORT = 9_876;

    private volatile boolean running;

    private final ExecutorService clientPool;
    private final ServerSocketFactory serverSocketFactory;
    private final ClientLoadBalancer clientLoadBalancer;

    public L4LoadBalancerImpl(final ExecutorService clientPool,
                              final ServerSocketFactory serverSocketFactory,
                              final ClientLoadBalancer clientLoadBalancer) {
        this.running = false;
        this.clientPool = clientPool;
        this.clientLoadBalancer = clientLoadBalancer;
        this.serverSocketFactory = serverSocketFactory;
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
        this.running = true;

        LOGGER.info("Opening up port {} to listen to client connections: ", CLIENT_PORT);
        try (final ServerSocket clientServerSocket = this.serverSocketFactory.create(CLIENT_PORT)) {
            LOGGER.info("Accepting connections on port: {}", CLIENT_PORT);
            while (this.running) {
                final Socket clientSocket = clientServerSocket.accept();
                clientPool.submit(() -> {
                    try {
                        clientLoadBalancer.loadBalance(clientSocket);
                    } catch (final Exception ex) {
                        LOGGER.error("Exception occurred during client socket session for {}:{}",
                                clientSocket.getInetAddress(), clientSocket.getPort());
                        throw ex;
                    }
                });
            }
        }
    }

    /**
     * Stops the load balancer from accepting any new client connections.
     */
    @Override
    public void stop() {
        this.running = false;
    }

    @Override
    public void close() throws Exception {
        stop();
        this.clientPool.shutdown();
    }
}
