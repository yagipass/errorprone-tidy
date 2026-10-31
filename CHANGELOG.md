# Changelog

## [v0.1.1](https://github.com/yagipass/errorprone-tidy/compare/v0.1.0...v0.1.1) - 2026-10-31

### Documentation
- docs: drop the mvn install step now that the plugin is on Maven Central by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/24
- docs: show Gradle usage and drop the version from the README by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/27
- docs: add badges to the README by @O1sumitkumar in https://github.com/yagipass/errorprone-tidy/pull/33
### Dependencies
- build(deps): bump gradle-wrapper from 9.8.0 to 9.8.1 by @dependabot[bot] in https://github.com/yagipass/errorprone-tidy/pull/42
- build(deps): bump nixpkgs from `73e728d` to `39ad350` by @dependabot[bot] in https://github.com/yagipass/errorprone-tidy/pull/41
- build(deps): bump com.diffplug.spotless from 8.10.3 to 8.10.4 by @dependabot[bot] in https://github.com/yagipass/errorprone-tidy/pull/38
- build(deps): bump io.github.yagipass:spotless-member-order from 0.1.0 to 0.2.0 by @dependabot[bot] in https://github.com/yagipass/errorprone-tidy/pull/39
- build(deps): bump Songmu/tagpr from 1.21.1 to 1.21.2 by @dependabot[bot] in https://github.com/yagipass/errorprone-tidy/pull/51
- build(deps): bump nixpkgs from `39ad350` to `8edc0c7` by @dependabot[bot] in https://github.com/yagipass/errorprone-tidy/pull/50
- build(deps): bump io.github.yagipass:spotless-member-order from 0.2.0 to 0.3.0 by @dependabot[bot] in https://github.com/yagipass/errorprone-tidy/pull/58
- build(deps): bump nixpkgs from `8edc0c7` to `f20cd90` by @dependabot[bot] in https://github.com/yagipass/errorprone-tidy/pull/66
- build(deps): bump Songmu/tagpr from 1.21.2 to 1.21.3 by @dependabot[bot] in https://github.com/yagipass/errorprone-tidy/pull/67
- build(deps): bump Songmu/tagpr from 1.21.3 to 1.21.4 by @dependabot[bot] in https://github.com/yagipass/errorprone-tidy/pull/83
- build(deps): bump gradle-wrapper from 9.8.1 to 9.8.2 by @dependabot[bot] in https://github.com/yagipass/errorprone-tidy/pull/82
- build(deps): bump treefmt-nix from `27b3b12` to `0d6d739` in the flake-inputs group by @dependabot[bot] in https://github.com/yagipass/errorprone-tidy/pull/81
- build(deps): bump errorprone from 2.50.0 to 2.51.0 by @dependabot[bot] in https://github.com/yagipass/errorprone-tidy/pull/80
- build(deps): bump com.uber.nullaway:nullaway from 0.14.2 to 0.14.3 by @dependabot[bot] in https://github.com/yagipass/errorprone-tidy/pull/84
### Other changes
- build: migrate the build from Maven to Gradle by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/26
- feat: attach the plugin jar to GitHub releases by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/32
- feat: check Java formatting with Spotless and google-java-format by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/35
- chore: ignore VS Code settings by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/37
- feat: check the order of Java members with spotless-member-order by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/36
- fix: switch to the built-in gitleaks hook and update git-hooks by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/44
- build: lock GitHub Actions dependencies with a workflow lockfile by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/47
- feat: update GitHub Actions weekly with Dependabot by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/48
- fix: resolve the jactionlint findings in workflows and Dependabot by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/57
- fix: limit the Maven Central secrets to release jobs on main by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/59
- feat: manage the repository settings with gh infra by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/61
- ci: group the changelog by pull request labels by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/63
- chore: configure CodeRabbit reviews for this repository by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/65
- build: put pre-commit on PATH in the dev shell by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/69
- build: lint GitHub Actions files with jactionlint in the git hooks by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/71
- chore: keep CodeRabbit's summary out of pull request descriptions by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/75
- chore: configure cubic reviews for this repository by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/73
- docs: add rules and release steps to the contributing guide by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/77
- build: check every tracked file in the git hooks and run them in CI by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/79

## [v0.1.0](https://github.com/yagipass/errorprone-tidy/commits/v0.1.0) - 2026-10-05

- feat: add pre-commit checks to the dev shell by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/9
- feat: build and test pull requests in CI by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/10
- feat: update Maven dependencies weekly with Dependabot by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/11
- feat: update nixpkgs weekly with Dependabot by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/12
- docs: add a security policy by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/13
- docs: add a contributing guide by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/14
- feat: compile own sources under Error Prone, NullAway, and -Werror by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/15
- feat: publish releases to Maven Central with the release profile by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/16
- feat: release to Maven Central from main with tagpr by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/18
- refactor: name SimplifyBooleanExpression's binary outcomes with an enum by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/22
- feat: reuse Nix store paths and Maven dependencies across CI runs by @yagipass in https://github.com/yagipass/errorprone-tidy/pull/23
