package com.morgan.alexander.server.registry;

import com.morgan.alexander.server.model.Server;
import com.morgan.alexander.server.model.ServerStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.morgan.alexander.server.registry.ServerRegistryPrunerServiceImpl.FREQUENCY_OF_HEALTH_CHECK;
import static com.morgan.alexander.server.registry.ServerRegistryPrunerServiceImpl.INITIAL_DELAY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class ServerRegistryPrunerServiceImplTest {

    @Captor
    private ArgumentCaptor<Runnable> runnableArgumentCaptor;

    @Mock
    private ScheduledExecutorService scheduledExecutorService;
    @Mock
    private ServerRegistryInventoryProvider serverRegistryInventoryProvider;
    @Mock
    private ServerHealthChecker serverHealthChecker;

    private ServerRegistryPrunerService testee;

    @BeforeEach
    void setUp() {
        this.testee = new ServerRegistryPrunerServiceImpl(
                scheduledExecutorService, serverRegistryInventoryProvider, serverHealthChecker
        );
    }

    @Nested
    class start {


        //this.serverRegistryInventoryProvider.all().forEach(serverStatus -> {
        //                    final Server server = serverStatus.server();
        //                    boolean alive = serverHealthChecker.isAlive(server.host(), server.port());
        //                    if (alive != serverStatus.isHealthy()) {
        //                        serverStatus.setHealthy(alive);
        //                    }
        //                })

        @Test
        void noServers() {
            when(scheduledExecutorService.scheduleWithFixedDelay(any(), eq(INITIAL_DELAY), eq(FREQUENCY_OF_HEALTH_CHECK), eq(TimeUnit.SECONDS)))
                    .thenReturn(null);

            when(serverRegistryInventoryProvider.all())
                    .thenReturn(List.of());

            testee.start();

            verify(scheduledExecutorService).scheduleWithFixedDelay(runnableArgumentCaptor.capture(),
                    eq(INITIAL_DELAY), eq(FREQUENCY_OF_HEALTH_CHECK), eq(TimeUnit.SECONDS));
            runnableArgumentCaptor.getValue().run();

            verify(serverRegistryInventoryProvider).all();

            verifyNoInteractions(serverHealthChecker);
        }

        @Test
        void oneServer_WasAlive_And_IsStillAlive() {
            when(scheduledExecutorService.scheduleWithFixedDelay(any(), eq(INITIAL_DELAY), eq(FREQUENCY_OF_HEALTH_CHECK), eq(TimeUnit.SECONDS)))
                    .thenReturn(null);

            final ServerStatus serverStatus = new ServerStatus(new Server("127.0.0.1", 9876));
            serverStatus.setHealthy(true);
            when(serverRegistryInventoryProvider.all())
                    .thenReturn(List.of(serverStatus));

            when(serverHealthChecker.isAlive("127.0.0.1", 9876))
                    .thenReturn(true);

            testee.start();

            verify(scheduledExecutorService).scheduleWithFixedDelay(runnableArgumentCaptor.capture(),
                    eq(INITIAL_DELAY), eq(FREQUENCY_OF_HEALTH_CHECK), eq(TimeUnit.SECONDS));
            runnableArgumentCaptor.getValue().run();

            assertThat(serverStatus.isHealthy()).isTrue();

            verify(serverRegistryInventoryProvider).all();
            verify(serverHealthChecker).isAlive("127.0.0.1", 9876);

            verifyNoMoreInteractions(scheduledExecutorService, serverRegistryInventoryProvider, serverHealthChecker);
        }

        @Test
        void oneServer_WasNotAlive_And_IsNowAlive() {
            when(scheduledExecutorService.scheduleWithFixedDelay(any(), eq(INITIAL_DELAY), eq(FREQUENCY_OF_HEALTH_CHECK), eq(TimeUnit.SECONDS)))
                    .thenReturn(null);

            final ServerStatus serverStatus = new ServerStatus(new Server("127.0.0.1", 9876));
            serverStatus.setHealthy(false);
            when(serverRegistryInventoryProvider.all())
                    .thenReturn(List.of(serverStatus));

            when(serverHealthChecker.isAlive("127.0.0.1", 9876))
                    .thenReturn(true);

            testee.start();

            verify(scheduledExecutorService).scheduleWithFixedDelay(runnableArgumentCaptor.capture(),
                    eq(INITIAL_DELAY), eq(FREQUENCY_OF_HEALTH_CHECK), eq(TimeUnit.SECONDS));
            runnableArgumentCaptor.getValue().run();

            assertThat(serverStatus.isHealthy()).isTrue();

            verify(serverRegistryInventoryProvider).all();
            verify(serverHealthChecker).isAlive("127.0.0.1", 9876);

            verifyNoMoreInteractions(scheduledExecutorService, serverRegistryInventoryProvider, serverHealthChecker);
        }

        @Test
        void oneServer_WasAlive_And_IsNowNotAlive() {
            when(scheduledExecutorService.scheduleWithFixedDelay(any(), eq(INITIAL_DELAY), eq(FREQUENCY_OF_HEALTH_CHECK), eq(TimeUnit.SECONDS)))
                    .thenReturn(null);

            final ServerStatus serverStatus = new ServerStatus(new Server("127.0.0.1", 9876));
            serverStatus.setHealthy(true);
            when(serverRegistryInventoryProvider.all())
                    .thenReturn(List.of(serverStatus));

            when(serverHealthChecker.isAlive("127.0.0.1", 9876))
                    .thenReturn(false);

            testee.start();

            verify(scheduledExecutorService).scheduleWithFixedDelay(runnableArgumentCaptor.capture(),
                    eq(INITIAL_DELAY), eq(FREQUENCY_OF_HEALTH_CHECK), eq(TimeUnit.SECONDS));
            runnableArgumentCaptor.getValue().run();

            assertThat(serverStatus.isHealthy()).isFalse();

            verify(serverRegistryInventoryProvider).all();
            verify(serverHealthChecker).isAlive("127.0.0.1", 9876);

            verifyNoMoreInteractions(scheduledExecutorService, serverRegistryInventoryProvider, serverHealthChecker);
        }

        @Test
        void oneServer_WasNotAlive_And_IsStillNotAlive() {
            when(scheduledExecutorService.scheduleWithFixedDelay(any(), eq(INITIAL_DELAY), eq(FREQUENCY_OF_HEALTH_CHECK), eq(TimeUnit.SECONDS)))
                    .thenReturn(null);

            final ServerStatus serverStatus = new ServerStatus(new Server("127.0.0.1", 9876));
            serverStatus.setHealthy(false);
            when(serverRegistryInventoryProvider.all())
                    .thenReturn(List.of(serverStatus));

            when(serverHealthChecker.isAlive("127.0.0.1", 9876))
                    .thenReturn(false);

            testee.start();

            verify(scheduledExecutorService).scheduleWithFixedDelay(runnableArgumentCaptor.capture(),
                    eq(INITIAL_DELAY), eq(FREQUENCY_OF_HEALTH_CHECK), eq(TimeUnit.SECONDS));
            runnableArgumentCaptor.getValue().run();

            assertThat(serverStatus.isHealthy()).isFalse();

            verify(serverRegistryInventoryProvider).all();
            verify(serverHealthChecker).isAlive("127.0.0.1", 9876);

            verifyNoMoreInteractions(scheduledExecutorService, serverRegistryInventoryProvider, serverHealthChecker);
        }
    }
}