package com.morgan.alexander;

import com.morgan.alexander.datatransfer.SocketDataTransferServiceImpl;
import com.morgan.alexander.loadbalancer.ClientLoadBalancer;
import com.morgan.alexander.loadbalancer.ClientLoadBalancerImpl;
import com.morgan.alexander.loadbalancer.L4LoadBalancer;
import com.morgan.alexander.loadbalancer.L4LoadBalancerImpl;
import com.morgan.alexander.server.registry.*;
import com.morgan.alexander.socket.ServerSocketFactoryImpl;
import com.morgan.alexander.socket.SocketFactoryImpl;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Main class, entry-point for load balancer.
 */
public class Main {
    public static void main(final String... args) throws IOException {
        final ExecutorService dataTransferPool = Executors.newCachedThreadPool();

        final ServerRegistry serverRegistry = new RoundRobinServerRegistry();
        final SocketFactoryImpl socketFactory = new SocketFactoryImpl();
        final ClientLoadBalancer clientLoadBalancer = new ClientLoadBalancerImpl(
                dataTransferPool,
                serverRegistry,
                socketFactory,
                new SocketDataTransferServiceImpl()
        );


        final ExecutorService clientPool = Executors.newCachedThreadPool();
        final L4LoadBalancer l4LoadBalancer = new L4LoadBalancerImpl(
                clientPool,
                new ServerSocketFactoryImpl(),
                clientLoadBalancer
        );

        l4LoadBalancer.start();

        final ScheduledExecutorService scheduledPool = Executors.newSingleThreadScheduledExecutor();
        final ServerRegistryPrunerService serverRegistryPrunerService = new ServerRegistryPrunerServiceImpl(
                scheduledPool,
                serverRegistry,
                new ServerHealthCheckerImpl(socketFactory)
        );

        serverRegistryPrunerService.start();
    }
}
