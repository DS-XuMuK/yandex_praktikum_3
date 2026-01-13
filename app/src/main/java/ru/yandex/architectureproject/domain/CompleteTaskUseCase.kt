package ru.yandex.architectureproject.domain

import kotlinx.coroutines.delay
import ru.yandex.architectureproject.data.repository.TaskRepository
import kotlin.time.Duration.Companion.seconds

private val delayForDeletion = 10.seconds

class CompleteTaskUseCase(
    private val repository: TaskRepository,
) {
    suspend operator fun invoke(taskId: Int) {
        repository.completeTask(taskId)
        delay(delayForDeletion)
        repository.deleteTask(taskId)
    }
}
