package ru.yandex.architectureproject.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.yandex.architectureproject.domain.AddTaskUseCase
import ru.yandex.architectureproject.domain.CompleteTaskUseCase
import ru.yandex.architectureproject.domain.DeleteTaskUseCase
import ru.yandex.architectureproject.domain.GetAllTasksUseCase
import ru.yandex.architectureproject.domain.IncompleteTaskUseCase
import ru.yandex.architectureproject.presentation.state.TaskAction
import ru.yandex.architectureproject.presentation.state.TaskState

private const val ERROR_MESSAGE = "Ошибка загрузки"

class TaskViewModel(
    private val addTaskUseCase: AddTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val incompleteTaskUseCase: IncompleteTaskUseCase,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val _state = MutableStateFlow<TaskState>(TaskState.Loading)
    val state: StateFlow<TaskState> = _state.asStateFlow()

    private val taskForDeletionJobMap = mutableMapOf<Int, Job>()

    init {
        reduce(TaskAction.LoadTasks)
    }

    fun reduce(action: TaskAction) {
        viewModelScope.launch {
            when (action) {
                is TaskAction.LoadTasks -> {}
                is TaskAction.AddTask -> addTaskUseCase(action.task)
                is TaskAction.UpdateTaskStatus -> {
                    if (action.isDone) {
                        taskForDeletionJobMap[action.taskId] = this.coroutineContext.job
                        completeTaskUseCase(action.taskId)
                    } else {
                        taskForDeletionJobMap[action.taskId]?.cancel()
                        incompleteTaskUseCase(action.taskId)
                    }
                }
                is TaskAction.DeleteTask -> deleteTaskUseCase(action.taskId)
            }
            loadTasks()
        }
    }

    private suspend fun loadTasks() {
        withContext(ioDispatcher) {
            getAllTasksUseCase()
                .distinctUntilChanged()
                .onStart { _state.value = TaskState.Loading }
                .catch { e -> _state.value = TaskState.Error(e.message ?: ERROR_MESSAGE) }
                .collect { tasks -> _state.value = TaskState.Loaded(tasks) }
        }
    }
}
