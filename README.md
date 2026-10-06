# errorprone-tidy

[![CI](https://github.com/yagipass/errorprone-tidy/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/yagipass/errorprone-tidy/actions/workflows/ci.yml?query=branch%3Amain)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.yagipass/errorprone-tidy)](https://central.sonatype.com/artifact/io.github.yagipass/errorprone-tidy)
[![Release](https://img.shields.io/github/v/release/yagipass/errorprone-tidy)](https://github.com/yagipass/errorprone-tidy/releases/latest)
[![License](https://img.shields.io/github/license/yagipass/errorprone-tidy)](LICENSE)
![Java 21+](https://img.shields.io/badge/Java-21%2B-blue)
![Error Prone 2.50.0+](https://img.shields.io/badge/Error_Prone-2.50.0%2B-blue)

Additional [Error Prone](https://errorprone.info) checks for tidier Java code.

| Check | Severity | Flags |
|---|---|---|
| `FinalClass` | SUGGESTION | Non-final classes whose constructors are all private and that have no subclass in the same file |
| `RedundantModifier` | WARNING | Modifiers implied by context: `public`/`abstract`/`static`/`final` on interface members, `abstract` on interfaces, `static` on nested enums, records and interfaces, `final` on records and on methods of final or anonymous classes |
| `SimplifyBooleanExpression` | WARNING | `b == true`, `b != false`, `b && true`, `b \|\| false`, `!false`, `c ? true : false` |
| `SimplifyBooleanReturn` | WARNING | `if (c) return true; else return false;` (also with `yield`) |
| `PackageNaming` | WARNING | Package names with uppercase letters or underscores ([Google Java Style §5.2.1](https://google.github.io/styleguide/javaguide.html#s5.2.1-package-names)) |

## Usage

Requires Error Prone 2.50.0 or later on JDK 21 or later. Set up Error Prone as described in its [installation guide](https://errorprone.info/docs/installation), then add this plugin next to `error_prone_core`.

Maven:

```xml
<annotationProcessorPaths>
  <path>
    <groupId>com.google.errorprone</groupId>
    <artifactId>error_prone_core</artifactId>
    <version>${errorprone.version}</version>
  </path>
  <path>
    <groupId>io.github.yagipass</groupId>
    <artifactId>errorprone-tidy</artifactId>
    <version>${errorprone-tidy.version}</version>
  </path>
</annotationProcessorPaths>
```

Gradle, with [gradle-errorprone-plugin](https://github.com/tbroyer/gradle-errorprone-plugin):

```kotlin
dependencies {
  errorprone("com.google.errorprone:error_prone_core:$errorproneVersion")
  errorprone("io.github.yagipass:errorprone-tidy:$errorproneTidyVersion")
}
```

To turn off a check, pass `-Xep:<CheckName>:OFF` to Error Prone.
