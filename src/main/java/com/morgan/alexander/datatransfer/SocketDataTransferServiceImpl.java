package com.morgan.alexander.datatransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Implementation of {@link SocketDataTransferService}.
 */
public class SocketDataTransferServiceImpl implements SocketDataTransferService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketDataTransferServiceImpl.class);

    @Override
    public void transferData(final Socket inputSocket,
                             final Socket outputSocket) throws IOException {
        try (final InputStream input = inputSocket.getInputStream();
             final OutputStream output = outputSocket.getOutputStream()) {
            LOGGER.info("Transferring data from {} to {}", inputSocket.getInetAddress(), outputSocket.getInetAddress());
            input.transferTo(output);
        }
    }
}
