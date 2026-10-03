package io.github.yagipass.errorprone.tidy;

import static com.google.errorprone.util.ASTHelpers.getType;
import static com.google.errorprone.util.ASTHelpers.requiresParentheses;

import com.google.errorprone.VisitorState;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.UnaryTree;
import com.sun.tools.javac.code.Type;
import javax.lang.model.type.TypeKind;

final class BooleanExpressions {
  private BooleanExpressions() {}

  static boolean isPrimitiveBoolean(ExpressionTree tree) {
    if (tree instanceof UnaryTree unary && unary.getKind() == Tree.Kind.LOGICAL_COMPLEMENT) {
      return isPrimitiveBoolean(unary.getExpression());
    }
    Type type = getType(tree);
    return type != null && type.getKind() == TypeKind.BOOLEAN;
  }

  static String negate(ExpressionTree tree, VisitorState state) {
    if (tree instanceof UnaryTree unary && unary.getKind() == Tree.Kind.LOGICAL_COMPLEMENT) {
      return state.getSourceForNode(unary.getExpression());
    }
    String source = state.getSourceForNode(tree);
    return requiresParentheses(tree, state) ? "!(" + source + ")" : "!" + source;
  }
}
