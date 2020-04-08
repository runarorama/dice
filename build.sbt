scalaVersion := "2.12.11"

name := "dice"
organization := "com.higher-order"
version := "0.1"

resolvers += Resolver.githubPackages("runarorama")

libraryDependencies += "org.typelevel" %% "spire" % "0.16.0"
libraryDependencies += "com.higher-order" %% "MSet" % "0.1"

githubOwner := "runarorama"
githubRepository := "scala-mset"

githubTokenSource := TokenSource.GitConfig("github.token")

