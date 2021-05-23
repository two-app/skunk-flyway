val CatsVersion = "2.6.1"
val CatsEffectVersion = "2.5.1"
val SkunkVersion = "0.0.24"
val NatchezVersion = "0.0.25"
val MUnitVersion = "0.7.22"

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
      "org.flywaydb" % "flyway-core" % "7.3.2",
      "org.postgresql" % "postgresql" % "42.2.20",
      /* Testing */
      "org.scalameta" %% "munit" % MUnitVersion % Test
    ),
    addCompilerPlugin("org.typelevel" % "kind-projector_2.13.4" % "0.11.2"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    scalacOptions += "-Ymacro-annotations"
  )

Global / cancelable := false
testFrameworks += new TestFramework("munit.Framework")

/** Scalafix Configuration */
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

/** GitHub Maven Packages Deployment + Resolver Configuration */
ThisBuild / githubOwner := "two-app"
ThisBuild / githubRepository := "db-usr"
ThisBuild / resolvers += Resolver.githubPackages("OWNER")
ThisBuild / githubTokenSource := TokenSource.Environment("GITHUB_TOKEN")
