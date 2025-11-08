package com.morgan.alexander.socket;

import java.io.IOException;
import java.net.Socket;

/**
 * Factory for producing new {@link Socket}(s).
 */
public interface SocketFactory {
    /**
     * Creates a {@link Socket} bound to the provided host and port.
     *
     * @param host the host to use
     * @param port the port to use
     * @return the {@link  Socket}
     * @throws IOException in case of failure to create the {@link Socket}
     */
    Socket create(String host, int port) throws IOException;

    /**
     * Creates a {@link Socket} unbound to any specific host and port.
     *
     * @return the {@link  Socket}
     * @throws IOException in case of failure to create the {@link Socket}
     */
    Socket create() throws IOException;
}
