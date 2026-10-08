import io.github.yagipass.memberorder.MemberOrderStep
import net.ltgt.gradle.errorprone.errorprone

buildscript {
  repositories { mavenCentral() }
  dependencies { classpath(libs.spotless.member.order) }
}

plugins {
  `java-library`
  alias(libs.plugins.errorprone)
  alias(libs.plugins.maven.publish)
  alias(libs.plugins.spotless)
}

repositories { mavenCentral() }

dependencies {
  compileOnly(libs.errorprone.annotation)
  compileOnly(libs.errorprone.check.api)
  compileOnly(libs.auto.service.annotations)
  annotationProcessor(libs.auto.service)

  errorprone(libs.errorprone.core)
  errorprone(libs.nullaway)

  testImplementation(libs.errorprone.test.helpers)
  testImplementation(libs.junit)
}

configurations.testImplementation { extendsFrom(configurations.compileOnly.get()) }

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

spotless {
  java {
    addStep(MemberOrderStep.builder().order("T,SF,F,C,M:BRD,SM:BRD,M:V,SM:V").build())
    googleJavaFormat(libs.versions.google.java.format.get())
  }
}

tasks.withType<JavaCompile>().configureEach {
  options.encoding = "UTF-8"
  options.compilerArgs.addAll(listOf("-Xlint:all,-options,-processing", "-Werror"))
  options.compilerArgs.addAll(
    listOf("code", "parser", "tree", "util").flatMap {
      listOf("--add-exports", "jdk.compiler/com.sun.tools.javac.$it=ALL-UNNAMED")
    }
  )
  options.errorprone { error("Var", "UnnecessaryFinal") }
}

tasks.compileJava {
  options.errorprone {
    error("NullAway")
    option("NullAway:OnlyNullMarked", "true")
    option("NullAway:JSpecifyMode", "true")
  }
}

tasks.compileTestJava { options.errorprone { disable("NullAway") } }

tasks.test {
  useJUnit()
  jvmArgs(
    listOf("api", "file", "main", "model", "parser", "processing", "tree", "util").map {
      "--add-exports=jdk.compiler/com.sun.tools.javac.$it=ALL-UNNAMED"
    }
  )
  jvmArgs(
    listOf("code", "comp").map { "--add-opens=jdk.compiler/com.sun.tools.javac.$it=ALL-UNNAMED" }
  )
}

tasks.javadoc {
  (options as StandardJavadocDocletOptions)
    .addStringOption("-add-exports", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED")
}

mavenPublishing {
  publishToMavenCentral()
  signAllPublications()
  pom {
    name = "errorprone-tidy"
    description = "Additional Error Prone checks for tidier Java code"
    url = "https://github.com/yagipass/errorprone-tidy"
    licenses {
      license {
        name = "Apache-2.0"
        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
      }
    }
    developers {
      developer {
        id = "yagipass"
        name = "yagipass"
        url = "https://github.com/yagipass"
      }
    }
    scm {
      connection = "scm:git:https://github.com/yagipass/errorprone-tidy.git"
      developerConnection = "scm:git:ssh://git@github.com/yagipass/errorprone-tidy.git"
      url = "https://github.com/yagipass/errorprone-tidy"
    }
  }
}
