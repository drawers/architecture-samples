package com.example.android.architecture.blueprints.todoapp

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@ExperimentalCoroutinesApi
class CoroutinesTestListener(
        private val testCoroutineDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : TestListener {

    override suspend fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    override suspend fun afterSpec(spec: Spec) {
        Dispatchers.resetMain()
        super.afterSpec(spec)
    }
}