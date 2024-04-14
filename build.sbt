val scala3Version = "3.4.1"
val http4sVersion = "1.0.0-M40"

lazy val root = project
  .in(file("."))
  .settings(
    name := "http",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++=
      Seq(
        "org.typelevel" %% "cats-core" % "2.10.0",
        "org.typelevel" %% "cats-effect" % "3.5.4",
        "org.scalatest" %% "scalatest" % "3.2.18" % "test",
        "com.softwaremill.sttp.client3" %% "core" % "3.9.5"
      )
  )
