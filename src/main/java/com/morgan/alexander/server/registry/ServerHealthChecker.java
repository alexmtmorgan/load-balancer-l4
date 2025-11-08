package com.morgan.alexander.server.registry;

/**
 * Health check service for validating server health.
 */
public interface ServerHealthChecker {

    /**
     * Checks if server at specified {@code host} and {@code port} is reachable.
     *
     * @param host the host to connect to
     * @param port the port to connect to
     * @return boolean flag indicating alive-ness
     */
    boolean isAlive(String host,
                    int port);
}
