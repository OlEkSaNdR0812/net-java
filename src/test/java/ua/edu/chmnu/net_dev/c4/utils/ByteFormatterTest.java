package ua.edu.chmnu.net_dev.c4.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ByteFormatterTest {

    @CsvSource({
            "178, 178 B",
            "20781, 20.3 KiB",
            "168824, 164.9 KiB",
            "78983521, 75.3 MiB"
    })
    @ParameterizedTest
    void shouldCorrectFormat(long value, String expected) {
        assertEquals(expected, ByteFormatter.format(value));
    }
}