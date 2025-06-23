# Prewave - Alert Term Extraction

This repository provides a Scala solution for the **Alert Term Extraction** challenge. The following sections outline the task and describe how the project is organised.

## The Challenge

The goal is to identify the occurrence of specific query terms inside text-based alerts. Two REST endpoints provide the required data:

- **Query Terms**: `https://services.prewave.ai/adminInterface/api/testQueryTerm?key=<your-key>`  
  Each term has an `id`, `text`, `language` (two letter code) and a boolean `keepOrder` flag. If `keepOrder` is `true`, all parts of the term must appear exactly in that order. Otherwise the words may appear in any order anywhere in the text.
- **Alerts**: `https://services.prewave.ai/adminInterface/api/testAlerts?key=<your-key>`  
  An alert contains an `id`, a list of `contents` objects (`text`, `type`, `language`), a `date` and an `inputType`.

The alerts endpoint returns different data on every request, while the query term list remains stable. The expected output of the program is a set of pairs `(alertId, queryTermId)` without duplicates indicating where each term occurs.

## Project Overview

The implementation uses **Scala 3** with [cats-effect](https://typelevel.org/cats-effect/) and [http4s](https://http4s.org/) for the HTTP layer. The code is split into two main modules.

### matcher

This module consumes the REST APIs and performs the term matching.

Main components:

- **`AppMain`** – entry point. It loads the terms via `TermClient` and starts the processing loop in `MatcherLoop`.
- **`MatcherLoop`** – runs continuously (every 10 seconds) querying new alerts through `AlertClient`. Only unseen alerts are considered (tracked via `AlertCache`). For each new alert the `MatcherEngine` detects term occurrences and prints the result.
- **`MatcherEngine`** – verifies whether a term occurs in a text, respecting the `keepOrder` setting and the alert language.
- **`AlertClient`** and **`TermClient`** – perform the HTTP requests.
- **`AlertCache`** – keeps in memory the ids of already processed alerts.
- **Tests** – `MatcherEngineSpec.scala` covers the matching logic.

### Architectural Notes

To support the expectations of an interview scenario, several architectural decisions were made:

- **Separation of concerns**: Core logic, HTTP clients, loop control, matching logic, and caching are all isolated in their respective files and packages.
- **Mock server**: A local server was implemented to simulate the endpoints, enabling independent and deterministic testing.
- **Configurable environment**: A `ConfigLoader` reads a `useRemote` flag and the corresponding URLs from `application.conf`, allowing dynamic switching between real and local endpoints without code changes.
- **Streaming simulation**: Though the input is polling-based, the matcher is structured as if it were processing a continuous event stream.
- **Match logic expressiveness**: Terms with `keepOrder = true` require contiguous word sequences. For `keepOrder = false`, word presence is sufficient, even if out of order or interleaved.
- **Logging and feedback**: Custom println outputs make match results human-readable and informative. Graceful error handling and loop resilience were added.
- **Learning context**: The author’s background is in Java, and this implementation was an opportunity to explore Scala by leveraging AI tools as part of the development process.

### mock-server

A minimal local server also written in Scala that generates fake data for the term and alert endpoints. It is useful for testing without the remote APIs.

## Running the Project

1. (Optional) Start the mock server with `sbt run` inside the `mock-server` directory.
2. Inside `matcher`, run `sbt run` to launch the application. The console will display the matches found in each cycle.

Unit tests can be executed with `sbt test` in the `matcher` directory.

## Final Considerations

This project was designed as a practical response to a coding challenge. The implementation balances **clean structure**, **realistic extensibility**, and **learning goals**. While it does not aim to be production-grade, the core design allows for future evolution and reflects good development practices in terms of modularity, configuration management, testability, and error handling.

The author does not yet have professional experience with Scala but undertook this task with a proactive mindset, combining foundational knowledge from Java with modern development tools and the support of AI assistants.
## Development Time

The project was developed in approximately **4 to 5 hours** of focused implementation time to cover all required functionalities. An additional **1 to 2 hours** were spent preparing the repository structure and writing this README documentation.
