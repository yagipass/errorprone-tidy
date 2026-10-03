package io.github.yagipass.errorprone.tidy;

import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;
import static com.google.errorprone.matchers.Description.NO_MATCH;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.CompilationUnitTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.CompilationUnitTree;
import java.util.regex.Pattern;

@AutoService(BugChecker.class)
@BugPattern(
    summary = "Package names should use only lowercase letters and digits",
    severity = WARNING,
    linkType = CUSTOM,
    link = "https://google.github.io/styleguide/javaguide.html#s5.2.1-package-names")
public final class PackageNaming extends BugChecker implements CompilationUnitTreeMatcher {
  private static final Pattern PACKAGE_NAME =
      Pattern.compile("[a-z][a-z0-9]*(?:\\.[a-z][a-z0-9]*)*");

  @Override
  public Description matchCompilationUnit(CompilationUnitTree tree, VisitorState state) {
    if (tree.getPackage() == null || isSuppressed(tree.getPackage(), state)) {
      return NO_MATCH;
    }
    String name = tree.getPackageName().toString();
    if (PACKAGE_NAME.matcher(name).matches()) {
      return NO_MATCH;
    }
    return buildDescription(tree.getPackageName())
        .setMessage(
            String.format(
                "Package name `%s` should use only lowercase letters and digits, with no"
                    + " underscores",
                name))
        .build();
  }
}
