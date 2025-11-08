package com.morgan.alexander.datatransfer;

import java.io.IOException;
import java.net.Socket;

/**
 * Service for uni-directional transferal of data between two sockets.
 */
public interface SocketDataTransferService {
    /**
     * Transfer data from {@code inputSocket} to {@code outputSocket}.
     *
     * @param inputSocket  the input socket
     * @param outputSocket the output socket
     * @throws IOException in case of IO error during data transfer
     */
    void transferData(Socket inputSocket,
                      Socket outputSocket) throws IOException;
}
