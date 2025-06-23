ThisBuild / scalaVersion := "3.3.6"

lazy val root = (project in file("."))
  .settings(
    name := "prewave-mock-server",
    version := "0.1.0",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % "0.23.24",
      "org.http4s" %% "http4s-ember-client" % "0.23.24",
      "org.http4s" %% "http4s-dsl"          % "0.23.24",
      "org.http4s" %% "http4s-circe"        % "0.23.24",
      "io.circe"   %% "circe-core"          % "0.14.6",
      "io.circe"   %% "circe-generic"       % "0.14.6",
      "io.circe"   %% "circe-parser"        % "0.14.6",
      "org.typelevel" %% "cats-effect"      % "3.5.3"
    ),
  )
