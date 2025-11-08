package com.morgan.alexander.server.registry;

import com.morgan.alexander.server.model.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class})
class RoundRobinServerRegistryTest {

    private ServerRegistry testee;

    @BeforeEach
    void setUp() {
        this.testee = new RoundRobinServerRegistry();
    }

    @Nested
    class next {

        @Test
        void first() {
            final Server actual = testee.next();

            assertThat(actual)
                    .isEqualTo(new Server("127.0.0.1", 8081))
                    .hasNoNullFieldsOrProperties();
        }

        @Test
        void second() {
            testee.next();
            final Server actual = testee.next();

            assertThat(actual)
                    .isEqualTo(new Server("127.0.0.1", 8082))
                    .hasNoNullFieldsOrProperties();
        }

        @Test
        void third() {
            testee.next();
            testee.next();
            final Server actual = testee.next();

            assertThat(actual)
                    .isEqualTo(new Server("127.0.0.1", 8083))
                    .hasNoNullFieldsOrProperties();
        }

        @Test
        void fourth() {
            testee.next();
            testee.next();
            testee.next();
            final Server actual = testee.next();

            assertThat(actual)
                    .isEqualTo(new Server("127.0.0.1", 8084))
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
                    .isEqualTo(new Server("127.0.0.1", 8081))
                    .hasNoNullFieldsOrProperties();
        }

        @Test
        void loopsBackToNth_100Times() {
            Server actual;
            for (int i = 0; i < 100; i++) {
                actual = testee.next();

                int index = i % 4;

                assertThat(actual)
                        .isEqualTo(new Server("127.0.0.1", 8081 + index))
                        .hasNoNullFieldsOrProperties();
            }
        }
    }

}