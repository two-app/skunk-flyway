lazy val root = (project in file("."))
  .settings(
    organization := "com.two",
    name := "skunk-flyway",
    scalaVersion := "2.13.8",
    libraryDependencies ++= Seq(
      /* Cats Effect */
      "org.typelevel" %% "cats-core" % "2.8.0",
      "org.typelevel" %% "cats-effect" % "3.3.14",
      /* Skunk Postgres */
      "org.tpolecat" %% "skunk-core" % "0.3.1",
      /* Natchez Tracing */
      "org.tpolecat" %% "natchez-jaeger" % "0.1.6",
      /* Flyway */
      "org.flywaydb" % "flyway-core" % "9.1.0",
      "org.postgresql" % "postgresql" % "42.4.0",
      /* Testing */
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test
    ),
    addCompilerPlugin("org.typelevel" % "kind-projector_2.13.4" % "0.13.2"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    scalacOptions += "-Ymacro-annotations"
  )

Global / cancelable := false
testFrameworks += new TestFramework("munit.Framework")

/** Scalafix Configuration */
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

/** GitHub Maven Packages Deployment + Resolver Configuration */
ThisBuild / githubOwner := "two-app"
ThisBuild / githubRepository := "skunk-flyway"
ThisBuild / resolvers += Resolver.githubPackages("OWNER")
ThisBuild / githubTokenSource := TokenSource.Environment("GITHUB_TOKEN")
