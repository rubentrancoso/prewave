# Prewave - Alert Term Extraction

This repository provides a Scala solution for the **Alert Term Extraction** challenge. The following sections outline the task and describe how the project is organised.

## The Challenge

The goal is to identify the occurrence of specific query terms inside text-based alerts. Two REST endpoints provide the required data:

- **Query Terms**: `https://services.prewave.ai/adminInterface/api/testQueryTerm?key=<your-key>`
  - Each term has an `id`, `text`, `language` (two letter code) and a boolean `keepOrder` flag. If `keepOrder` is `true`, all parts of the term must appear exactly in that order. Otherwise the words may appear in any order anywhere in the text.
- **Alerts**: `https://services.prewave.ai/adminInterface/api/testAlerts?key=<your-key>`
  - An alert contains an `id`, a list of `contents` objects (`text`, `type`, `language`), a `date` and an `inputType`.

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

Configuration lives in `matcher/src/main/resources/application.conf`. Set `prewave.useRemote = true` to query the real endpoints or keep `false` to rely on the local mock server.

### mock-server

A minimal local server also written in Scala that generates fake data for the term and alert endpoints. It is useful for testing without the remote APIs.

## Running the Project

1. (Optional) Start the mock server with `sbt run` inside the `mock-server` directory.
2. Inside `matcher`, run `sbt run` to launch the application. The console will display the matches found in each cycle.

Unit tests can be executed with `sbt test` in the `matcher` directory.

## Notes

If `sbt` or its dependencies cannot be installed, ensure that the required Scala libraries are available in the environment so the project can be built and run.
