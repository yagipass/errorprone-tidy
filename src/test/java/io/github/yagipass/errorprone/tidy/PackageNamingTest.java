package io.github.yagipass.errorprone.tidy;

import com.google.errorprone.CompilationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PackageNamingTest {
  private final CompilationTestHelper compilationHelper =
      CompilationTestHelper.newInstance(PackageNaming.class, getClass());

  @Test
  public void lowercaseLettersAndDigits_followGoogleJavaStyle() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            package com.example.deepspace2;

            class Test {}
            """)
        .doTest();
  }

  @Test
  public void uppercaseLetter_isNotAllowed() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            // BUG: Diagnostic contains: `com.example.deepSpace`
            package com.example.deepSpace;

            class Test {}
            """)
        .doTest();
  }

  @Test
  public void uppercaseInFirstComponent_isNotAllowed() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            // BUG: Diagnostic contains: PackageNaming
            package Com.example;

            class Test {}
            """)
        .doTest();
  }

  @Test
  public void underscore_isNotAllowed() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            // BUG: Diagnostic contains: PackageNaming
            package com.example.deep_space;

            class Test {}
            """)
        .doTest();
  }

  @Test
  public void suppressionInPackageInfo_coversEveryFileOfALegacyPackage() {
    compilationHelper
        .addSourceLines(
            "package-info.java",
            """
            @SuppressWarnings("PackageNaming")
            package com.example.deep_space;
            """)
        .addSourceLines(
            "Test.java",
            """
            package com.example.deep_space;

            class Test {}
            """)
        .doTest();
  }

  @Test
  public void defaultPackage_hasNoNameToCheck() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            class Test {}
            """)
        .doTest();
  }
}
