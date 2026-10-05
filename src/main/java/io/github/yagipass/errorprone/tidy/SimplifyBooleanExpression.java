package io.github.yagipass.errorprone.tidy;

import static com.google.errorprone.BugPattern.LinkType.NONE;
import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;
import static com.google.errorprone.matchers.Description.NO_MATCH;
import static io.github.yagipass.errorprone.tidy.BooleanExpressions.isPrimitiveBoolean;
import static io.github.yagipass.errorprone.tidy.BooleanExpressions.negate;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.BinaryTreeMatcher;
import com.google.errorprone.bugpatterns.BugChecker.ConditionalExpressionTreeMatcher;
import com.google.errorprone.bugpatterns.BugChecker.UnaryTreeMatcher;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.UnaryTree;
import org.jspecify.annotations.Nullable;

@AutoService(BugChecker.class)
@BugPattern(
    summary = "This boolean expression can be simplified",
    severity = WARNING,
    linkType = NONE)
public final class SimplifyBooleanExpression extends BugChecker
    implements BinaryTreeMatcher, UnaryTreeMatcher, ConditionalExpressionTreeMatcher {
  private static final long serialVersionUID = -5974696324324712927L;

  @Override
  public Description matchBinary(BinaryTree tree, VisitorState state) {
    Boolean left = literalValue(tree.getLeftOperand());
    Boolean right = literalValue(tree.getRightOperand());
    if ((left == null) == (right == null)) {
      return NO_MATCH;
    }
    boolean literal = left != null ? left : right;
    ExpressionTree other = left != null ? tree.getRightOperand() : tree.getLeftOperand();
    if (!isPrimitiveBoolean(other)) {
      return NO_MATCH;
    }
    Boolean keepOther =
        switch (tree.getKind()) {
          case EQUAL_TO -> literal;
          case NOT_EQUAL_TO -> !literal;
          case CONDITIONAL_AND -> literal ? Boolean.TRUE : null;
          case CONDITIONAL_OR -> literal ? null : Boolean.TRUE;
          default -> null;
        };
    if (keepOther == null) {
      return NO_MATCH;
    }
    String replacement = keepOther ? state.getSourceForNode(other) : negate(other, state);
    if (replacement == null) {
      return NO_MATCH;
    }
    return simplify(tree, replacement);
  }

  @Override
  public Description matchUnary(UnaryTree tree, VisitorState state) {
    Boolean value = literalValue(tree);
    if (value == null) {
      return NO_MATCH;
    }
    Tree parent = state.getPath().getParentPath().getLeaf();
    if (parent instanceof BinaryTree
        || parent instanceof ConditionalExpressionTree
        || parent instanceof UnaryTree) {
      return NO_MATCH;
    }
    return simplify(tree, value.toString());
  }

  @Override
  public Description matchConditionalExpression(
      ConditionalExpressionTree tree, VisitorState state) {
    Boolean whenTrue = literalValue(tree.getTrueExpression());
    Boolean whenFalse = literalValue(tree.getFalseExpression());
    if (whenTrue == null || whenFalse == null || whenTrue.equals(whenFalse)) {
      return NO_MATCH;
    }
    ExpressionTree condition = tree.getCondition();
    if (!isPrimitiveBoolean(condition)) {
      return NO_MATCH;
    }
    String replacement = whenTrue ? state.getSourceForNode(condition) : negate(condition, state);
    if (replacement == null) {
      return NO_MATCH;
    }
    return simplify(tree, replacement);
  }

  private Description simplify(Tree tree, String replacement) {
    return buildDescription(tree)
        .setMessage(String.format("This expression can be simplified to `%s`", replacement))
        .addFix(SuggestedFix.replace(tree, replacement))
        .build();
  }

  private static @Nullable Boolean literalValue(ExpressionTree tree) {
    if (tree instanceof LiteralTree literal && literal.getValue() instanceof Boolean value) {
      return value;
    }
    if (tree instanceof UnaryTree unary && unary.getKind() == Tree.Kind.LOGICAL_COMPLEMENT) {
      Boolean operand = literalValue(unary.getExpression());
      return operand == null ? null : !operand;
    }
    return null;
  }
}
