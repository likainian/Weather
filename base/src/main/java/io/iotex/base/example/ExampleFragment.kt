package io.iotex.base.example

import android.os.Bundle
import io.iotex.base.BaseViewModel
import io.iotex.base.R
import io.iotex.base.bindbase.BaseBindFragment
import io.iotex.base.databinding.FragExampleBinding

class ExampleFragment : BaseBindFragment<BaseViewModel, FragExampleBinding>(R.layout.frag_example) {
    companion object {
        fun newInstance(): ExampleFragment {
            val args = Bundle()

            val fragment = ExampleFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initView() {
        /**
         * @see ExampleActivity
         */
    }

    override fun initData() {
        /**
         * @see ExampleActivity
         */
    }
}