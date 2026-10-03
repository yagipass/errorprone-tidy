package io.github.yagipass.errorprone.tidy;

import com.google.errorprone.BugCheckerRefactoringTestHelper;
import com.google.errorprone.CompilationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SimplifyBooleanReturnTest {
  private final CompilationTestHelper compilationHelper =
      CompilationTestHelper.newInstance(SimplifyBooleanReturn.class, getClass());
  private final BugCheckerRefactoringTestHelper refactoringHelper =
      BugCheckerRefactoringTestHelper.newInstance(SimplifyBooleanReturn.class, getClass());

  @Test
  public void returningLiteralsFromBothBranches_returnsTheCondition() {
    refactoringHelper
        .addInputLines(
            "Test.java",
            """
            class Test {
              boolean f(int a) {
                if (a > 0) {
                  return true;
                } else {
                  return false;
                }
              }

              boolean g(int a) {
                if (a > 0) return false;
                else return true;
              }

              boolean h(boolean a, boolean b) {
                if (a && b) {
                  return false;
                } else {
                  return true;
                }
              }

              boolean i(boolean a) {
                if (!a) {
                  return false;
                } else {
                  return true;
                }
              }
            }
            """)
        .addOutputLines(
            "Test.java",
            """
            class Test {
              boolean f(int a) {
                return a > 0;
              }

              boolean g(int a) {
                return !(a > 0);
              }

              boolean h(boolean a, boolean b) {
                return !(a && b);
              }

              boolean i(boolean a) {
                return a;
              }
            }
            """)
        .doTest();
  }

  @Test
  public void yieldingLiteralsFromBothBranches_yieldsTheCondition() {
    refactoringHelper
        .addInputLines(
            "Test.java",
            """
            class Test {
              boolean f(int k, boolean b) {
                return switch (k) {
                  case 0 -> {
                    if (b) {
                      yield true;
                    } else {
                      yield false;
                    }
                  }
                  default -> false;
                };
              }
            }
            """)
        .addOutputLines(
            "Test.java",
            """
            class Test {
              boolean f(int k, boolean b) {
                return switch (k) {
                  case 0 -> {
                    yield b;
                  }
                  default -> false;
                };
              }
            }
            """)
        .doTest();
  }

  @Test
  public void ifWithoutElse_isNotReported_becauseTheFinalReturnIsASeparateStatement() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            class Test {
              boolean f(boolean b) {
                if (b) {
                  return true;
                }
                return false;
              }
            }
            """)
        .doTest();
  }

  @Test
  public void branchesWithMoreThanAReturn_doMoreThanReturnTheCondition() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            class Test {
              int calls;

              boolean f(boolean b) {
                if (b) {
                  calls++;
                  return true;
                } else {
                  return false;
                }
              }

              boolean g(boolean b, boolean c) {
                if (b) {
                  return c;
                } else {
                  return false;
                }
              }
            }
            """)
        .doTest();
  }

  @Test
  public void sameLiteralInBothBranches_isLeftToDuplicateBranches() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            class Test {
              boolean f(boolean b) {
                if (b) {
                  return true;
                } else {
                  return true;
                }
              }
            }
            """)
        .doTest();
  }

  @Test
  public void boxedCondition_isNotReturnedDirectly_becauseItCanBeNull() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            class Test {
              Object f(Boolean b) {
                if (b) {
                  return true;
                } else {
                  return false;
                }
              }

              Object g(Boolean b) {
                if (!b) {
                  return false;
                } else {
                  return true;
                }
              }
            }
            """)
        .doTest();
  }

  @Test
  public void diagnostic_showsTheSimplifiedStatement() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            class Test {
              boolean f(boolean b) {
                // BUG: Diagnostic contains: can be simplified to `return b;`
                if (b) {
                  return true;
                } else {
                  return false;
                }
              }
            }
            """)
        .doTest();
  }
}
