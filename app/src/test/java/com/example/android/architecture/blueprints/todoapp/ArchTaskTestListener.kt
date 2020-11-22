package com.example.android.architecture.blueprints.todoapp

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec

class ArchTaskTestListener : TestListener {
    override suspend fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) {
                runnable.run()
            }

            override fun postToMainThread(runnable: Runnable) {
                runnable.run()
            }

            override fun isMainThread(): Boolean {
                return true
            }
        })
    }

    override suspend fun afterSpec(spec: Spec) {
        ArchTaskExecutor.getInstance().setDelegate(null)
        super.afterSpec(spec)
    }
}