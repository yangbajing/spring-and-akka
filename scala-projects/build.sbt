import Dependencies._

name := "scala-projects"

ThisBuild / version := "1.0.0-SNAPSHOT"

ThisBuild / scalaVersion := versionScala213

ThisBuild / scalafmtOnCompile := true

ThisBuild / resolvers += Resolver.bintrayRepo("helloscala", "maven")

lazy val root = (project in file("."))
  .aggregate(
    `gateway-app`,
    `gateway-grpc`,
    `auth-server-app`,
    `auth-server-grpc`,
    `message-app`,
    `message-grpc`,
    `scala-common`,
    `fusion-grpc`,
    `fusion-consul`,
    `fusion-cloud`)
  .settings(Commons.basicSettings: _*)
  .settings(name := "scala-projects")

lazy val `gateway-app` =
  _project("gateway-app")
    .dependsOn(`gateway-grpc`, `scala-common`)
    .settings(Publishing.noPublish: _*)
    .settings(libraryDependencies ++= Seq(_akkaDiscoveryConsul) ++ _akkaClusters)

lazy val `gateway-grpc` = (project in file("gateway-grpc")).settings(Commons.basicSettings: _*)

lazy val `auth-server-app` =
  _project("auth-server-app")
    .dependsOn(`auth-server-grpc`, `scala-common`, `fusion-consul`, `fusion-grpc`)
    .settings(Publishing.noPublish: _*)
    .settings(
      libraryDependencies ++= Seq(
          akkaSecurityOauthJose,
          akkaSecurityOauthCore,
          _slickPg,
          _postgresql,
          _akkaDiscoveryConsul,
          _akkaManagementClusterBootstrap,
          _akkaManagementClusterHttp) ++ _slicks ++ _akkaClusters)

lazy val `auth-server-grpc` = (project in file("auth-server-grpc"))
  .enablePlugins(AkkaGrpcPlugin)
  .settings(Commons.basicSettings: _*)
  .settings(akkaGrpcCodeGeneratorSettings += "server_power_apis")

lazy val `message-app` =
  _project("message-app").dependsOn(`message-grpc`, `scala-common`).settings(Publishing.noPublish: _*)

lazy val `message-grpc` = (project in file("message-grpc")).settings(Commons.basicSettings: _*)

lazy val `scala-common` =
  _project("scala-common")
    .dependsOn(`fusion-cloud`)
    .settings(libraryDependencies ++= Seq(_helloscalaCommon, _logback) ++ _akkaHttps)

lazy val `fusion-consul` = _project("fusion-consul")
  .dependsOn(`fusion-cloud`)
  .settings(organization := "com.helloscala.fusion", libraryDependencies ++= Seq(_consulClient))

lazy val `fusion-grpc` = _project("fusion-grpc")
  .dependsOn(`fusion-cloud`)
  .settings(Commons.basicSettings: _*)
  .settings(
    Compile / unmanagedResourceDirectories += sourceDirectory.value / "protobuf",
    organization := "com.helloscala.fusion",
    libraryDependencies ++= Seq(_akkaHttp) ++ _grpcs)

lazy val `fusion-cloud` = _project("fusion-cloud").settings(
  organization := "com.helloscala.fusion",
  libraryDependencies ++= Seq(
      _akkaClusterShardingTyped,
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      _helloscalaCommon) ++ _akkas)

def _project(name: String, path: String = null) =
  Project(name, file(if (path == null || path.isEmpty) name else path))
    .disablePlugins(AkkaGrpcPlugin)
    .settings(Commons.basicSettings: _*)
    .settings(libraryDependencies ++= Seq(_akkaTypedTestkit % Test, _akkaStreamTestkit % Test, _scalatest % Test))
