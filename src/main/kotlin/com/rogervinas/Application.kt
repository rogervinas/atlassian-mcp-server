package com.rogervinas

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class Application


fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
