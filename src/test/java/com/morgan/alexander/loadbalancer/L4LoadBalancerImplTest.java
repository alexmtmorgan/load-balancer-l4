package com.morgan.alexander.loadbalancer;

import com.morgan.alexander.datatransfer.SocketDataTransferService;
import com.morgan.alexander.socket.ServerSocketFactory;
import com.morgan.alexander.socket.SocketFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class L4LoadBalancerImplTest {
    @Mock
    private ServerSocketFactory serverSocketFactory;
    @Mock
    private SocketFactory socketFactory;
    @Mock
    private SocketDataTransferService socketDataTransferService;

    private L4LoadBalancer testee;

    @BeforeEach
    void setUp() {
        this.testee = new L4LoadBalancerImpl(
                serverSocketFactory,
                socketFactory,
                socketDataTransferService
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

            doNothing()
                    .when(socketDataTransferService)
                    .transferData(clientSocket, loadBalancedServerSocket);
            doNothing()
                    .when(socketDataTransferService)
                    .transferData(loadBalancedServerSocket, clientSocket);



            testee.start();

            final InOrder inOrder = inOrder(socketDataTransferService);
            inOrder.verify(socketDataTransferService).transferData(clientSocket, loadBalancedServerSocket);
            inOrder.verify(socketDataTransferService).transferData(loadBalancedServerSocket, clientSocket);
        }
    }

}