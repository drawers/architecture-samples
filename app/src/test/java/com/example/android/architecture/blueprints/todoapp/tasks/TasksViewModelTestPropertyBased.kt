/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import com.example.android.architecture.blueprints.todoapp.*
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeRepository
import com.nhaarman.mockitokotlin2.mock
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.property.checkAll
import io.kotest.property.forAll
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Unit tests for the implementation of [TasksViewModel]
 */
@ExperimentalCoroutinesApi
class TasksViewModelTestPropertyBased : FunSpec() {

    // Subject under test
    private lateinit var tasksViewModel: TasksViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var tasksRepository: FakeRepository

    private lateinit var dataLoadingObserver: Observer<Boolean>

    private lateinit var itemsObserver: Observer<List<Task>>

    private lateinit var currentFilteringLabelObserver: Observer<Int>

    private lateinit var taskAddViewVisibleObserver: Observer<Boolean>

    init {
        listener(CoroutinesTestListener())
        listener(ArchTaskTestListener())
        listener(SetupTestListener())

        test("add button visible only on filter all tasks") {
            forAll(TASKS) { taskAction ->
                run(taskAction)

                isAddButtonVisible() == isFilteringLabelAll()
            }
        }

        test("no active tasks if filtering label is completed") {
            forAll(TASKS) { taskAction ->
                run(taskAction)

                !(hasActiveTask() && isFilteringLabelComplete())
            }
        }

        test("no completed tasks if filtering label is active") {
            forAll(TASKS) { taskAction ->
                run(taskAction)

                !(hasCompletedTask() && isFilteringLabelActive())
            }
        }

        test("data loading indicator turns on then off") {
            checkAll(TASKS) { taskAction ->
                run(taskAction)

                dataLoadingObserver.observed()
                        .take(2)
                        .shouldContainInOrder(
                                true,  // loading
                                false  // loaded, with error or without error
                        )
            }
        }
    }

    private fun isAddButtonVisible(): Boolean {
        return taskAddViewVisibleObserver.lastValue()
    }

    private fun run(taskAction: TaskAction) {
        taskAction.body.invoke(TasksContext(tasksViewModel, tasksRepository))
    }

    private fun hasCompletedTask() = itemsObserver.observed().last().any { it.isCompleted }

    private fun isFilteringLabelActive() = currentFilteringLabelObserver.lastValue() == R.string.label_active

    private fun isFilteringLabelComplete() = currentFilteringLabelObserver.lastValue() == R.string.label_completed

    private fun isFilteringLabelAll(): Boolean = currentFilteringLabelObserver.lastValue() == R.string.label_all

    private fun hasActiveTask() = itemsObserver.observed().last().any { it.isActive }

    private inner class SetupTestListener : TestListener {
        override suspend fun beforeEach(testCase: TestCase) {
            super.beforeEach(testCase)
            tasksRepository = FakeRepository()
            val task1 = Task("Title1", "Description1")
            val task2 = Task("Title2", "Description2", true)
            val task3 = Task("Title3", "Description3", true)
            tasksRepository.addTasks(task1, task2, task3)

            tasksViewModel = TasksViewModel(tasksRepository, SavedStateHandle())

            dataLoadingObserver = mock()
            itemsObserver = mock()
            currentFilteringLabelObserver = mock()
            taskAddViewVisibleObserver = mock()

            tasksViewModel.dataLoading.observeForever(dataLoadingObserver)
            tasksViewModel.items.observeForever((itemsObserver))
            tasksViewModel.currentFilteringLabel.observeForever(currentFilteringLabelObserver)
            tasksViewModel.tasksAddViewVisible.observeForever(taskAddViewVisibleObserver)
        }

        override suspend fun afterEach(testCase: TestCase, result: TestResult) {
            tasksViewModel.dataLoading.removeObserver(dataLoadingObserver)
            tasksViewModel.items.removeObserver(itemsObserver)
            tasksViewModel.currentFilteringLabel.removeObserver(currentFilteringLabelObserver)
            tasksViewModel.tasksAddViewVisible.observeForever(taskAddViewVisibleObserver)

            super.afterEach(testCase, result)
        }
    }
}
