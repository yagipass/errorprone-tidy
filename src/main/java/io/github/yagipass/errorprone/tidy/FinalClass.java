package io.github.yagipass.errorprone.tidy;

import static com.google.errorprone.BugPattern.LinkType.NONE;
import static com.google.errorprone.BugPattern.SeverityLevel.SUGGESTION;
import static com.google.errorprone.matchers.Description.NO_MATCH;
import static com.google.errorprone.matchers.Matchers.contains;
import static com.google.errorprone.util.ASTHelpers.getConstructors;
import static com.google.errorprone.util.ASTHelpers.getSymbol;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.ClassTreeMatcher;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.fixes.SuggestedFixes;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import java.util.Set;
import javax.lang.model.element.Modifier;

@AutoService(BugChecker.class)
@BugPattern(
    summary =
        "This class cannot be extended outside this file because all of its constructors are"
            + " private, so it should be declared final",
    severity = SUGGESTION,
    linkType = NONE)
public final class FinalClass extends BugChecker implements ClassTreeMatcher {
  private static final long serialVersionUID = 6001496812822206919L;

  @Override
  public Description matchClass(ClassTree tree, VisitorState state) {
    if (tree.getKind() != Tree.Kind.CLASS) {
      return NO_MATCH;
    }
    ClassSymbol symbol = getSymbol(tree);
    Set<Modifier> modifiers = symbol.getModifiers();
    if (symbol.isAnonymous()
        || modifiers.contains(Modifier.FINAL)
        || modifiers.contains(Modifier.ABSTRACT)
        || modifiers.contains(Modifier.NON_SEALED)) {
      return NO_MATCH;
    }
    if (!getConstructors(symbol).stream()
        .allMatch(c -> c.getModifiers().contains(Modifier.PRIVATE))) {
      return NO_MATCH;
    }
    if (isExtendedInCompilationUnit(symbol, state)) {
      return NO_MATCH;
    }
    return describeMatch(
        tree,
        SuggestedFixes.addModifiers(tree, state, Modifier.FINAL).orElse(SuggestedFix.emptyFix()));
  }

  private static boolean isExtendedInCompilationUnit(ClassSymbol symbol, VisitorState state) {
    return contains(
            ClassTree.class,
            (ClassTree c, VisitorState s) -> symbol.equals(getSymbol(c).getSuperclass().tsym))
        .matches(state.getPath().getCompilationUnit(), state);
  }
}
