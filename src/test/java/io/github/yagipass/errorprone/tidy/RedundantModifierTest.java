package io.github.yagipass.errorprone.tidy;

import com.google.errorprone.BugCheckerRefactoringTestHelper;
import com.google.errorprone.CompilationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RedundantModifierTest {
  private final CompilationTestHelper compilationHelper =
      CompilationTestHelper.newInstance(RedundantModifier.class, getClass());
  private final BugCheckerRefactoringTestHelper refactoringHelper =
      BugCheckerRefactoringTestHelper.newInstance(RedundantModifier.class, getClass());

  @Test
  public void interfaceMembers_arePublicAndStaticByDefinition() {
    refactoringHelper
        .addInputLines(
            "Test.java",
            """
            abstract interface Test {
              public static final int X = 1;

              public abstract void a();

              public default void b() {}

              public static void c() {}

              public static class Nested {}

              public static enum E {}
            }
            """)
        .addOutputLines(
            "Test.java",
            """
            interface Test {
              int X = 1;

              void a();

              default void b() {}

              static void c() {}

              class Nested {}

              enum E {}
            }
            """)
        .doTest();
  }

  @Test
  public void annotationElements_arePublicAndAbstractByDefinition() {
    refactoringHelper
        .addInputLines(
            "Test.java",
            """
            @interface Test {
              public abstract String value();
            }
            """)
        .addOutputLines(
            "Test.java",
            """
            @interface Test {
              String value();
            }
            """)
        .doTest();
  }

  @Test
  public void implicitModifiers_areNotReported() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            interface Test {
              int X = 1;

              void a();

              default void b() {}

              class Nested {}

              enum E {
                ONE;

                void c() {}
              }

              record R() {}

              @interface A {
                String value();
              }
            }
            """)
        .addSourceLines(
            "Outer.java",
            """
            final class Outer {
              void a() {}

              enum E {}

              record R() {}

              interface I {}

              Object o =
                  new Object() {
                    void b() {}
                  };
            }
            """)
        .doTest();
  }

  @Test
  public void privateInterfaceMethod_keepsItsModifier() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            interface Test {
              private void a() {}

              private static void b() {}
            }
            """)
        .doTest();
  }

  @Test
  public void classNestedInInterface_keepsModifiersThatAreNotImplied() {
    refactoringHelper
        .addInputLines(
            "Test.java",
            """
            interface Test {
              public static final class Nested {}
            }
            """)
        .addOutputLines(
            "Test.java",
            """
            interface Test {
              final class Nested {}
            }
            """)
        .doTest();
  }

  @Test
  public void nestedEnumRecordAndInterface_areStaticByDefinition() {
    refactoringHelper
        .addInputLines(
            "Test.java",
            """
            class Test {
              static enum E {}

              static record R() {}

              static interface I {}

              static @interface A {}
            }
            """)
        .addOutputLines(
            "Test.java",
            """
            class Test {
              enum E {}

              record R() {}

              interface I {}

              @interface A {}
            }
            """)
        .doTest();
  }

  @Test
  public void staticOnNestedClass_changesItsMeaning() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            class Test {
              static class Nested {}
            }
            """)
        .doTest();
  }

  @Test
  public void modifiersOnClassFields_changeTheirMeaning() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            class Test {
              public static final int X = 1;
            }
            """)
        .doTest();
  }

  @Test
  public void record_isFinalByDefinition() {
    refactoringHelper
        .addInputLines(
            "Test.java",
            """
            final record Test(int x) {}
            """)
        .addOutputLines(
            "Test.java",
            """
            record Test(int x) {}
            """)
        .doTest();
  }

  @Test
  public void methodsOfClassesThatCannotBeExtended_cannotBeOverridden() {
    refactoringHelper
        .addInputLines(
            "Test.java",
            """
            final class Test {
              final void a() {}

              record R() {
                final void b() {}
              }

              enum E {
                ONE;

                final void c() {}
              }

              Object o =
                  new Object() {
                    final void d() {}
                  };
            }
            """)
        .addOutputLines(
            "Test.java",
            """
            final class Test {
              void a() {}

              record R() {
                void b() {}
              }

              enum E {
                ONE;

                void c() {}
              }

              Object o =
                  new Object() {
                    void d() {}
                  };
            }
            """)
        .doTest();
  }

  @Test
  public void finalMethod_preventsOverridingInExtensibleClasses() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            class Test {
              final void a() {}

              enum E {
                ONE {
                  @Override
                  void d() {}
                };

                final void c() {}

                void d() {}
              }
            }
            """)
        .doTest();
  }

  @Test
  public void safeVarargsMethod_mustBeFinalItself_evenInFinalClasses() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            final class Test {
              @SafeVarargs
              final <T> void a(T... xs) {}
            }
            """)
        .doTest();
  }

  @Test
  public void publicConstructorOfPackagePrivateClass_isVisibleToReflection() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            class Test {
              public Test() {}
            }
            """)
        .doTest();
  }

  @Test
  public void diagnostic_namesTheRedundantModifiers() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            interface Test {
              // BUG: Diagnostic contains: `public abstract` is implied here
              public abstract void a();
            }
            """)
        .doTest();
  }
}
