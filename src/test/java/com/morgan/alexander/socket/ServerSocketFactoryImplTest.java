package com.morgan.alexander.socket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;

import static org.assertj.core.api.Assertions.assertThat;

class ServerSocketFactoryImplTest {

    private ServerSocketFactory testee;

    @BeforeEach
    void setUp() {
        this.testee = new ServerSocketFactoryImpl();
    }

    @Nested
    class create_withPort {

        @Test
        void success() throws IOException {
            try (final ServerSocket actual = testee.create(10_000)) {
                assertThat(actual.getLocalPort()).isEqualTo(10_000);
            }
        }
    }
}