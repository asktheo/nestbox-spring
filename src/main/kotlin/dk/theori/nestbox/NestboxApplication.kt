package dk.theori.nestbox

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NestboxApplication

fun main(args: Array<String>) {
	runApplication<NestboxApplication>(*args)
}
