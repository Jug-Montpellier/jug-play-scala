//scalaVersion := "2.10.1"
scalaVersion := "2.9.2"

// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Orcades-LR repository" at "http://www.orcades.net/tmprepo/"

// Use the Play sbt plugin for Play projects
//addSbtPlugin("play" % "sbt-plugin" % "2.2-SNAPSHOT")
addSbtPlugin("play" % "sbt-plugin" % "2.1.0")

addSbtPlugin("net.orcades" % "sbt-dart-plugin" % "0.2.2-SNAPSHOT")

