import com.typesafe.sbt.packager.docker.DockerChmodType

name := """Library"""

dockerBaseImage := "adoptopenjdk/openjdk11"
dockerChmodType := DockerChmodType.UserGroupWriteExecute

dockerEnvVars := Map(
  "SQL_AUTO_MIGRATE" -> "true",
  "SECRET_KEY" -> "ASDFJASDFAJSDFJASDJFSJDFJSDFJASJDFJASDJFJSDFJASFDJXCVfsadjfaskdfjasldfasdFASDF",
  "DB_URL" -> "jdbc:mysql://localhost:3306/library?useUnicode=true&characterEncoding=UTF-8",
  "DB_USER" -> "library",
  "DB_USER_PASSWORD" -> "test12",
)

maintainer := "asanjarbek@gmail.com"

version := "2.7.x"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.0"

libraryDependencies += guice
libraryDependencies += "com.typesafe.play" %% "play-slick" % "4.0.2"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "4.0.2"

libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.16"
libraryDependencies += "com.h2database" % "h2" % "1.4.199"

libraryDependencies += specs2 % Test

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-Xfatal-warnings"
)
