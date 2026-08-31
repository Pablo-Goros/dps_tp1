# Currency Converter

This repository contains the first practical assignment for ITBA's *Diseño y
Paradigmas de Software*. It is a Java command-line currency converter backed by
[Free Currency API](https://freecurrencyapi.com/).

The application demonstrates these features:

- listing supported currencies;
- obtaining a current exchange-rate quote;
- converting one amount to one or several currencies;
- converting with historical rates for a requested date;
- reporting both a historical rate's effective date and the time at which each
  API response was retrieved.

## Prerequisites

- JDK 21
- Maven 3.9 or newer
- A Free Currency API account and API key

Confirm the tools are available with `java -version` and `mvn -version`.

## Configuration

Register at [freecurrencyapi.com](https://freecurrencyapi.com/), copy
`.env.example` to a file named `.env` in the repository root, and replace the
placeholder:

```dotenv
CURRENCY_API_KEY=your_real_api_key
```

A nonblank `CURRENCY_API_KEY` environment variable takes precedence over the
root `.env` file. **Never commit `.env` or a real API key.** The `.env` file is
ignored by Git; `.env.example` must contain only a placeholder. If a key is ever
exposed, revoke or rotate it immediately in the provider dashboard.

## Build, test, and run

From the repository root:

```shell
mvn clean package
mvn test
mvn verify
mvn test jacoco:report
mvn exec:java -Dexec.mainClass=edu.itba.dps.tp1.exchange.main.Main
```

`mvn verify` runs the unit tests and enforces the coverage threshold. The HTML
coverage report is generated at `target/site/jacoco/index.html`. Running the
application makes live API requests; unit tests use mocked HTTP responses and
never need the real API key.

## Architecture

The code follows a ports-and-adapters structure:

- `domain` contains immutable value objects such as `MoneyAmount` and
  `CurrencyRate`;
- `application` coordinates conversion use cases in `CurrencyManager`;
- `ports` define the provider contracts needed by the application;
- `infrastructure` implements HTTP, API, and console adapters;
- `main` contains the CLI and the composition root.

This separation applies SOLID principles by keeping domain and application code
independent of HTTP and console details, using small provider interfaces, and
injecting implementations at startup. `ApiKeyLoader` isolates configuration
loading, while injectable clocks make retrieval timestamps deterministic in
tests.

## Coverage policy

JaCoCo requires 100% line and branch coverage for included production classes.
Only `Main` is excluded from both reporting and verification because it is a
logic-free composition root that constructs concrete dependencies and starts
the CLI. Configuration behavior is not excluded: it lives in `ApiKeyLoader` and
is fully unit tested.
