cfparser
========
[![Build Status](https://travis-ci.org/cfparser/cfparser.svg?branch=master)](https://travis-ci.org/cfparser/cfparser)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/b25fc5beacea4d4f9c493971fcfb7e90)](https://www.codacy.com/app/ryaneberly/cfparser?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=cfparser/cfparser&amp;utm_campaign=Badge_Grade)

CFParser is available on Maven Central!

The CFParser license is BSD (http://www.opensource.org/licenses/bsd-license.html).

To build use:
mvn clean install


```xml
<dependency>
    <groupId>com.github.cfparser</groupId>
    <artifactId>cfml.parsing</artifactId>
    <version>2.5.6</version>
</dependency>
```

To update the version number prior to a build, run:
`mvn versions:set -DnewVersion=2.2.13-SNAPSHOT` 

To update the version number prior to a release, run:
`mvn versions:set -DnewVersion=2.2.13` 

#Release example
(we do our final commit for 2.2.13-SNAPSHOT on the develop branch, we're ready to release it)
```
[develop]$`git checkout master`
 [master]$`git merge --no-ff develop`
 [master]$`mvn versions:set -DnewVersion=2.2.13`
 [master]$`mvn clean verify`
 [master]$`git commit -am 'Release version 2.2.13'`
 [master]$`git tag -a 2.2.13`
 [master]$`git checkout develop`
[develop]$`git merge --no-ff master`
[develop]$`mvn versions:set -DnewVersion=2.2.14-SNAPSHOT`
[develop]$`git commit -am 'Setup version 2.2.14 for development'`
```
