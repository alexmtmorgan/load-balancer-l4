package com.morgan.alexander.loadbalancer;

import com.morgan.alexander.socket.ServerSocketFactory;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class L4LoadBalancerImplTest {

    @Captor
    private ArgumentCaptor<Runnable> runnableArgumentCaptor;

    @Mock
    private ExecutorService clientPool;
    @Mock
    private ServerSocketFactory serverSocketFactory;
    @Mock
    private ClientLoadBalancer clientLoadBalancer;

    private L4LoadBalancer testee;

    @BeforeEach
    void setUp() {
        this.testee = new L4LoadBalancerImpl(
                clientPool,
                serverSocketFactory,
                clientLoadBalancer
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
                    .thenAnswer((invocation -> {
                        // ensure only loops once
                        testee.stop();
                        return clientSocket;
                    }));

            when(clientPool.submit(any(Runnable.class)))
                    .thenReturn(null);

            doNothing()
                    .when(clientLoadBalancer).loadBalance(clientSocket);

            testee.start();

            final InOrder inOrder = inOrder(clientPool, clientLoadBalancer);
            inOrder.verify(clientPool).submit(runnableArgumentCaptor.capture());
            runnableArgumentCaptor.getValue().run();

            inOrder.verify(clientLoadBalancer).loadBalance(clientSocket);

            verifyNoMoreInteractions(serverSocketFactory, clientPool, clientLoadBalancer);
        }
    }

}