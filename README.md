# DeployApp Storage Service

The storage service is responsible for parsing and managing uploaded apps, and communicating with third-party storage mechanisms (S3, FTP, etc.)

## Development
Running `deployapp-storage-service` as part of [`deployapp-platform`](https://github.com/Transfusion/deployapp-platform#running-development) is recommended for a streamlined local development environment setup process.

## Environment
GraalVM CE 22.3.1, OpenJDK 11.0.18 is recommended.

`deployapp-storage-service` depends on [`app-info-java-graalvm`](https://github.com/Transfusion/app-info-java-graalvm), which in turn depends on [GraalVM](https://www.graalvm.org/) and the [Polyglot API](https://www.graalvm.org/22.0/reference-manual/ruby/Polyglot/).

There are many scenarios where it is useful to run `deployapp-storage-service` independently, such as when trying to expose a local instance of `deployapp-backend` through a K8s cluster via [Telepresence](https://www.telepresence.io/) and `kubectl port-forward`.

1. Download [GraalVM CE](https://www.graalvm.org/downloads/).
2. Install the [TruffleRuby runtime](https://www.graalvm.org/latest/reference-manual/ruby/InstallingGraalVM/).
    ```
    gu install ruby
    ```
3. Install the `app-info` gem based on [this compatibility matrix](https://github.com/Transfusion/app-info-java-graalvm#version-support).  
    ```
    gem install app-info -v x.x.x
    ```
    If there are errors when building protobuf-related native extensions on AArch64 Linux, please refer to [`the Dockerfile`](https://github.com/Transfusion/deployapp-storage-service/blob/main/Dockerfile).
4. Copy [`application-dev.yml`](https://github.com/Transfusion/deployapp-platform/blob/dev/deployapp-storage-service-config/application-dev.yml) to [`src/main/resources`](https://github.com/Transfusion/deployapp-storage-service/tree/main/src/main/resources) and edit it [accordingly](https://github.com/Transfusion/deployapp-platform#running-development), then run

```shell
JAVA_HOME=/path/to/graalvm/jdk/Home SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun
```

## Unit tests
```shell
JAVA_HOME=/path/to/graalvm/jdk/Home sh run_unit_tests.sh
```

## Integration tests

```shell
sh run_integration_tests.sh
```
spins up all the dependent services with `docker-compose`, then runs the tests.

Alternatively, the relevant services may be started with, for instance,
```shell
docker-compose -f docker-compose.test.yml up redis minio minio_createbucket ftp-web ftp
```
and the desired tests in the test suite run with
```shell
./gradlew :test --tests io.github.transfusion.deployapp.storagemanagementservice.external_integration.*
```