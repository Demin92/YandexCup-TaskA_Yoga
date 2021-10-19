package ru.demin.taska_yoga

import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import java.util.*

interface Event

class EventsQueue : MutableLiveData<Queue<Event>>() {
    private val queue = LinkedList<Event>()

    @MainThread
    fun add(event: Event) {
        queue.add(event)
        value = queue
    }
}