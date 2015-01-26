This is an SBT plugin that provides an interface to the JOOQ code generation tool
(<http://www.jooq.org>). The plugin is compatible with SBT 0.11.3+ and Scala 2.9.1+.

The current version of the plugin is *1.1*

Quick Start
======

1. Add jooq-sbt-plugin to your `project/plugins.sbt`:
        
        resolvers += "Oseberg Repo" at "http://dev-staging1.oseberg.io/mavenrepo"
        addSbtPlugin("io.oseberg" % "jooq-sbt-plugin" % "1.1-SNAPSHOT")
    
2. In your `build.sbt`, do the following:

   * Inject the plugin settings into your build definition:
   
            jooqSettings
      
     This will also add the JOOQ libraries to your application's compile
   `libraryDependencies`.
      
   * Add your database driver to your list of `libraryDependencies` with "jooq" scope:
   
            libraryDependencies += "org.postgresql" % "postgresql" % "9.3-1102-jdbc41" % "jooq"
      
   * Configure options for your environment via embedded xml, for example:
   
            jooqConfig := Some(
              <configuration>
                <jdbc>
                  <driver>org.postgresql.Driver</driver>
                  <url>jdbc:postgresql://localhost:5434/oseberg_vault</url>
                  <user>jooq</user>
                  <password>jooq</password>
                </jdbc>
                <generator>
                  <database>
                    <customTypes>
                      <customType>
                        <name>DateTime</name>
                        <type>org.joda.time.DateTime</type>
                        <converter>io.oseberg.service.JooqDateConverter</converter>
                      </customType>
                    </customTypes>
                    <forcedTypes>
                      <forcedType>
                        <name>DateTime</name>
                        <expression>.*_date*.</expression>
                      </forcedType>
                    </forcedTypes>
                    <name>org.jooq.util.postgres.PostgresDatabase</name>
                    <schemata>
                      <schema><inputSchema>dataload_tx_rrc_p4</inputSchema></schema>
                      <schema><inputSchema>dataload_tx_rrc_p5</inputSchema></schema>
                    </schemata>
                    <excludes>
                        dataload_tx_rrc_p4\.operator_names_from_numbers*
                      | dataload_tx_rrc_p4\.lease_pointer*
                    </excludes>
                  </database>
                  <target>
                    <packageName>io.oseberg.persistence.generated</packageName>
                    <directory>{((sourceManaged in Compile).value / "java").getAbsolutePath}</directory>
                  </target>
                </generator>
              </configuration>)
     
Settings
=====

The plugin exposes several settings:

* *jooq-config* (`jooqConfig` in build.sbt): an `Option[scala.xml.Elem]`
  containing configuration properties for the JOOQ code generator. These will 
  be transformed into paths into the XML configuration file.  See the full xsd
  definition for details: http://www.jooq.org/xsd/jooq-codegen-3.5.0.xsd.

* *jooq-config-file* (`jooqConifgFile` in build.sbt): an `Option[File]` that 
  allows you to supply a specific JOOQ XML configuration file to the plugin. 
  Set it like this:

            jooqConfigFile := Some(new java.io.File("/path/to/your/file")

   This will override the `jooq-options` setting if present. Either a config
   file or jooqConfig must be defined in build.sbt.

* *jooq-version* (`jooqVersion` in build.sbt): a `String` indicating the version
  of JOOQ to use. The JOOQ libraries at this version will also be imported into your
  project's compile scope. The default value is *3.5.0*, but the plugin is known
  to work with the 2.x series of JOOQ as well (e.g. 2.6.1).

* *jooq-log-level* (`jooqLogLevel` in build.sbt): a `String` controlling the
  the verbosity of code generator's logging. It defaults to "info", which 
  still produces a fair amount of output. Setting it to "error" will effectively
  silence it, except in the case of problems. Other values include "warn" and "debug".

Tasks
=====

And provides a single task:

* *jooq:codegen*: Runs the code generator.

The plugin also attaches to the `compile:compile` task (by way of 
`compile-source-generators`) and will run prior to compile if doesn't see any
`*.java` files in the directory indicated by `jooq-output-directory` (e.g. if
you run `clean`).

TODO
=====
* Clean credentials handling
* Setup maven and pom file for publishing to Github
* Fix issue with managed/unmanaged dependencies so that the destination
  of generated files can be in the unmanaged sources directories and still
  allow for continuous compilation.

History
=====
* 1.1 Initial Oseberg release
* FORK from [sean8223's repo](https://github.com/sean8223/jooq-sbt-plugin)
