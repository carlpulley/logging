name := "logging"

organization := "Cake Solutions Limited"

version := "1.0"

scalaVersion := "2.11.3"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.4"

libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.3.4"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.3.4" % "test"

libraryDependencies += "com.typesafe" % "config" % "1.2.1"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

libraryDependencies += "org.scalatest"  %% "scalatest" % "2.2.2" % "test"
