sbtPlugin := true

version := "1.1-SNAPSHOT"

organization := "io.oseberg"

val resolver = Resolver.ssh(
  "Oseberg maven repo", 
  "dev-staging1", 
  "/var/www/mavenrepo") withPermissions ("0644")

publishTo := Some(resolver as ("maven", Path.userHome / ".ssh" / "id_rsa"))

name := "jooq-sbt-plugin"

crossScalaVersions := Seq("2.9.3", "2.10.4", "2.11.4")
