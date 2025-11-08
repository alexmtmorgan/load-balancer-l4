package com.morgan.alexander.server.registry;

import com.morgan.alexander.server.model.Server;
import com.morgan.alexander.server.model.ServerStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinServerRegistry implements ServerRegistry {

    private final List<ServerStatus> servers;
    private final AtomicInteger roundRobinCounter;

    public RoundRobinServerRegistry() {
        this.servers =
                List.of(new ServerStatus(new Server("127.0.0.1", 8081)),
                        new ServerStatus(new Server("127.0.0.1", 8082)),
                        new ServerStatus(new Server("127.0.0.1", 8083)),
                        new ServerStatus(new Server("127.0.0.1", 8084)));
        this.roundRobinCounter = new AtomicInteger(0);
    }

    @Override
    public Server next() {
        int attempts = 0;
        int numberOfServers = servers.size();

        while (attempts < numberOfServers) {
            final int index = roundRobinCounter.getAndIncrement() % numberOfServers;
            final ServerStatus nextServer = servers.get(index);

            if (nextServer.isHealthy()) {
                return nextServer.server();
            }
            attempts++;
        }

        throw new IllegalStateException("Failure to find any healthy servers");
    }

    /**
     * Returns a copy of the list of servers.
     *
     * @return the {@link Server}.
     */
    @Override
    public List<ServerStatus> all() {
        return new ArrayList<>(this.servers);
    }
}
