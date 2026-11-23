package io.iotex.base.example

import android.widget.Toast
import androidx.lifecycle.Observer
import io.iotex.base.R
import io.iotex.base.bindbase.BaseBindActivity
import io.iotex.base.databinding.ActivityExampleBinding

class ExampleActivity :
    BaseBindActivity<ExampleVM, ActivityExampleBinding>(R.layout.activity_example) {

    override fun initView() {
        supportFragmentManager.beginTransaction().add(R.id.frame, ExampleFragment.newInstance())
            .commit()
        mBinding.tvText.setOnClickListener {
            Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show()
        }
    }

    override fun initData() {
        showLoading()
        mViewModel.textLD.observe(this, Observer {
            mBinding.tvText.text = it
        })
    }
}