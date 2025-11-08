package com.morgan.alexander.server.registry;

import com.morgan.alexander.server.model.Server;

import java.util.ArrayList;
import java.util.List;

public class ServerRegistryImpl implements ServerRegistry {

    private final List<Server> servers;
    private int roundRobinCounter;

    public ServerRegistryImpl() {
        this.servers = new ArrayList<>(
                List.of(
                        new Server("https://server.us-east-1.com", 443),
                        new Server("https://server.us-east-2.com", 443),
                        new Server("https://server.us-west-1.com", 443),
                        new Server("https://server.us-west-2.com", 443)
                )
        );
        this.roundRobinCounter = 0;
    }

    @Override
    public Server next() {
        final Server nextServer = servers.get(roundRobinCounter);
        updateRoundRobinCounter();
        return nextServer;
    }

    private void updateRoundRobinCounter() {
        if (roundRobinCounter + 1 == servers.size()) {
            roundRobinCounter = 0;
        } else {
            roundRobinCounter += 1;
        }
    }
}
