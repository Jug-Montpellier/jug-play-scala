scalaVersion := "2.10.2"

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

scalacOptions ++= Seq("-target:jvm-1.7","-language:postfixOps")

//scalacOptions += "-feature"

resolvers += "Central repository" at "http://repo1.maven.org/maven2/"

dartDev := true

dartNoJs := true

//dartEntryPoints += "sosimple.dart"
dartEntryPoints ++= Seq("sosimple.dart")

dartWebUIEntryPoints += "speakers.html"

dartWebUIEntryPoints += "admin/dbbrowser.html"

