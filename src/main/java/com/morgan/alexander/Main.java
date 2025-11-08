package com.morgan.alexander;

import com.morgan.alexander.datatransfer.SocketDataTransferServiceImpl;
import com.morgan.alexander.loadbalancer.L4LoadBalancer;
import com.morgan.alexander.loadbalancer.L4LoadBalancerImpl;
import com.morgan.alexander.server.registry.ServerRegistryImpl;
import com.morgan.alexander.socket.ServerSocketFactoryImpl;
import com.morgan.alexander.socket.SocketFactoryImpl;

import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * Main class, entry-point for load balancer.
 */
public class Main {
    public static void main(final String... args) throws IOException {
        final L4LoadBalancer l4LoadBalancer = new L4LoadBalancerImpl(
                Executors.newCachedThreadPool(),
                new ServerSocketFactoryImpl(),
                new ServerRegistryImpl(),
                new SocketFactoryImpl(),
                new SocketDataTransferServiceImpl()
        );
        l4LoadBalancer.start();
    }
}
