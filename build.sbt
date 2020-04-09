scalaVersion := "2.12.11"

name := "dice"
organization := "com.higher-order"
version := "0.2.3"

resolvers += Resolver.githubPackages("runarorama")

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)

libraryDependencies += "com.higher-order" %% "MSet" % "0.4.0"
libraryDependencies += "org.typelevel" %% "spire" % "0.16.0"
libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"

githubOwner := "runarorama"
githubRepository := "scala-mset"

githubTokenSource := TokenSource.GitConfig("github.token")

