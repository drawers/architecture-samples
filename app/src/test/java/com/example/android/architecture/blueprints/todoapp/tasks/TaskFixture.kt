package com.example.android.architecture.blueprints.todoapp.tasks

import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeRepository
import com.example.android.architecture.blueprints.todoapp.tasks.TaskAction.Companion.action
import io.kotest.property.exhaustive.exhaustive

class TasksContext(
        val viewModel: TasksViewModel,
        val repo: FakeRepository
)

class TaskAction(val name: String, val body: TasksContext.() -> Unit) {

    override fun toString(): String {
        return "TaskAction(name='$name')"
    }

    companion object {
        fun action(name: String, body: TasksContext.() -> Unit): TaskAction {
            return TaskAction(name, body)
        }
    }
}

val LOAD_ALL = action("Load") {
    viewModel.setFiltering(TasksFilterType.ALL_TASKS)
    viewModel.loadTasks(forceUpdate = true)
}

val LOAD_COMPLETED = action("LoadCompleted") {
    viewModel.setFiltering(TasksFilterType.COMPLETED_TASKS)
    viewModel.loadTasks(forceUpdate = true)
}

val LOAD_ERROR = action("LoadError") {
    repo.setReturnError(true)
    viewModel.loadTasks(forceUpdate = true)
    repo.setReturnError(false)
}

val CLICK_ON_FAB = action("ClickOnFab") {
    viewModel.addNewTask()
}

val CLICK_ON_OPEN_TASK = action("ClickOnOpenTask") {
    val taskId = "42"
    viewModel.openTask(taskId)
}

val CLEAR_COMPLETED_TASKS = action("ClearCompletedTasks") {
    viewModel.clearCompletedTasks()
    viewModel.loadTasks(forceUpdate = true)
}

val SHOW_EDIT_RESULT_OK = action("ShowEditResultOk") {
    viewModel.showEditResultMessage(EDIT_RESULT_OK)
}

val SHOW_EDIT_RESULT_MESSAGES = action("ShowDeleteResultOk") {
    viewModel.showEditResultMessage(DELETE_RESULT_OK)
}

val COMPLETE_TASK = action("CompleteTask") {
    val task = Task("Title", "Description")
    repo.addTasks(task)
    viewModel.completeTask(task, true)
}

val ACTIVATE_TASK = action("ActivateTask") {
    val task = Task("Title", "Description", true)
    repo.addTasks(task)
    viewModel.completeTask(task, false)
}

val TASKS = listOf(
        LOAD_ALL,
        LOAD_COMPLETED,
        LOAD_ERROR,
        CLICK_ON_FAB,
        CLICK_ON_OPEN_TASK,
        CLEAR_COMPLETED_TASKS,
        SHOW_EDIT_RESULT_OK,
        SHOW_EDIT_RESULT_MESSAGES,
        COMPLETE_TASK,
        ACTIVATE_TASK
).exhaustive()