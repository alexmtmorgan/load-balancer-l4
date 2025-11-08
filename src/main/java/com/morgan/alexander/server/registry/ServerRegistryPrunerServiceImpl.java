package com.morgan.alexander.server.registry;

import com.morgan.alexander.server.model.Server;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerRegistryPrunerServiceImpl implements ServerRegistryPrunerService, AutoCloseable {

    public static final long FREQUENCY_OF_HEALTH_CHECK = 30L;
    public static final long INITIAL_DELAY = 0L;

    private final ScheduledExecutorService scheduledPool;
    private final ServerRegistryInventoryProvider serverRegistryInventoryProvider;
    private final ServerHealthChecker serverHealthChecker;

    public ServerRegistryPrunerServiceImpl(final ScheduledExecutorService scheduledPool,
                                           final ServerRegistryInventoryProvider serviceRegistryDetailsProvider,
                                           final ServerHealthChecker serverHealthChecker) {
        this.scheduledPool = scheduledPool;
        this.serverRegistryInventoryProvider = serviceRegistryDetailsProvider;
        this.serverHealthChecker = serverHealthChecker;
    }

    @Override
    public void start() {
        scheduledPool.scheduleWithFixedDelay(() ->
                this.serverRegistryInventoryProvider.all().forEach(serverStatus -> {
                    final Server server = serverStatus.server();
                    boolean alive = serverHealthChecker.isAlive(server.host(), server.port());
                    if (alive != serverStatus.isHealthy()) {
                        serverStatus.setHealthy(alive);
                    }
                }), INITIAL_DELAY, FREQUENCY_OF_HEALTH_CHECK, TimeUnit.SECONDS);
    }

    @Override
    public void close() {
        this.scheduledPool.close();
    }
}
