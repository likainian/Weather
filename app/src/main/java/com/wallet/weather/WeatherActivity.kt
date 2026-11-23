package com.wallet.weather

import com.wallet.weather.bean.City
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.IntentCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.wallet.weather.databinding.ActivityWeatherBinding
import com.wallet.weather.item.WeatherPagerAdapter
import com.wallet.weather.utils.LocationUtils
import com.wallet.weather.utils.TimeUtils
import io.iotex.base.bindbase.BaseBindActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.jvm.java
import com.wallet.weather.state.WeatherState

class WeatherActivity :
    BaseBindActivity<WeatherVM, ActivityWeatherBinding>(R.layout.activity_weather) {

    private var restoringState = false
    private val adapter by lazy { WeatherPagerAdapter(this) }
    private var syncing = false
    private var locatingJob: Job? = null
    private val permission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) locate() else mViewModel.finishLocation(null)
    }

    private val citySearch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data = result.data ?: return@registerForActivityResult
        if (result.resultCode != RESULT_OK) return@registerForActivityResult
        val city = IntentCompat.getSerializableExtra(data, CitySearchActivity.EXTRA_CITY, City::class.java)
            ?: return@registerForActivityResult
        mViewModel.add(city) {
            Toast.makeText(this, R.string.weather_save_city_failed, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        restoringState = savedInstanceState != null
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        adapter.show(mViewModel.state.value.pages)
        mBinding.weatherPager.adapter = adapter
        mBinding.weatherRefresh.setColorSchemeResources(R.color.weather_accent)
        mBinding.weatherRefresh.setProgressBackgroundColorSchemeResource(R.color.weather_primary)
    }

    override fun initEvent() {
        mBinding.addCity.setOnClickListener {
            citySearch.launch(Intent(this, CitySearchActivity::class.java))
        }

        mBinding.weatherRefresh.setOnRefreshListener {
            val state = mViewModel.state.value
            state.pages.getOrNull(state.selected)?.let { mViewModel.refresh(it.city) }
            if (state.failed) mViewModel.start()
        }
        mBinding.weatherPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (!syncing) mViewModel.select(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    mBinding.weatherPager.post { syncPager(mViewModel.state.value) }
                }
            }
        })
    }

    override fun initData() {
        lifecycleScope.launch { mViewModel.state.collect(::render) }
        mViewModel.start()

        when {
            !mViewModel.state.value.locating -> Unit
            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED -> locate()
            !restoringState -> permission.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    private fun locate() {
        if (locatingJob?.isActive == true) return
        locatingJob = lifecycleScope.launch {
            mViewModel.finishLocation(LocationUtils.currentCity(applicationContext))
        }
    }

    private fun render(state: WeatherState) {
        val page = state.pages.getOrNull(state.selected)
        mBinding.cityTitle.text = page?.city?.name ?: getString(R.string.weather_launcher_title)
        val timestamp = page?.cache?.updatedAt?.let(TimeUtils::formatUpdateTime)
        mBinding.weatherStatus.text = when {
            state.failed -> getString(R.string.weather_status_cities_failed)
            page?.loading == true -> getString(R.string.weather_status_updating)
            page?.failed == true && timestamp != null ->
                getString(R.string.weather_status_cached_update_failed, timestamp)
            page?.failed == true -> getString(R.string.weather_status_load_failed)
            timestamp != null -> getString(R.string.weather_status_updated_at, timestamp)
            state.locating && page == null -> getString(R.string.weather_status_reading_cache_and_locating)
            else -> getString(R.string.weather_status_reading_cache)
        }
        mBinding.weatherRefresh.isRefreshing = page?.loading == true || (page == null && !state.failed)
        mBinding.addCity.isEnabled = page != null
        mBinding.cityDots.setPosition(state.pages.size, state.selected)
        syncPager(state)
    }

    private fun syncPager(state: WeatherState) {
        val pager = mBinding.weatherPager
        if (pager.scrollState != ViewPager2.SCROLL_STATE_IDLE) return
        syncing = true
        adapter.show(state.pages)
        if (state.selected in state.pages.indices && pager.currentItem != state.selected) {
            pager.setCurrentItem(state.selected, false)
        }
        syncing = false
    }
}
