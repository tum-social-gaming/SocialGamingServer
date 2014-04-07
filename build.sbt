name := "social_games_ws"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache
)     

play.Project.playJavaSettings

// dependency for google cloud message service (push service)
libraryDependencies += "com.google.android.gcm" % "gcm-server" % "1.0.2"

// dependency for restfb
libraryDependencies += "com.restfb" % "restfb" % "1.6.14"

resolvers += "GCM Server Repository" at "https://raw.github.com/slorber/gcm-server-repository/master/releases/" 

libraryDependencies ++= Seq("de.undercouch" % "bson4jackson" % "2.1.0" force(),
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.1.0" force(),
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.1.0" force(),
  "com.fasterxml.jackson.core" % "jackson-core" % "2.1.0" force(),
  "org.mongodb" % "mongo-java-driver" % "2.11.3",
  "org.jongo" % "jongo" % "1.0",
  "uk.co.panaxiom" %% "play-jongo" % "0.6.0-jongo1.0"
)
