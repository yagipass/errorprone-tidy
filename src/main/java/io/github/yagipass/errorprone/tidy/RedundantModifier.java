package io.github.yagipass.errorprone.tidy;

import static com.google.errorprone.BugPattern.LinkType.NONE;
import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;
import static com.google.errorprone.matchers.Description.NO_MATCH;
import static com.google.errorprone.util.ASTHelpers.getSymbol;
import static com.google.errorprone.util.ASTHelpers.hasAnnotation;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.ClassTreeMatcher;
import com.google.errorprone.bugpatterns.BugChecker.MethodTreeMatcher;
import com.google.errorprone.bugpatterns.BugChecker.VariableTreeMatcher;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.fixes.SuggestedFixes;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.util.ErrorProneToken;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;

@AutoService(BugChecker.class)
@BugPattern(
    summary = "This modifier is implied by the declaration's context and can be removed",
    severity = WARNING,
    linkType = NONE)
public final class RedundantModifier extends BugChecker
    implements ClassTreeMatcher, MethodTreeMatcher, VariableTreeMatcher {
  @Override
  public Description matchClass(ClassTree tree, VisitorState state) {
    ClassSymbol symbol = getSymbol(tree);
    Set<Modifier> implied = EnumSet.noneOf(Modifier.class);
    if (symbol.isInterface()) {
      implied.add(Modifier.ABSTRACT);
    }
    if (tree.getKind() == Tree.Kind.RECORD) {
      implied.add(Modifier.FINAL);
    }
    if (symbol.getNestingKind() == NestingKind.MEMBER) {
      if (tree.getKind() != Tree.Kind.CLASS) {
        implied.add(Modifier.STATIC);
      }
      if (symbol.owner.isInterface()) {
        implied.add(Modifier.PUBLIC);
        implied.add(Modifier.STATIC);
      }
    }
    return report(tree, tree.getModifiers(), implied, state);
  }

  @Override
  public Description matchMethod(MethodTree tree, VisitorState state) {
    MethodSymbol symbol = getSymbol(tree);
    ClassSymbol owner = symbol.enclClass();
    Set<Modifier> implied = EnumSet.noneOf(Modifier.class);
    if (owner.isInterface()) {
      implied.add(Modifier.PUBLIC);
      implied.add(Modifier.ABSTRACT);
    } else if ((owner.getModifiers().contains(Modifier.FINAL) || owner.isAnonymous())
        && !hasAnnotation(symbol, "java.lang.SafeVarargs", state)) {
      implied.add(Modifier.FINAL);
    }
    return report(tree, tree.getModifiers(), implied, state);
  }

  @Override
  public Description matchVariable(VariableTree tree, VisitorState state) {
    VarSymbol symbol = getSymbol(tree);
    if (symbol.getKind() != ElementKind.FIELD || !symbol.owner.isInterface()) {
      return NO_MATCH;
    }
    return report(
        tree,
        tree.getModifiers(),
        EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL),
        state);
  }

  private Description report(
      Tree tree, ModifiersTree modifiers, Set<Modifier> implied, VisitorState state) {
    Set<Modifier> redundant = EnumSet.copyOf(implied);
    redundant.retainAll(modifiers.getFlags());
    if (redundant.isEmpty()) {
      return NO_MATCH;
    }
    redundant.retainAll(writtenModifiers(modifiers, state));
    if (redundant.isEmpty()) {
      return NO_MATCH;
    }
    String names = redundant.stream().map(Modifier::toString).collect(Collectors.joining(" "));
    return buildDescription(tree)
        .setMessage(String.format("`%s` is implied here and can be removed", names))
        .addFix(
            SuggestedFixes.removeModifiers(modifiers, state, redundant)
                .orElse(SuggestedFix.emptyFix()))
        .build();
  }

  private static Set<Modifier> writtenModifiers(ModifiersTree modifiers, VisitorState state) {
    Set<Modifier> written = EnumSet.noneOf(Modifier.class);
    if (state.getSourceForNode(modifiers) == null) {
      return written;
    }
    for (ErrorProneToken token : state.getOffsetTokensForNode(modifiers)) {
      switch (token.kind()) {
        case PUBLIC -> written.add(Modifier.PUBLIC);
        case ABSTRACT -> written.add(Modifier.ABSTRACT);
        case STATIC -> written.add(Modifier.STATIC);
        case FINAL -> written.add(Modifier.FINAL);
        default -> {}
      }
    }
    return written;
  }
}
