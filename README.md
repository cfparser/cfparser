cfparser
========
[![Build Status](https://travis-ci.org/cfparser/cfparser.svg?branch=master)](https://travis-ci.org/cfparser/cfparser)

CFParser 2.2.10 is available on Maven Central!

The CFParser license is BSD (http://www.opensource.org/licenses/bsd-license.html).

To build use:
mvn clean install


```xml
<dependency>
    <groupId>com.github.cfparser</groupId>
    <artifactId>cfparser</artifactId>
    <version>2.2.10</version>
    <type>pom</type>
</dependency>
<dependency>
    <groupId>com.github.cfparser</groupId>
    <artifactId>cfml.dictionary</artifactId>
    <version>2.2.10</version>
</dependency>
<dependency>
    <groupId>com.github.cfparser</groupId>
    <artifactId>cfml.parsing</artifactId>
    <version>2.2.10</version>
</dependency>
```

To update the version number prior to a build, run:
`mvn versions:set -DnewVersion=2.2.13-SNAPSHOT` 
