name := "logging"

organization := "Cake Solutions Limited"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4-M2"

libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.4-M2"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.4-M2" % "test"

libraryDependencies += "com.typesafe" % "config" % "1.3.0"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"

libraryDependencies += "org.scalatest"  %% "scalatest" % "3.0.0-M14" % "test"

fork in test := false
