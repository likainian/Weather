package io.iotex.base.example

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import io.iotex.base.BaseViewModel

class ExampleVM(application: Application) : BaseViewModel(application) {

    val textLD = MutableLiveData<String>()

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

        getData()
    }

    fun getData() {
        textLD.postValue("新数据")
    }
}