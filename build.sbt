val CatsVersion = "2.7.0"
val CatsEffectVersion = "3.2.9"
val SkunkVersion = "0.2.2"
val NatchezVersion = "0.1.6"

ThisBuild / scalaVersion := "2.13.4"

lazy val root = (project in file("."))
  .settings(
    organization := "com.two",
    name := "skunk-flyway",
    libraryDependencies ++= Seq(
      /* Cats Effect */
      "org.typelevel" %% "cats-core" % CatsVersion,
      "org.typelevel" %% "cats-effect" % CatsEffectVersion,
      /* Skunk Postgres */
      "org.tpolecat" %% "skunk-core" % SkunkVersion,
      /* Natchez Tracing */
      "org.tpolecat" %% "natchez-jaeger" % NatchezVersion,
      /* Flyway */
      "org.flywaydb" % "flyway-core" % "7.15.0",
      "org.postgresql" % "postgresql" % "42.2.25",
      /* Testing */
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test
    ),
    addCompilerPlugin("org.typelevel" % "kind-projector_2.13.4" % "0.11.3"),
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
