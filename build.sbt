lazy val root = (project in file("."))
  .settings(
    name := "beer-scraper-scala",
    organization := "fr.colinleverger",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.12.1",
    libraryDependencies += "net.ruippeixotog" %% "scala-scraper"   % "1.2.0",
    libraryDependencies += "org.scalactic"    %% "scalactic"       % "3.0.1",
    libraryDependencies += "org.scalatest"    %% "scalatest"       % "3.0.1"   % "test",
    libraryDependencies += "org.json4s"       %% "json4s-jackson"  % "3.5.0",
    libraryDependencies += "org.json4s"       %% "json4s-native"   % "3.5.0"
  )