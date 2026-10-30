# Contributing

## Setup

Run `nix develop` (or `direnv allow`). It provides JDK 25 and installs the git hooks.

Without Nix, use JDK 21 or later. The git hooks are then not installed.

## Build and test

| Command | Purpose |
|---|---|
| `./gradlew build` | build, run the tests, and check the Java formatting and member order |
| `./gradlew test --tests FinalClassTest` | run the tests of one check |
| `./gradlew spotlessApply` | order the members of Java types with spotless-member-order and format Java with google-java-format |
| `./gradlew publishToMavenLocal` | install the plugin into the local Maven repository to try it in another project |
| `nix fmt` | format Nix |
| `nix flake check` | run the git hooks over every tracked file |

## Rules

- `./gradlew build` fails on unformatted Java, including members out of the order set in `build.gradle.kts`, and on any javac (`-Xlint:all,-options,-processing -Werror`) or Error Prone warning, NullAway in `src/main` included.
- Commit messages follow Conventional Commits, checked by the `commit-msg` hook. `feat` and `fix` are for changes to the checks. Changes to the build, CI, or tooling use `build`, `ci`, or `chore`.
- Name branches `<type>/<slug>` after the Conventional Commits type. `.github/labeler.yml` labels the pull request by it, `feat/` as `enhancement` and `fix/` as `bug` when they change `src/main`, and `docs/` as `documentation`, and `.github/release.yml` groups the changelog by these labels, `breaking-change`, and Dependabot's `dependencies`.

## Proposing a change

Open an issue before you start on a new check or a change to what a check flags, with code that it should and should not flag. A small fix, such as a typo, can go straight to a pull request.

A pull request:

- references its issue (`Closes #N`), if it has one.
- adds tests when it changes what a check flags or fixes, in `src/test/java` with `CompilationTestHelper`, and with `BugCheckerRefactoringTestHelper` for a suggested fix. A fix for a false positive adds a test that reproduces it.
- registers a new check with `@AutoService(BugChecker.class)` and adds it to the table in `README.md`.
- passes `./gradlew build`.
- has a Conventional Commits title. Pull requests are squash-merged, so the title becomes the commit on `main`.

## Release

Before merging a pull request, label it by hand if it comes from a fork, which the labeler skips, and add `breaking-change` if it breaks existing users.

Merge the release pull request that [tagpr](https://github.com/Songmu/tagpr) keeps open. It is a minor release if a merged pull request is labeled `enhancement` or `breaking-change`, and a patch release otherwise. Label it `tagpr:major` for a major release. `release.yml` then tags `vX.Y.Z`, builds and tests the tag in the dev shell, uploads the signed artifacts to Maven Central, attaches the jar to the GitHub release, and publishes the release. Then publish the deployment by hand in the [Central Portal](https://central.sonatype.com/publishing/deployments).

tagpr writes `CHANGELOG.md` and the version in `gradle.properties`, so do not edit them by hand.

This needs Actions to be allowed to create pull requests, and an environment `maven-central` with four secrets: `MAVEN_CENTRAL_TOKEN_USERNAME` and `MAVEN_CENTRAL_TOKEN_PASSWORD` (a Central Portal user token), and `MAVEN_GPG_PRIVATE_KEY` and `MAVEN_GPG_PASSPHRASE` (the ASCII-armored signing key and its passphrase).

## Repository settings

The repository settings, labels, rulesets, and Actions permissions are managed with [gh-infra](https://github.com/babarot/gh-infra) in `.github/infra.yml`. To change them, edit the file, review the difference with `gh infra plan .github/infra.yml`, and apply it with `gh infra apply .github/infra.yml`. Applying needs admin access to the repository.

Environments and secrets are not managed by gh-infra. Change them in the repository settings on GitHub.
