Montpellier JUG web site / play 2.1.1 / scala 2.10.1

This project needs local install (publish-local) of:
  * sbt-dart-plugin https://github.com/cheleb/sbt-dart-plugin
  * securesocial https://github.com/jaliss/securesocial



$> export DART_HOME=/path/to/dart_sdk
$> cd jug-play-scala
$> cd public
#Install dart dependencies.
$> $DART_HOME/bin/pub install 
$> cd -
$> sbt
$> run

 

