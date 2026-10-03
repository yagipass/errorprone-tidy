package io.github.yagipass.errorprone.tidy;

import com.google.errorprone.BugCheckerRefactoringTestHelper;
import com.google.errorprone.CompilationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SimplifyBooleanExpressionTest {
  private final CompilationTestHelper compilationHelper =
      CompilationTestHelper.newInstance(SimplifyBooleanExpression.class, getClass());
  private final BugCheckerRefactoringTestHelper refactoringHelper =
      BugCheckerRefactoringTestHelper.newInstance(SimplifyBooleanExpression.class, getClass());

  @Test
  public void comparisonWithLiteral_isTheOperandOrItsNegation() {
    refactoringHelper
        .addInputLines(
            "Test.java",
            """
            class Test {
              void f(boolean b) {
                boolean x1 = b == true;
                boolean x2 = b == false;
                boolean x3 = b != true;
                boolean x4 = b != false;
                boolean x5 = false == b;
              }
            }
            """)
        .addOutputLines(
            "Test.java",
            """
            class Test {
              void f(boolean b) {
                boolean x1 = b;
                boolean x2 = !b;
                boolean x3 = !b;
                boolean x4 = b;
                boolean x5 = !b;
              }
            }
            """)
        .doTest();
  }

  @Test
  public void neutralOperandOfLogicalOperator_canBeDropped() {
    refactoringHelper
        .addInputLines(
            "Test.java",
            """
            class Test {
              void f(boolean b) {
                boolean x1 = b && true;
                boolean x2 = true && b;
                boolean x3 = b || false;
                boolean x4 = false || b;
                boolean x5 = b && !false;
              }
            }
            """)
        .addOutputLines(
            "Test.java",
            """
            class Test {
              void f(boolean b) {
                boolean x1 = b;
                boolean x2 = b;
                boolean x3 = b;
                boolean x4 = b;
                boolean x5 = b;
              }
            }
            """)
        .doTest();
  }

  @Test
  public void alwaysConstantLogicalOperator_isLeftToComplexBooleanConstant() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            class Test {
              void f(boolean b) {
                boolean x1 = b || true;
                boolean x2 = b && false;
                boolean x3 = true == false;
                boolean x4 = b || !false;
              }
            }
            """)
        .doTest();
  }

  @Test
  public void negatedLiteral_isTheOppositeLiteral() {
    refactoringHelper
        .addInputLines(
            "Test.java",
            """
            class Test {
              boolean x1 = !true;
              boolean x2 = !!false;
            }
            """)
        .addOutputLines(
            "Test.java",
            """
            class Test {
              boolean x1 = false;
              boolean x2 = false;
            }
            """)
        .doTest();
  }

  @Test
  public void conditionalWithLiteralBranches_isTheConditionOrItsNegation() {
    refactoringHelper
        .addInputLines(
            "Test.java",
            """
            class Test {
              void f(int a, boolean b) {
                boolean x1 = a > 0 ? true : false;
                boolean x2 = a > 0 ? false : true;
                boolean x3 = b ? false : true;
                boolean x4 = b ? !false : false;
              }
            }
            """)
        .addOutputLines(
            "Test.java",
            """
            class Test {
              void f(int a, boolean b) {
                boolean x1 = a > 0;
                boolean x2 = !(a > 0);
                boolean x3 = !b;
                boolean x4 = b;
              }
            }
            """)
        .doTest();
  }

  @Test
  public void conditionalWithSameLiteralInBothBranches_isLeftToDuplicateBranches() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            class Test {
              boolean f(boolean b) {
                return b ? true : true;
              }
            }
            """)
        .doTest();
  }

  @Test
  public void negation_keepsOperatorPrecedence() {
    refactoringHelper
        .addInputLines(
            "Test.java",
            """
            class Test {
              void f(int a, boolean b, Object o) {
                boolean x1 = a > 0 == false;
                boolean x2 = (b || a > 0) == false;
                boolean x3 = !b == false;
                boolean x4 = o instanceof String != true;
              }
            }
            """)
        .addOutputLines(
            "Test.java",
            """
            class Test {
              void f(int a, boolean b, Object o) {
                boolean x1 = !(a > 0);
                boolean x2 = !(b || a > 0);
                boolean x3 = b;
                boolean x4 = !(o instanceof String);
              }
            }
            """)
        .doTest();
  }

  @Test
  public void boxedOperand_isNotSimplified_becauseUnboxingCanThrow() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            class Test {
              void f(Boolean b) {
                Object x1 = b == true;
                Object x2 = b && true;
                Object x3 = b ? true : false;
                Object x4 = !b == false;
              }
            }
            """)
        .doTest();
  }

  @Test
  public void diagnostic_showsTheSimplifiedExpression() {
    compilationHelper
        .addSourceLines(
            "Test.java",
            """
            class Test {
              boolean f(boolean b) {
                // BUG: Diagnostic contains: can be simplified to `!b`
                return b == false;
              }
            }
            """)
        .doTest();
  }
}
