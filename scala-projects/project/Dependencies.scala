import sbt._

object Dependencies {
  val versionScala213 = "2.13.4"
  val versionSpringBoot = "2.3.5.RELEASE"
  val versionScalatest = "3.1.4"
  val versionAkka = "2.6.10"
  val versionAlpakka = "2.0.2"
  val versionAkkaHttp = "10.2.1"
  val versionCassandra = "4.9.0"
  val versionScalaLogging = "3.9.2"
  val versionLogback = "1.2.3"
  val versionAlpnAgent = "2.0.10"
  val versionAkkaManagement = "1.0.9"
  val versionAkkaSecurity = "0.1"
  val versionScalaCollectionCompat = "2.3.1"
  val versionScalaJava8Compat = "0.9.1"
  val versionGrpc = "1.32.1"

  val _scalatest = "org.scalatest" %% "scalatest" % versionScalatest
  val _scalaCollectionCompat = "org.scala-lang.modules" %% "scala-collection-compat" % versionScalaCollectionCompat
  val _scalaJava8Compat = "org.scala-lang.modules" %% "scala-java8-compat" % versionScalaJava8Compat

  val _akkaActorTyped = "com.typesafe.akka" %% "akka-actor-typed" % versionAkka
  val _akkaStreamTyped = "com.typesafe.akka" %% "akka-stream-typed" % versionAkka
  val _akkaDiscovery = "com.typesafe.akka" %% "akka-discovery" % versionAkka
  val _akkaSerializationJackson = "com.typesafe.akka" %% "akka-serialization-jackson" % versionAkka
  val _akkaProtobufV3 = "com.typesafe.akka" %% "akka-protobuf-v3" % versionAkka
  val _akkaPersistenceTyped = "com.typesafe.akka" %% "akka-persistence-typed" % versionAkka
  val _akkaTypedTestkit = "com.typesafe.akka" %% "akka-actor-testkit-typed" % versionAkka
  val _akkaStreamTestkit = "com.typesafe.akka" %% "akka-stream-testkit" % versionAkka

  val _akkas = _scalaJava8Compat +: Seq(
      "com.typesafe.akka" %% "akka-slf4j" % versionAkka,
      _akkaDiscovery,
      _akkaActorTyped,
      _akkaStreamTyped).map(_.exclude("org.scala-lang.modules", "scala-java8-compat").cross(CrossVersion.binary))

  val _akkaClusterShardingTyped = "com.typesafe.akka" %% "akka-cluster-sharding-typed" % versionAkka

  val _akkaClusters = Seq("com.typesafe.akka" %% "akka-cluster-typed" % versionAkka, _akkaClusterShardingTyped)

  val _alpnAgent = "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % versionAlpnAgent

  val _akkaHttp = "com.typesafe.akka" %% "akka-http" % versionAkkaHttp

  val _akkaHttps = _alpnAgent +: Seq(
      "com.typesafe.akka" %% "akka-http-spray-json" % versionAkkaHttp,
      "com.typesafe.akka" %% "akka-http2-support" % versionAkkaHttp,
      "com.typesafe.akka" %% "akka-http-core" % versionAkkaHttp,
      _akkaHttp).map(
      _.exclude("com.typesafe.akka", "akka-stream")
        .exclude("com.typesafe.akka", "akka-http-spary-json")
        .cross(CrossVersion.binary))

  val _akkaDiscoveryConsul = "com.lightbend.akka.discovery" %% "akka-discovery-consul" % versionAkkaManagement
  val _akkaManagementClusterBootstrap = "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % versionAkkaManagement
  val _akkaManagementClusterHttp = "com.lightbend.akka.management" %% "akka-management-cluster-http" % versionAkkaManagement

  val _alpakkaCsv =
    ("com.lightbend.akka" %% "akka-stream-alpakka-csv" % versionAlpakka)
      .excludeAll(ExclusionRule("com.typesafe.akka"))
      .cross(CrossVersion.binary)

  val _alpakkaFile =
    ("com.lightbend.akka" %% "akka-stream-alpakka-file" % versionAlpakka)
      .excludeAll(ExclusionRule("com.typesafe.akka"))
      .cross(CrossVersion.binary)

  val _alpakkaFtp =
    ("com.lightbend.akka" %% "akka-stream-alpakka-ftp" % versionAlpakka)
      .excludeAll(ExclusionRule("com.typesafe.akka"))
      .cross(CrossVersion.binary)

  val _alpakkaSpringWeb =
    ("com.lightbend.akka" %% "akka-stream-alpakka-spring-web" % versionAlpakka)
      .excludeAll(ExclusionRule("com.typesafe.akka"))
      .cross(CrossVersion.binary)
      .excludeAll(ExclusionRule("org.springframework"))

  val akkaSecurityOauthCore = "com.helloscala" %% "akka-security-oauth-core" % versionAkkaSecurity
  val akkaSecurityOauthJose = "com.helloscala" %% "akka-security-oauth-jose" % versionAkkaSecurity

  val _cassandras = Seq("com.datastax.oss" % "java-driver-core" % versionCassandra)

  val _javaUuidGenerator = "com.fasterxml.uuid" % "java-uuid-generator" % "4.0.1"

  val _logs = Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % versionScalaLogging,
    "ch.qos.logback" % "logback-classic" % versionLogback)

  val _springs = Seq("org.springframework.boot" % "spring-boot-starter-web" % versionSpringBoot)

  val _guava = "com.google.guava" % "guava" % "30.0-jre"

  val _consulClient = "com.orbitz.consul" % "consul-client" % "1.4.2"

  val _grpcs = Seq("io.grpc" % "grpc-core" % versionGrpc)
}
