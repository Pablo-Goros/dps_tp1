package edu.itba.dps.tp1.exchange.main;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiKeyLoaderTest {

	@TempDir
	Path temporaryDirectory;

	@Test
	void nonblankEnvironmentValueTakesPrecedenceOverDotenv() throws IOException {
		final Path dotenv = dotenv("CURRENCY_API_KEY=file-key");
		final var loader = loader("  environment-key  ", dotenv);

		assertEquals("environment-key", loader.load());
	}

	@Test
	void readsAndTrimsTheKeyFromDotenvWhenTheEnvironmentIsAbsent() throws IOException {
		final Path dotenv = dotenv("# local configuration\n  CURRENCY_API_KEY = ignored\nCURRENCY_API_KEY=  file-key  ");

		assertEquals("file-key", loader(null, dotenv).load());
	}

	@Test
	void blankEnvironmentValueFallsBackToDotenv() throws IOException {
		final Path dotenv = dotenv("CURRENCY_API_KEY=file-key");

		assertEquals("file-key", loader("  ", dotenv).load());
	}

	@Test
	void reportsAMissingDotenvFile() {
		final Path missing = temporaryDirectory.resolve("missing.env");

		final var exception = assertThrows(IllegalStateException.class, () -> loader(null, missing).load());

		assertTrue(exception.getMessage().contains("Missing CURRENCY_API_KEY"));
	}

	@Test
	void reportsAKeyMissingFromDotenv() throws IOException {
		final Path dotenv = dotenv("ANOTHER_KEY=value");

		final var exception = assertThrows(IllegalStateException.class, () -> loader(null, dotenv).load());

		assertTrue(exception.getMessage().contains("Missing CURRENCY_API_KEY in"));
	}

	@Test
	void reportsABlankKeyInDotenv() throws IOException {
		final Path dotenv = dotenv("CURRENCY_API_KEY=   ");

		final var exception = assertThrows(IllegalStateException.class, () -> loader(null, dotenv).load());

		assertTrue(exception.getMessage().contains("must not be blank"));
	}

	@Test
	void reportsAnInvalidDotenvEntry() throws IOException {
		final Path dotenv = dotenv("CURRENCY_API_KEY");

		assertThrows(IllegalStateException.class, () -> loader(null, dotenv).load());
	}

	@Test
	void reportsAnUnreadableDotenvFile() throws IOException {
		final Path dotenv = dotenv("CURRENCY_API_KEY=file-key");
		final var loader = new ApiKeyLoader(name -> null, dotenv, path -> {
			throw new IOException("access denied");
		});

		final var exception = assertThrows(IllegalStateException.class, loader::load);

		assertTrue(exception.getMessage().contains("Cannot read API configuration"));
		assertTrue(exception.getCause() instanceof IOException);
	}

	@Test
	void defaultLoaderUsesTheProcessEnvironmentAndRootDotenv() {
		new ApiKeyLoader();
	}

	private ApiKeyLoader loader(String environmentValue, Path dotenv) {
		return new ApiKeyLoader(name -> environmentValue, dotenv, Files::readAllLines);
	}

	private Path dotenv(String contents) throws IOException {
		final Path file = temporaryDirectory.resolve(".env");
		return Files.writeString(file, contents);
	}
}
