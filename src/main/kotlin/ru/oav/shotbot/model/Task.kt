package ru.oav.shotbot.model

import java.util.concurrent.ThreadLocalRandom

data class Task(val task: String, val answer: Int){

    companion object{
        fun generateTask(): Task{
            val firstNum = ThreadLocalRandom.current().nextInt(100)
            val secondNum = ThreadLocalRandom.current().nextInt(100)

            return Task("$firstNum+$secondNum=?", firstNum + secondNum)
        }
    }

}
