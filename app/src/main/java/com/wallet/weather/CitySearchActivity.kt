package com.wallet.weather

import android.content.Intent
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.wallet.weather.bean.City
import com.wallet.weather.databinding.ActivityCitySearchBinding
import com.wallet.weather.item.CitySearchAdapter
import io.iotex.base.bindbase.BaseBindToolbarActivity
import kotlinx.coroutines.launch
import com.wallet.weather.state.CityState

/**
 * 城市搜索页面
 */
class CitySearchActivity :
    BaseBindToolbarActivity<CitySearchVM, ActivityCitySearchBinding>(R.layout.activity_city_search) {

    private val adapter = CitySearchAdapter(::selectCity)

    override fun initView() {
        mBinding.cityResults.layoutManager = LinearLayoutManager(this)
        mBinding.cityResults.adapter = adapter
    }

    override fun initEvent() {
        setToolbarTitle(getString(R.string.weather_search_city))
        mBinding.citySearchSubmit.setOnClickListener { search() }
        mBinding.cityQuery.setOnEditorActionListener { _, action, _ ->
            if (action != EditorInfo.IME_ACTION_SEARCH) return@setOnEditorActionListener false
            search()
            true
        }
    }

    override fun initData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.result.collect(::render)
            }
        }
    }

    private fun search() {
        val query = mBinding.cityQuery.text.toString().trim()
        if (query.length < 2) {
            mBinding.cityQuery.error = getString(R.string.weather_city_query_too_short)
            return
        }
        mBinding.cityQuery.error = null
        mViewModel.search(query)
    }

    private fun render(result: CityState) {
        val cities = (result as? CityState.Found)?.cities.orEmpty()
        adapter.submitList(cities)
        mBinding.cityResults.isVisible = cities.isNotEmpty()
        val message = when (result) {
            CityState.Idle -> null
            CityState.Loading -> R.string.weather_city_search_loading
            CityState.Failed -> R.string.weather_city_search_failed
            is CityState.Found -> if (cities.isEmpty()) R.string.weather_city_search_empty else null
        }
        mBinding.citySearchStatus.isVisible = message != null
        message?.let { mBinding.citySearchStatus.setText(it) }
    }

    private fun selectCity(city: City) {
        setResult(RESULT_OK, Intent().putExtra(EXTRA_CITY, city))
        finish()
    }

    companion object {
        const val EXTRA_CITY = "weather.selected_city"
    }
}
