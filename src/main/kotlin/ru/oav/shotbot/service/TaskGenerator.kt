package ru.oav.shotbot.service

import ru.oav.shotbot.model.Task
import java.util.concurrent.ThreadLocalRandom

object TaskGenerator {


    fun generateTask(): Task {

        val firstNum = ThreadLocalRandom.current().nextInt(100)
        val secondNum = ThreadLocalRandom.current().nextInt(100)

        return Task("$firstNum+$secondNum=?", firstNum + secondNum)
    }
}
