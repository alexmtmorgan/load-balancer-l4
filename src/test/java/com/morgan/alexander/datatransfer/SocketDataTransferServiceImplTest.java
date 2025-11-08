package com.morgan.alexander.datatransfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SocketDataTransferServiceImplTest {

    private SocketDataTransferService testee;

    @BeforeEach
    void setUp() {
        this.testee = new SocketDataTransferServiceImpl();
    }

    @Nested
    class transferData {

        @Test
        void success() throws IOException {
            final byte[] inputBytes = {1, 0, 0, 1};
            final InputStream inputStream = new ByteArrayInputStream(inputBytes);
            final Socket inputSocket = mock(Socket.class);
            when(inputSocket.getInputStream())
                    .thenReturn(inputStream);

            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(inputBytes.length);
            final Socket outputSocket = mock(Socket.class);
            when(outputSocket.getOutputStream())
                    .thenReturn(outputStream);

            testee.transferData(inputSocket, outputSocket);

            assertThat(outputStream.toByteArray()).isEqualTo(inputBytes);
        }
    }
}