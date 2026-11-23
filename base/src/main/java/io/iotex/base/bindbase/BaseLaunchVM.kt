package io.iotex.base.bindbase

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.LogUtils
import io.iotex.base.BaseViewModel
import io.iotex.base.ILifecycle
import kotlinx.coroutines.*

open class BaseLaunchVM(application: Application) : BaseViewModel(application), ILifecycle,
    CoroutineScope by CoroutineScope(Dispatchers.IO) {

    private var singJobMap = HashMap<String, Job>()
    private var noCancelMap = HashMap<String, Job>()
    private var launchJobs = ArrayList<Job>()

    override fun onDestroy(owner: LifecycleOwner) {
        launchJobs.forEach {
            runCatching {
                if (!it.isCancelled) it.cancel()
            }
        }
        super.onDestroy(owner)
    }

    fun cancelAllJob() {
        launchJobs.iterator().forEach {
            runCatching {
                if (!it.isCancelled) it.cancel()
            }
        }
        singJobMap.forEach {
            if (!it.value.isCancelled) it.value.cancel()
        }
    }

    fun addLaunchNoCancel(
        tag: String,
        showLoading: Boolean = false,
        onError: ((Throwable) -> Unit)? = null,
        block: suspend CoroutineScope.() -> Unit
    ) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            handFail(exception, onError)
            noCancelMap.remove(tag)
        }
        if (noCancelMap[tag] != null) return
        val job = launch(SupervisorJob() + exceptionHandler) {
            runCatching {
                if (showLoading) iView?.showLoading()
                block.invoke(this)
            }.onFailure {
                handFail(it, onError)
            }
            if (showLoading) iView?.hideLoading()
        }.also { job ->
            job.invokeOnCompletion { noCancelMap.remove(tag) }
        }
        noCancelMap[tag] = job
    }

    fun addLaunch(
        showLoading: Boolean = false,
        onError: ((Throwable) -> Unit)? = null,
        block: suspend CoroutineScope.() -> Unit
    ) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            handFail(exception, onError)
        }
        val job = launch(SupervisorJob() + exceptionHandler) {
            runCatching {
                if (showLoading) iView?.showLoading()
                block.invoke(this)
                if (showLoading) iView?.hideLoading()
            }.onFailure {
                handFail(it, onError)
            }
        }
        launchJobs.add(job)
    }

    fun addLaunchSingle(
        tag: String,
        showLoading: Boolean = false,
        onError: ((Throwable) -> Unit)? = null,
        block: suspend CoroutineScope.() -> Unit
    ) {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            handFail(exception, onError)
        }
        val job = singJobMap[tag]
        if (job?.isCancelled == false) job.cancel()
        val singJob = launch(SupervisorJob() + exceptionHandler) {
            runCatching {
                if (showLoading) iView?.showLoading()
                block.invoke(this)
            }.onFailure {
                handFail(it, onError)
            }
            if (showLoading) iView?.hideLoading()
        }
        singJobMap[tag] = singJob
    }

    private fun handFail(
        exception: Throwable,
        onError: ((Throwable) -> Unit)? = null,
    ) {
        exception.printStackTrace()
        iView?.hideLoading()
        onError?.invoke(exception)
        LogUtils.i("onError", exception.message)
    }
}