import Dependencies._

name := "scala-projects"

ThisBuild / version := "1.0.0-SNAPSHOT"

ThisBuild / scalaVersion := versionScala213

ThisBuild / scalafmtOnCompile := true

lazy val root = (project in file("."))
  .aggregate(
    `gateway-app`,
    `gateway-grpc`,
    `auth-server-app`,
    `auth-server-grpc`,
    `message-app`,
    `message-grpc`,
    `scala-common`)
  .settings(Commons.basicSettings)

lazy val `gateway-app` =
  _project("gateway-app")
    .dependsOn(`gateway-grpc`, `scala-common`)
    .settings(Publishing.noPublish: _*)
    .settings(libraryDependencies ++= Seq() ++ _akkaClusters)

lazy val `gateway-grpc` = (project in file("gateway-grpc")).settings(Commons.basicSettings)

lazy val `auth-server-app` =
  _project("auth-server-app")
    .dependsOn(`auth-server-grpc`, `scala-common`)
    .settings(Publishing.noPublish: _*)
    .settings(libraryDependencies ++= Seq(
      "com.helloscala" %% "akka-security-oauth-core" % "0.1",
      "com.helloscala" %% "akka-security-oauth-jose" % "0.1"
    ) ++ _akkaClusters)

lazy val `auth-server-grpc` = project in file("auth-server-grpc")

lazy val `message-app` =
  _project("message-app").dependsOn(`message-grpc`, `scala-common`).settings(Publishing.noPublish: _*)

lazy val `message-grpc` = (project in file("message-grpc")).settings(Commons.basicSettings)

lazy val `scala-common` =
  _project("scala-common").settings(
    libraryDependencies ++= Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value) ++ _akkas ++ _akkaHttps ++ _logs)

def _project(name: String, path: String = null) =
  Project(name, file(if (path == null || path.isEmpty) name else path))
    .settings(Commons.basicSettings)
    .settings(libraryDependencies ++= Seq(_akkaTypedTestkit % Test, _akkaStreamTestkit % Test, _scalatest % Test))