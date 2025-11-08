package com.morgan.alexander.server.registry;

import com.morgan.alexander.socket.SocketFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import static com.morgan.alexander.server.registry.ServerHealthCheckerImpl.SOCKET_TIMEOUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class ServerHealthCheckerImplTest {

    @Mock
    private SocketFactory socketFactory;

    private ServerHealthChecker testee;

    @BeforeEach
    void setUp() {
        this.testee = new ServerHealthCheckerImpl(
                socketFactory
        );
    }

    @Nested
    class isAlive {

        @Test
        void success() throws IOException {
            final Socket socket = mock(Socket.class);
            when(socketFactory.create())
                    .thenReturn(socket);

            doNothing()
                    .when(socket)
                    .connect(new InetSocketAddress("127.0.0.1", 8081), SOCKET_TIMEOUT);

            final boolean actual = testee.isAlive("127.0.0.1", 8081);

            assertThat(actual).isTrue();
        }

        @Test
        void error() throws IOException {
            final Socket socket = mock(Socket.class);
            when(socketFactory.create())
                    .thenReturn(socket);

            doThrow(new IOException("Oh no"))
                    .when(socket)
                    .connect(new InetSocketAddress("127.0.0.1", 8081), SOCKET_TIMEOUT);

            final boolean actual = testee.isAlive("127.0.0.1", 8081);

            assertThat(actual).isFalse();
        }
    }
}