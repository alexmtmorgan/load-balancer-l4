package com.morgan.alexander.server.registry;

import com.morgan.alexander.server.model.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class})
class ServerRegistryImplTest {

    private static final Map<Integer, String> SERVER_HOSTS = Map.of(
            0, "us-east-1",
            1, "us-east-2",
            2, "us-west-1",
            3, "us-west-2"
    );

    private ServerRegistry testee;

    @BeforeEach
    void setUp() {
        this.testee = new ServerRegistryImpl();
    }

    @Nested
    class next {

        @Test
        void first() {
            final Server actual = testee.next();

            assertThat(actual)
                    .isEqualTo(new Server("https://server.us-east-1.com", 443))
                    .hasNoNullFieldsOrProperties();
        }

        @Test
        void second() {
            testee.next();
            final Server actual = testee.next();

            assertThat(actual)
                    .isEqualTo(new Server("https://server.us-east-2.com", 443))
                    .hasNoNullFieldsOrProperties();
        }

        @Test
        void third() {
            testee.next();
            testee.next();
            final Server actual = testee.next();

            assertThat(actual)
                    .isEqualTo(new Server("https://server.us-west-1.com", 443))
                    .hasNoNullFieldsOrProperties();
        }

        @Test
        void fourth() {
            testee.next();
            testee.next();
            testee.next();
            final Server actual = testee.next();

            assertThat(actual)
                    .isEqualTo(new Server("https://server.us-west-2.com", 443))
                    .hasNoNullFieldsOrProperties();
        }

        @Test
        void loopsBackToFirst() {
            testee.next();
            testee.next();
            testee.next();
            testee.next();
            final Server actual = testee.next();

            assertThat(actual)
                    .isEqualTo(new Server("https://server.us-east-1.com", 443))
                    .hasNoNullFieldsOrProperties();
        }

        @Test
        void loopsBackToNth_100Times() {
            Server actual;
            for(int i = 0; i < 100; i++) {
                actual = testee.next();

                final String serverLocation = SERVER_HOSTS.get(i % 4);

                assertThat(actual)
                        .isEqualTo(new Server("https://server.%s.com".formatted(serverLocation), 443))
                        .hasNoNullFieldsOrProperties();
            }
        }
    }

}