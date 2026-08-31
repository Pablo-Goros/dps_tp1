package edu.itba.dps.tp1.exchange.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class ApiKeyLoader {

	static final String API_KEY_VARIABLE = "CURRENCY_API_KEY";
	private static final Path DEFAULT_DOTENV_FILE = Path.of(".env");

	private final Function<String, String> environment;
	private final Path dotenvFile;
	private final FileReader fileReader;

	public ApiKeyLoader() {
		this(System::getenv, DEFAULT_DOTENV_FILE, Files::readAllLines);
	}

	ApiKeyLoader(Function<String, String> environment, Path dotenvFile, FileReader fileReader) {
		this.environment = environment;
		this.dotenvFile = dotenvFile;
		this.fileReader = fileReader;
	}

	public String load() {
		final String environmentValue = environment.apply(API_KEY_VARIABLE);
		if (environmentValue != null && !environmentValue.isBlank()) {
			return environmentValue.strip();
		}

		if (!Files.exists(dotenvFile)) {
			throw new IllegalStateException("Missing " + API_KEY_VARIABLE
					+ ": set a nonblank environment variable or add it to " + dotenvFile);
		}

		final List<String> lines;
		try {
			lines = fileReader.read(dotenvFile);
		} catch (IOException | SecurityException exception) {
			throw new IllegalStateException("Cannot read API configuration from " + dotenvFile, exception);
		}

		final String prefix = API_KEY_VARIABLE + "=";
		final String fileValue = lines.stream()
				.map(String::strip)
				.filter(line -> line.startsWith(prefix))
				.map(line -> line.substring(prefix.length()).strip())
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(
						"Missing " + API_KEY_VARIABLE + " in " + dotenvFile));

		if (fileValue.isBlank()) {
			throw new IllegalStateException(API_KEY_VARIABLE + " must not be blank in " + dotenvFile);
		}
		return fileValue;
	}

	@FunctionalInterface
	interface FileReader {
		List<String> read(Path path) throws IOException;
	}
}
