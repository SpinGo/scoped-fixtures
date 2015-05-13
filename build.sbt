name := "scoped-fixtures"

organization := "com.spingo"

version := "1.0.0"

sbtVersion := "0.13"

scalaVersion := "2.10.4"

crossScalaVersions := Seq("2.11.6", "2.10.4")

libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "2.2.0")

// Deployment

publishMavenStyle := true

publishTo := {
  val repo = if (version.value.trim.endsWith("SNAPSHOT")) "snapshots" else "releases"
  Some(repo at s"s3://spingo-oss/repositories/$repo")
}

homepage := Some(url("https://github.com/SpinGo/scoped-fixtures"))
