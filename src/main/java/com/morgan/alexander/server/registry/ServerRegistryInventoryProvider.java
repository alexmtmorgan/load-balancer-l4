package com.morgan.alexander.server.registry;

import com.morgan.alexander.server.model.ServerStatus;

import java.util.List;

@FunctionalInterface
public interface ServerRegistryInventoryProvider {

    List<ServerStatus> all();
}
