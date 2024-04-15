package pt.ulisboa.ist.pharmacist

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PharmacistApplication

fun main(args: Array<String>) {
    runApplication<PharmacistApplication>(*args)
}
