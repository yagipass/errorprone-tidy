package io.github.yagipass.errorprone.tidy;

import com.google.errorprone.BugCheckerRefactoringTestHelper;
import com.google.errorprone.CompilationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FinalClassTest {
  private final CompilationTestHelper compilationHelper =
      CompilationTestHelper.newInstance(FinalClass.class, getClass());
  private final BugCheckerRefactoringTestHelper refactoringHelper =
      BugCheckerRefactoringTestHelper.newInstance(FinalClass.class, getClass());

  @Test
  public void onlyPrivateConstructors_cannotBeExtended_soFinalIsAdded() {
    refactoringHelper
        .addInputLines(
            "Test.java",
            """
            public class Test {
              private Test() {}

              private Test(int x) {}
            }
            """)
        .addOutputLines(
            "Test.java",
            """
            public final class Test {
              private Test() {}

              private Test(int x) {}
            }
            """)
        .doTest();
  }

  @Test
  public void privateNestedClassWithDefaultConstructor_cannotBeExtendedOutsideFile() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            class Test {
              // BUG: Diagnostic contains: FinalClass
              private static class Nested {}
            }
            """)
        .doTest();
  }

  @Test
  public void subclassInSameFile_needsTheClassToStayExtensible() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            class Test {
              private static class Base {
                private Base() {}
              }

              private static final class Derived extends Base {}
            }
            """)
        .doTest();
  }

  @Test
  public void anonymousSubclassInSameFile_needsTheClassToStayExtensible() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            class Test {
              private static class Base {
                private Base() {}
              }

              Object o = new Base() {};
            }
            """)
        .doTest();
  }

  @Test
  public void nonPrivateConstructor_allowsSubclassesInOtherFiles() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            class Test {
              private Test() {}

              Test(int x) {}
            }
            """)
        .addSourceLines(
            "Outer.java",
            """
            class Outer {
              static class Nested {}
            }
            """)
        .doTest();
  }

  @Test
  public void abstractClass_cannotBeFinal() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            abstract class Test {
              private Test() {}
            }
            """)
        .doTest();
  }

  @Test
  public void nonSealedClass_cannotAlsoBeFinal() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            sealed interface Test permits Test.Impl {
              non-sealed class Impl implements Test {
                private Impl() {}
              }
            }
            """)
        .doTest();
  }
}
