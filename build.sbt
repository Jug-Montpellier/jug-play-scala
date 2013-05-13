scalaVersion := "2.10.1"

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

scalacOptions ++= Seq("-target:jvm-1.7","-language:postfixOps")

//scalacOptions += "-feature"

//resolvers += Resolver.file("Local Repository", file("/Users/olivier/projects/scala/Play20-myfork/repository/local"))(Resolver.ivyStylePatterns)

//mainDarts := Seq("sosimple.dart")

dart2js := false

dartEntryPoints += "sosimple.dart"

dartWebUIEntryPoints += "speakers.html"

dartWebUIEntryPoints += "admin/database.html"

