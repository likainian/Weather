package io.iotex.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner


open class BaseViewModel(application: Application) : AndroidViewModel(application), ILifecycle {

    protected var iView: IView? = null
    protected var owner: LifecycleOwner? = null
    override fun onCreate(owner: LifecycleOwner) {
        this.owner = owner
        if (owner is IView) {
            this.iView = owner
        }
    }

    override fun onResume(owner: LifecycleOwner) {}
    override fun onStart(owner: LifecycleOwner) {}
    override fun onStop(owner: LifecycleOwner) {}
    override fun onDestroy(owner: LifecycleOwner) {}
    override fun onLifecycleChanged(owner: LifecycleOwner, event: Lifecycle.Event) {}
}