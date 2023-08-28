package me.choicore.likeapuppy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class SpringSecurityApplication

fun main(args: Array<String>) {
    runApplication<SpringSecurityApplication>(*args)
}