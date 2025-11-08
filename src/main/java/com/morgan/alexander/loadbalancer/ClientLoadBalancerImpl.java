package com.morgan.alexander.loadbalancer;

import com.morgan.alexander.datatransfer.SocketDataTransferService;
import com.morgan.alexander.server.model.Server;
import com.morgan.alexander.server.registry.ServerRegistry;
import com.morgan.alexander.socket.SocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * Perform client load balancing.
 */
public class ClientLoadBalancerImpl implements ClientLoadBalancer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientLoadBalancerImpl.class);

    private final ExecutorService dataTransferPool;
    private final ServerRegistry serverRegistry;
    private final SocketFactory socketFactory;
    private final SocketDataTransferService socketDataTransferService;

    public ClientLoadBalancerImpl(final ExecutorService dataTransferPool,
                                  final ServerRegistry serverRegistry,
                                  final SocketFactory socketFactory,
                                  final SocketDataTransferService socketDataTransferService) {
        this.dataTransferPool = dataTransferPool;
        this.serverRegistry = serverRegistry;
        this.socketFactory = socketFactory;
        this.socketDataTransferService = socketDataTransferService;
    }

    @Override
    public void loadBalance(final Socket clientSocket) {
        LOGGER.debug("Accepted connection from client: {}:{}",
                clientSocket.getInetAddress(), clientSocket.getPort());
        final Server server = serverRegistry.next();

        // do not use try-with-resources or socket is closed once thread is submitted
        try {
            final Socket serverSocket = socketFactory.create(server.host(), server.port());
            dataTransferPool.submit(transferData(clientSocket, serverSocket));
            dataTransferPool.submit(transferData(serverSocket, clientSocket));
        } catch (IOException e) {
            throw new RuntimeException(e);
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
