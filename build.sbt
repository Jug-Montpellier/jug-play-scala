scalaVersion := "2.10.3"

offline := true

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

scalacOptions ++= Seq("-target:jvm-1.7","-language:postfixOps")

//scalacOptions += "-feature"

//resolvers += "Central repository" at "http://repo1.maven.org/maven2/"

resolvers += Resolver.file("Local Repository", file("/Users/olivier/projects/scala/Play20/repository/local"))(Resolver.ivyStylePatterns)

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

dartDev := true

//dartNoJs := true

//dartEntryPoints += "sosimple.dart"
dartEntryPoints ++= Seq("sosimple.dart")

//dartWebUIEntryPoints += "speakers.html"

//dartWebUIEntryPoints += "admin/dbbrowser.html"

