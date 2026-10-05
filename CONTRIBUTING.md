# Contributing

## Setup

Run `nix develop` (or `direnv allow`). It provides JDK 25 and Maven and installs the git hooks.

Without Nix, use JDK 21 or later and Maven 3.9 or later. The git hooks are then not installed, so format Java with [google-java-format](https://github.com/google/google-java-format) yourself.

## Build and test

| Command | Purpose |
|---|---|
| `mvn verify` | build and run the tests |
| `mvn test -Dtest=FinalClassTest` | run the tests of one check |
| `mvn install` | install the plugin into the local Maven repository to try it in another project |
| `nix fmt` | format Java and Nix |
| `nix flake check` | run the git hooks over every tracked file |

## Proposing a change

Open an issue before you start on a new check or a change to what a check flags, with code that it should and should not flag. A small fix, such as a typo, can go straight to a pull request.

A pull request:

- references its issue (`Closes #N`).
- adds tests in `src/test/java` with `CompilationTestHelper`, and with `BugCheckerRefactoringTestHelper` for a suggested fix. A fix for a false positive adds a test that reproduces it.
- registers a new check with `@AutoService(BugChecker.class)` and adds it to the table in `README.md`.
- passes `mvn verify`.
- has commit messages that follow Conventional Commits, which the `commit-msg` hook checks.
