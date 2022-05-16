# axon-avro

![currently in development](https://img.shields.io/badge/lifecycle-INCUBATING-orange.svg)

Collection of libs and tools to work with axon framework and apache avro.

[![Build Status](https://github.com/holixon/axon-avro/workflows/Development%20branches/badge.svg)](https://github.com/holixon/axon-avro/actions)
[![sponsored](https://img.shields.io/badge/sponsoredBy-Holisticon-red.svg)](https://holisticon.de/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.holixon.avro.maven/axon-avro/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.holixon.avro.maven/axon-avro)

## goals

### verify-and-create-docs

Keep your schema files in `src/main/avro`. The path must fit the declared namespace, so

```json
{ "type":"record", "namespace":"io.foo.bar", "name": "BarEvent" }
```

must be placed in a file: `src/main/avro/io/foo/bar/BarEvent.avsc`

## Useful information

* [Mojo API specification](https://maven.apache.org/developers/mojo-api-specification.html)
