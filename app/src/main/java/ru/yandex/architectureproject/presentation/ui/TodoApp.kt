package ru.yandex.architectureproject.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.yandex.architectureproject.presentation.state.TaskState
import ru.yandex.architectureproject.presentation.viewmodel.TaskViewModel

@Composable
fun TodoApp(viewModel: TaskViewModel) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is TaskState.Loading -> CircularProgressIndicator()

            is TaskState.Loaded -> {
                val tasks = (state as TaskState.Loaded).tasks
                TodoList(tasks = tasks, onAction = { action -> viewModel.reduce(action) })
            }

            is TaskState.Error -> Text(text = (state as TaskState.Error).message)
        }
    }
}