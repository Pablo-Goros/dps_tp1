package edu.itba.dps.tp1.exchange.infrastructure.io;

import edu.itba.dps.tp1.exchange.io.OutputWriter;

public class ConsoleWriter implements OutputWriter {

	@Override
	public void write(String message) {
		System.out.println(message);
	}
}
