scalaVersion := "2.13.1"

name := "dice"
organization := "com.higher-order"
version := "0.3.0"

resolvers += Resolver.githubPackages("runarorama")

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)

libraryDependencies += "com.higher-order" %% "MSet" % "0.7.0"
libraryDependencies += "org.typelevel" %% "spire" % "0.17.0-M1"
libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"

githubOwner := "runarorama"
githubRepository := "scala-mset"

githubTokenSource := TokenSource.GitConfig("github.token")

