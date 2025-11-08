package com.morgan.alexander.server.registry;

import com.morgan.alexander.server.model.Server;

@FunctionalInterface
public interface ServerRegistryServerProvider {

    Server next();
}
