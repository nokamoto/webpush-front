scalaVersion := "2.12.6"

name := "webpush-front"

version := IO.readLines(file("VERSION")).head

enablePlugins(PlayScala)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  guice,
  "com.github.nokamoto" %% "webpush-protobuf-grpc" % "0.0.0-SNAPSHOT",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "org.mockito" % "mockito-core" % "2.22.0" % Test
)
