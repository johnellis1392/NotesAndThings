
name := "NotesAndThings"
version := "1.0"
scalaVersion := "2.11.7"

enablePlugins(JavaServerAppPackaging)

libraryDependencies ++= Seq(
  "com.typesafe.akka"  %% "akka-actor"       % "2.4.2",
  "com.typesafe.akka"  %% "akka-slf4j"       % "2.4.2",
  "org.mongodb"        %% "casbah"           % "2.7.3",
  "io.spray"           %%  "spray-can"     % "1.3.3",
  "io.spray"           %%  "spray-routing" % "1.3.3",
  "io.spray"           %% "spray-json"       % "1.3.2",
  "org.json4s"         %% "json4s-native" % "3.2.11",
  "com.typesafe.play"  %% "play-json" % "2.4.0-M1",
  "com.typesafe.akka"  %% "akka-stream-experimental"             % "2.0.1",
  "com.typesafe.akka"  %% "akka-http-core-experimental"          % "2.0.1",
  "com.typesafe.akka"  %% "akka-http-experimental"               % "2.0.1",
  "com.typesafe.akka"  %% "akka-http-spray-json-experimental"    % "2.0.1",
  "com.typesafe.akka"  %% "akka-http-testkit-experimental"       % "2.0.1"
)

fork in run := true

herokuAppName in Compile := "fast-sands-53013"

