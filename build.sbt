name := "scoped-fixtures"

organization := "com.spingo"

version := "2.0.0"

scalaVersion := "2.13.1"

crossScalaVersions := Seq("2.11.8", "2.12.1", "2.13.1")

libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "3.0.8")

licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

pomExtra := {
  <scm>
    <url>https://github.com/SpinGo/scoped-fixtures</url>
    <connection>scm:git:git@github.com:SpinGo/scoped-fixtures.git</connection>
  </scm>
  <developers>
    <developer>
      <id>timcharper</id>
      <name>Tim Harper</name>
      <url>http://timcharper.com</url>
    </developer>
  </developers>
}

// Deployment

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

homepage := Some(url("https://github.com/SpinGo/scoped-fixtures"))
