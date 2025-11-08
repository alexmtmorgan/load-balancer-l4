package com.morgan.alexander.server.registry;

import com.morgan.alexander.server.model.Server;

public class ServerRegistryImpl implements ServerRegistry {

    @Override
    public Server next() {
        return new Server("https://google.com", 443);
    }
}
