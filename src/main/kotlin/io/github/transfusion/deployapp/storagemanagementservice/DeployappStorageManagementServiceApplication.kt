package io.github.transfusion.deployapp.storagemanagementservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("io.github.transfusion.deployapp")
//https://stackoverflow.com/questions/29272203/autowired-spring-beans-across-java-packages
class DeployappStorageManagementServiceApplication

fun main(args: Array<String>) {
    runApplication<DeployappStorageManagementServiceApplication>(*args)
}
