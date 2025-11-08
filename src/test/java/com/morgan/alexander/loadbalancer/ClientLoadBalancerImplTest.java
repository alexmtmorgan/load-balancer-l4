package com.morgan.alexander.loadbalancer;

import com.morgan.alexander.datatransfer.SocketDataTransferService;
import com.morgan.alexander.server.model.Server;
import com.morgan.alexander.server.registry.ServerRegistry;
import com.morgan.alexander.socket.SocketFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class ClientLoadBalancerImplTest {

    @Captor
    private ArgumentCaptor<Runnable> runnableArgumentCaptor;

    @Mock
    private ExecutorService dataTransferPool;
    @Mock
    private ServerRegistry serverRegistry;
    @Mock
    private SocketFactory socketFactory;
    @Mock
    private SocketDataTransferService socketDataTransferService;

    private ClientLoadBalancer testee;

    @BeforeEach
    void setUp() {
        this.testee = new ClientLoadBalancerImpl(
                dataTransferPool, serverRegistry, socketFactory, socketDataTransferService
        );
    }

    @Nested
    class loadBalance {

        @Test
        void success() throws IOException {
            final Socket clientSocket = mock(Socket.class);

            final String serverHost = "127.0.0.1";
            final int serverPort = 5432;
            final Server server = new Server(serverHost, serverPort);
            when(serverRegistry.next())
                    .thenReturn(server);

            final Socket loadBalancedServerSocket = mock(Socket.class);
            when(socketFactory.create(serverHost, serverPort))
                    .thenReturn(loadBalancedServerSocket);

            when(dataTransferPool.submit(any(Runnable.class)))
                    .thenReturn(null);

            doNothing()
                    .when(socketDataTransferService)
                    .transferData(clientSocket, loadBalancedServerSocket);
            doNothing()
                    .when(socketDataTransferService)
                    .transferData(loadBalancedServerSocket, clientSocket);

            testee.loadBalance(clientSocket);

            final InOrder inOrder = inOrder(dataTransferPool, socketDataTransferService);
            inOrder.verify(dataTransferPool, times(2)).submit(runnableArgumentCaptor.capture());
            runnableArgumentCaptor.getAllValues().forEach(Runnable::run);

            inOrder.verify(socketDataTransferService).transferData(clientSocket, loadBalancedServerSocket);
            inOrder.verify(socketDataTransferService).transferData(loadBalancedServerSocket, clientSocket);
        }
    }
}
