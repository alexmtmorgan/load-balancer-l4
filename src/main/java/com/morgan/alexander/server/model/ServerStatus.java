package com.morgan.alexander.server.model;

public class ServerStatus {
    private final Server server;
    private volatile boolean healthy;

    public ServerStatus(Server server) {
        this.server = server;
        this.healthy = true;
    }

    public Server server() {
        return server;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }
}
