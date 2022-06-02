# axon-avro

Template repository for usage in organizations: toolisticon, holunda-io, holixon...

[![incubating](https://img.shields.io/badge/lifecycle-INCUBATING-orange.svg)](https://github.com/holisticon#open-source-lifecycle)
[![Build Status](https://github.com/holixon/axon-avro/workflows/Development%20branches/badge.svg)](https://github.com/holixon/axon-avro/actions)
[![sponsored](https://img.shields.io/badge/sponsoredBy-Holisticon-RED.svg)](https://holisticon.de/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.holixon.avro.maven/axon-avro-generator-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.holixon.avro.maven/axon-avro-generator-maven-plugin)


## goals

### verify-and-create-docs

Keep your schema files in `src/main/avro`. The path must fit the declared namespace, so

```json
{ "type":"record", "namespace":"io.foo.bar", "name": "BarEvent" }
```

must be placed in a file: `src/main/avro/io/foo/bar/BarEvent.avsc`

## Useful information

* [Mojo API specification](https://maven.apache.org/developers/mojo-api-specification.html)
