logLevel := Level.Info

resolvers += Resolver.sbtPluginRepo("releases")
resolvers += Resolver.bintrayIvyRepo("2m", "sbt-plugins")

addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.6.0")
//addSbtPlugin("com.typesafe.sbt" % "sbt-multi-jvm" % "0.4.0")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.15.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.7.6")
addSbtPlugin("com.lightbend.akka" % "sbt-paradox-akka" % "0.35")
//addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.0.0")
//addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.9.0")
//addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.0")
addSbtPlugin("com.lightbend.sbt" % "sbt-javaagent" % "0.1.5")
addSbtPlugin("com.lightbend.akka.grpc" % "sbt-akka-grpc" % "1.0.2")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.2")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.23")
addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.6")
