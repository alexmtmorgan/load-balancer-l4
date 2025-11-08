package com.morgan.alexander.loadbalancer;

import java.net.Socket;

/**
 * Client-specific load balancer responsible for load-balancing for the client socket.
 */
public interface ClientLoadBalancer {

    /**
     * Perform the load balancing for connections on the provided {@code clientSocket}.
     *
     * @param clientSocket the {@link Socket} to load-balance for.
     */
    void loadBalance(Socket clientSocket);
}
