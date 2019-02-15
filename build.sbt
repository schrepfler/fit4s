import sbt.Keys.crossScalaVersions
// *****************************************************************************
// Projects
// *****************************************************************************

lazy val fit4s =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .enablePlugins(JavaAppPackaging)
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
        library.caseApp,
        library.scalaCheck % Test,
        library.scalaTest  % Test
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val scalaCheck = "1.14.0"
      val scalaTest  = "3.0.5"
      val caseApp    = "2.0.0-M3"
    }
    val scalaCheck = "org.scalacheck"             %% "scalacheck" % Version.scalaCheck
    val scalaTest  = "org.scalatest"              %% "scalatest"  % Version.scalaTest
    val caseApp    = "com.github.alexarchambault" %% "case-app"   % Version.caseApp
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
commonSettings ++
scalafmtSettings

lazy val commonSettings =
  Seq(
    // scalaVersion from .travis.yml via sbt-travisci
    // scalaVersion := "2.12.8",
    organization := "io.kimeru",
    organizationName := "Kimeru",
    maintainer := "Srdan Srepfler",
    version := "0.1",
    crossScalaVersions := Seq("2.11.11", "2.12.8"),
    startYear := Some(2019),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding",
      "UTF-8",
      "-Ypartial-unification",
      "-Ywarn-unused-import"
    ),
    Compile / unmanagedSourceDirectories := Seq((Compile / scalaSource).value),
    Test / unmanagedSourceDirectories := Seq((Test / scalaSource).value),
    Compile / compile / wartremoverWarnings ++= Warts.unsafe
  )

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true
  )
