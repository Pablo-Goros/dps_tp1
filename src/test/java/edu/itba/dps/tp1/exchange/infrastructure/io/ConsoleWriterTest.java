package edu.itba.dps.tp1.exchange.infrastructure.io;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsoleWriterTest {

	private final PrintStream originalOut = System.out;
	private final ByteArrayOutputStream capturedOut = new ByteArrayOutputStream();

	@BeforeEach
	void redirectSystemOut() {
		System.setOut(new PrintStream(capturedOut, true, StandardCharsets.UTF_8));
	}

	@AfterEach
	void restoreSystemOut() {
		System.setOut(originalOut);
	}

	@Test
	void writesTheMessageFollowedByANewLine() {
		new ConsoleWriter().write("hello");

		assertEquals("hello" + System.lineSeparator(), capturedOut.toString(StandardCharsets.UTF_8));
	}
}
