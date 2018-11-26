lazy val flatland = (crossProject.crossType(CrossType.Pure))
  .settings(
    organization := "com.github.fdietze",
    name := "flatland",
    version := "master-SNAPSHOT",
    crossScalaVersions := Seq("2.10.7", "2.11.12", "2.12.7"),
    scalaVersion := crossScalaVersions.value.last,
    libraryDependencies ++= (
      "org.scalatest" %%% "scalatest" % "3.0.5" % Test ::
      Nil
    ),

  scalaJSStage in Test := FastOptStage, // not fullopt, because exceptions are removed by optimizations

  initialCommands in console := """
  import flatland._
  """,

  scalacOptions ++=
    "-encoding" :: "UTF-8" ::
    "-unchecked" ::
    "-deprecation" ::
    "-explaintypes" ::
    "-feature" ::
    "-language:_" ::
    "-Xcheckinit" ::
    "-Xfuture" ::
    "-Xlint:-unused" ::
    "-Ypartial-unification" ::
    "-Yno-adapted-args" ::
    "-Ywarn-infer-any" ::
    "-Ywarn-value-discard" ::
    "-Ywarn-nullary-override" ::
    "-Ywarn-nullary-unit" ::
    Nil,
  )
  .jvmSettings(
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided"
  )
  .jsSettings(
    scalacOptions += {
      val local = baseDirectory.value.toURI
      val remote = s"https://raw.githubusercontent.com/fdietze/flatland/${git.gitHeadCommit.value.get}/"
      s"-P:scalajs:mapSourceURI:$local->$remote"
    }
  )

lazy val jvm = flatland.jvm
lazy val js = flatland.js
