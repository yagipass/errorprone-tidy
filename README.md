# errorprone-tidy

Additional [Error Prone](https://errorprone.info) checks for tidier Java code.

| Check | Severity | Flags |
|---|---|---|
| `FinalClass` | SUGGESTION | Non-final classes whose constructors are all private and that have no subclass in the same file |
| `RedundantModifier` | WARNING | Modifiers implied by context: `public`/`abstract`/`static`/`final` on interface members, `abstract` on interfaces, `static` on nested enums, records and interfaces, `final` on records and on methods of final or anonymous classes |
| `SimplifyBooleanExpression` | WARNING | `b == true`, `b != false`, `b && true`, `b \|\| false`, `!false`, `c ? true : false` |
| `SimplifyBooleanReturn` | WARNING | `if (c) return true; else return false;` (also with `yield`) |
| `PackageNaming` | WARNING | Package names with uppercase letters or underscores ([Google Java Style §5.2.1](https://google.github.io/styleguide/javaguide.html#s5.2.1-package-names)) |

## Usage

Requires Error Prone 2.50.0 or later on JDK 21 or later. Set up Error Prone as described in its [installation guide](https://errorprone.info/docs/installation), then add this plugin next to `error_prone_core`:

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
    <version>0.1.0</version>
  </path>
</annotationProcessorPaths>
```

To turn off a check, pass `-Xep:<CheckName>:OFF` to Error Prone.
