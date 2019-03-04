TID-x 19: Avoiding the Tower of Babel
=====================================

This repository keeps code samples and the demo for my talk at
[TIDx 2019](tidx). See a PDF version of the slides in the `slides` dir.

[tidx]: https://www.tid-x.com/

Requirements
------------

You should have docker, avro-tools and kafka tools but not the servers
installed. For that just:

```bash
$ docker-compose up -d
```

Avro schema samples
-------------------

Go to the `schemas` dir and play with the provided `Makefile`.

Schema evolution demo
---------------------

This is a multi-module SBT project with three modules: `producer`,
`productStats` and `recommender`.

You can play with each project this way:

```bash
$ sbt
> project <project name>
name> run
... does its things until you press ENTER ...
name> regen
name> run
```

Use the command `regen` if you change the schema to get the generated code
refreshed. For the case of going from v0 to v1 of the product visit schema you
will need to uncomment some snippets in the services.

Enjoy

