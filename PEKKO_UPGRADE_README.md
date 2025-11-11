# Apache Pekko 1.0.3 Upgrade

## Overview

This document describes the upgrade of sunbird-utils repository from Akka 2.5.19 to Apache Pekko 1.0.3.

## Why This Upgrade

1. License Compliance: Akka changed from Apache 2.0 to Business Source License 1.1, requiring commercial licenses for production use. Apache Pekko maintains Apache 2.0 license.
2. Security: Akka 2.5.19 no longer receives security updates.
3. Modernization: Access to latest features and performance improvements.

## Technology Stack Changes

- Actor Framework: Akka 2.5.19 to Apache Pekko 1.0.3
- Scala: 2.11 to 2.13
- Jackson Module Scala: 2.10.1 to 2.14.3

## Key Changes

### Dependencies

All Maven POM files updated with new versions. Scala library exclusions added to prevent version conflicts between Scala 2.11 and 2.13.

Updated POM files:
- sunbird-platform-core/actor-core/pom.xml
- sunbird-platform-core/actor-util/pom.xml
- sunbird-platform-core/common-util/pom.xml
- sunbird-es-utils/pom.xml

### Source Code

Akka imports migrated to Pekko across all Java files:
- akka.actor to org.apache.pekko.actor
- akka.pattern to org.apache.pekko.pattern
- akka.routing to org.apache.pekko.routing
- akka.util to org.apache.pekko.util
- akka.dispatch to org.apache.pekko.dispatch

### Configuration

Configuration references updated from akka to pekko namespaces:
- akka.actor.provider to pekko.actor.provider
- akka.remote.RemoteActorRefProvider to org.apache.pekko.remote.RemoteActorRefProvider
- akka.remote.netty.tcp to pekko.remote.artery
- akka:// protocol references to pekko://

### Scala Version Handling

Added exclusions to prevent Scala 2.11 transitive dependencies:
- Excluded scala-library and scala-reflect from cloud-store-sdk in common-util
- Explicitly declared scala-library 2.13.12 dependency across all modules

## Build Instructions

Build all modules:
```
mvn clean install -DskipTests
```

Build with tests:
```
mvn clean install
```

Check dependency tree for verification:
```
mvn dependency:tree
```

## Migration Impact

Business Logic: No changes to business logic or functionality
API Compatibility: Maintained, as Pekko is API-compatible with Akka
Code Changes: Primarily package name updates from akka to pekko
License: Now compliant with Apache 2.0 throughout the stack

## Verification

After upgrade, verify:
1. Build succeeds without errors
2. No Akka dependencies remain: mvn dependency:tree | grep akka
3. Pekko dependencies present: mvn dependency:tree | grep pekko
4. All tests pass

## Known Issues

Scala 2.11/2.13 Conflict: If you encounter NoClassDefFoundError for scala.collection classes, verify dependency tree to ensure no Scala 2.11 artifacts are present. Run mvn dependency:tree and add exclusions for any scala-library or scala-reflect with version 2.11.
