package com.morgan.alexander.loadbalancer;

import com.morgan.alexander.socket.ServerSocketFactory;
import com.morgan.alexander.socket.SocketFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class L4LoadBalancerImplTest {
    @Mock
    private ServerSocketFactory serverSocketFactory;
    @Mock
    private SocketFactory socketFactory;

    private L4LoadBalancer testee;

    @BeforeEach
    void setUp() {
        this.testee = new L4LoadBalancerImpl(
                serverSocketFactory,
                socketFactory
        );
    }

    @Nested
    class start {

        @Test
        void success() throws IOException {
            final ServerSocket mockClientServerSocket = mock(ServerSocket.class);
            when(serverSocketFactory.create(9_876))
                    .thenReturn(mockClientServerSocket);
            final Socket clientSocket = mock(Socket.class);
            when(mockClientServerSocket.accept())
                    .thenReturn(clientSocket);

            final Socket loadBalancedServerSocket = mock(Socket.class);
            when(socketFactory.create("127.0.0.1", 5432))
                    .thenReturn(loadBalancedServerSocket);

            byte[] clientBytes = {1, 0, 0, 1};
            final InputStream clientInputStream = new ByteArrayInputStream(clientBytes);
            when(clientSocket.getInputStream())
                    .thenReturn(clientInputStream);

            final ByteArrayOutputStream serverOutputStream = new ByteArrayOutputStream(clientBytes.length);
            when(loadBalancedServerSocket.getOutputStream())
                    .thenReturn(serverOutputStream);

            testee.start();

            assertThat(serverOutputStream.toByteArray()).isEqualTo(clientBytes);
        }
    }

}