name := "scala-netty-telnet-example"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "io.netty" % "netty-all" % "4.1.6.Final",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "ch.qos.logback" % "logback-classic" % "1.1.7"
)
