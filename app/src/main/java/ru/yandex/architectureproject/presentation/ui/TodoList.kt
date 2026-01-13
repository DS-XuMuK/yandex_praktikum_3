package ru.yandex.architectureproject.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import ru.yandex.architectureproject.data.model.Task
import ru.yandex.architectureproject.presentation.state.TaskAction

private const val LABEL_ADD = "Добавить"
private const val LABEL_DELETE = "Удалить"

@Composable
fun TodoList(tasks: List<Task>, onAction: (TaskAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val taskText = remember { mutableStateOf("") }

        Row {
            TextField(
                value = taskText.value,
                onValueChange = { taskText.value = it },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (taskText.value.isNotBlank()) {
                    onAction(TaskAction.AddTask(taskText.value))
                    taskText.value = ""
                }
            }) {
                Text(LABEL_ADD)
            }
        }

        LazyColumn {
            items(tasks) { task ->
                TodoItem(task = task, onAction = onAction)
            }
        }
    }
}

@Composable
fun TodoItem(task: Task, onAction: (TaskAction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            task.text,
            style = if (task.isDone) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle()
        )
        Row {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = { isDone ->
                    onAction(
                        TaskAction.UpdateTaskStatus(
                            taskId = task.id,
                            isDone = isDone,
                        )
                    )
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { onAction(TaskAction.DeleteTask(task.id)) }) {
                Text(LABEL_DELETE)
            }
        }
    }
}