scalaVersion := "2.10.2"
//scalaVersion := "2.9.2"

offline := true

// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += Resolver.file("Local Repository", file("/Users/olivier/projects/scala/Play20/repository/local"))(Resolver.ivyStylePatterns)

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

//resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

//resolvers += "Orcades-LR repository" at "http://www.orcades.net/tmprepo/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2-SNAPSHOT")
//addSbtPlugin("play" % "sbt-plugin" % "2.1.1")
//addSbtPlugin("play" % "sbt-plugin" % "2.1.3-SNAPSHOT")

addSbtPlugin("net.orcades" % "sbt-dart-plugin" % "0.2.2-SNAPSHOT")

// add in user pref addSbtPlugin("net.orcades" % "sbt-slick-plugin" % "0.2-SNAPSHOT")

