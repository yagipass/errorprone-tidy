package io.github.yagipass.errorprone.tidy;

import static com.google.errorprone.BugPattern.LinkType.NONE;
import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;
import static com.google.errorprone.matchers.Description.NO_MATCH;
import static com.google.errorprone.util.ASTHelpers.stripParentheses;
import static io.github.yagipass.errorprone.tidy.BooleanExpressions.isPrimitiveBoolean;
import static io.github.yagipass.errorprone.tidy.BooleanExpressions.negate;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.IfTreeMatcher;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.YieldTree;
import org.jspecify.annotations.Nullable;

@AutoService(BugChecker.class)
@BugPattern(
    summary =
        "Return the condition directly instead of returning boolean literals from each branch",
    severity = WARNING,
    linkType = NONE)
public final class SimplifyBooleanReturn extends BugChecker implements IfTreeMatcher {
  private record BooleanExit(String keyword, boolean value) {}

  private static final long serialVersionUID = 5674167023609375752L;

  @Override
  public Description matchIf(IfTree tree, VisitorState state) {
    if (tree.getElseStatement() == null) {
      return NO_MATCH;
    }
    BooleanExit whenTrue = booleanExit(tree.getThenStatement());
    BooleanExit whenFalse = booleanExit(tree.getElseStatement());
    if (whenTrue == null || whenFalse == null || whenTrue.value() == whenFalse.value()) {
      return NO_MATCH;
    }
    ExpressionTree condition = stripParentheses(tree.getCondition());
    if (!isPrimitiveBoolean(condition)) {
      return NO_MATCH;
    }
    String value = whenTrue.value() ? state.getSourceForNode(condition) : negate(condition, state);
    if (value == null) {
      return NO_MATCH;
    }
    String replacement = whenTrue.keyword() + " " + value + ";";
    return buildDescription(tree)
        .setMessage(String.format("This if statement can be simplified to `%s`", replacement))
        .addFix(SuggestedFix.replace(tree, replacement))
        .build();
  }

  private static @Nullable BooleanExit booleanExit(StatementTree statement) {
    if (statement instanceof BlockTree block && block.getStatements().size() == 1) {
      return booleanExit(block.getStatements().get(0));
    }
    if (statement instanceof ReturnTree returnTree) {
      Boolean value = literalValue(returnTree.getExpression());
      return value == null ? null : new BooleanExit("return", value);
    }
    if (statement instanceof YieldTree yieldTree) {
      Boolean value = literalValue(yieldTree.getValue());
      return value == null ? null : new BooleanExit("yield", value);
    }
    return null;
  }

  private static @Nullable Boolean literalValue(@Nullable ExpressionTree tree) {
    return (tree instanceof LiteralTree literal && literal.getValue() instanceof Boolean value)
        ? value
        : null;
  }
}
