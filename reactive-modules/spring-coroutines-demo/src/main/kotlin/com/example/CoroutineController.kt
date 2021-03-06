package com.example

import com.example.persistence.ReactiveAuditRepository
import com.example.persistence.ReactiveMessageRepository
import com.example.persistence.ReactivePeopleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class CoroutineController(
        val peopleRepository: ReactivePeopleRepository,
        val messageRepository: ReactiveMessageRepository,
        val auditRepository: ReactiveAuditRepository) {

    @GetMapping("/coroutine/{personId}")
    fun getMessages(@PathVariable personId: String) = GlobalScope.mono(Dispatchers.Unconfined) {
        val person = peopleRepository.findById(personId).awaitSingle()
        val lastLogin = auditRepository.findByEmail(person.email).awaitSingle().eventDate
        val numberOfMessages = messageRepository.countByMessageDateGreaterThanAndEmail(lastLogin, person.email).awaitSingle()

        val message = "Hello ${person.name}, you have $numberOfMessages messages since $lastLogin"

        message
    }
}