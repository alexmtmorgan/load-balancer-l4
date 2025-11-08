package com.morgan.alexander.loadbalancer;

import java.io.IOException;

/**
 * A simple L4LoadBalancer to transfer data from client(s) to load-balanced server(s).
 */
public interface L4LoadBalancer {

    /**
     * Starts up the load balancer.
     * @throws IOException if IO error occurs that is unrecoverable.
     */
    void start() throws IOException;
}
