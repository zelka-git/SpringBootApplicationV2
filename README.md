[![Java CI](https://github.com/zelka-git/SpringBootApplicationV2/actions/workflows/learn-github-actions.yml/badge.svg)](https://github.com/zelka-git/SpringBootApplicationV2/actions/workflows/learn-github-actions.yml)

| __JaCoCo Test Coverage__ | [![coverage](https://raw.githubusercontent.com/zelka-git/SpringBootApplicationV2/badges/jacoco.svg)](https://github.com/zelka-git/SpringBootApplicationV2/actions/workflows/build.yml) [![branch coverage](https://raw.githubusercontent.com/zelka-git/SpringBootApplicationV2/badges/branches.svg)](https://github.com/cicirello/JavaPermutationTools/actions/workflows/build.yml) |


# SpringBootApplicationV2

SpringBoot,
gradle,
liquibase,
testcontainers,
contract test

liquibase - hibernate plugin to generate changelog

### Test with embedded postgresql:
*(src/test/java/com/example/demo/repository/ItemRepositoryTest.java)*

used zonky Embedded Database : https://github.com/zonkyio/embedded-database-spring-test



./gradlew build -x contractTest -x test
