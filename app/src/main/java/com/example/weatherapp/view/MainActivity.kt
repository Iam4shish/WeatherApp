package com.example.weatherapp.view



import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide

import com.example.weatherapp.R
import com.example.weatherapp.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var GET : SharedPreferences
    private lateinit var SET : SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        GET = getSharedPreferences(packageName, MODE_PRIVATE)
        SET = GET.edit()

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        var cname = GET.getString("cityName", "Jamshedpur")
        var edt_city_name = findViewById<EditText>(R.id.edt_city_name)
        edt_city_name.setText(cname)
        viewModel.refreshData(cname!!)

        getLiveData()

        val swipe_refresh_layout = findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        swipe_refresh_layout.setOnRefreshListener {
            val ll_data_view = findViewById<LinearLayout>(R.id.ll_data_view)
            ll_data_view.visibility = View.GONE
            val pb_loading = findViewById<ProgressBar>(R.id.pb_loading)
            pb_loading.visibility = View.GONE

            val tv_error = findViewById<TextView>(R.id.tv_error)
            tv_error.visibility = View.GONE

            var cityName = GET.getString("cityName", cname)
            edt_city_name.setText(cityName)
            viewModel.refreshData(cityName!!)
            swipe_refresh_layout.isRefreshing = false
        }

        var img_search_city_name = findViewById<ImageView>(R.id.image_search_city_name)
        img_search_city_name.setOnClickListener{

            val cityName = edt_city_name.text.toString()
            SET.putString("cityName", cityName)
            SET.apply()
            viewModel.refreshData(cityName)


                getLiveData()


        }

    }

    private fun getLiveData() {
        viewModel.weather_data.observe(this, Observer{data ->
            data?.let{
                val ll_data_view = findViewById<LinearLayout>(R.id.ll_data_view)
                ll_data_view.visibility = View.VISIBLE

                val pb_loading = findViewById<ProgressBar>(R.id.pb_loading)
                pb_loading.visibility = View.GONE

                val tv_degree = findViewById<TextView>(R.id.tv_degree)
                tv_degree.text = data.main.temp.toString() + "°C"
                val tv_country_code = findViewById<TextView>(R.id.tv_country_code)
                tv_country_code.text = data.sys.country.toString()
                val tv_city_name = findViewById<TextView>(R.id.tv_city_name)
                tv_city_name.text = data.name.toString()
                val tv_humidity = findViewById<TextView>(R.id.tv_humidity)
                tv_humidity.text = data.main.humidity.toString() + "%"
                val tv_speed = findViewById<TextView>(R.id.tv_speed)
                tv_speed.text = data.wind.speed.toString() + " km/h"
                val tv_lat = findViewById<TextView>(R.id.tv_lat)
                tv_lat.text = data.coord.lat.toString()+"°"
                val tv_lon = findViewById<TextView>(R.id.tv_lon)
                tv_lon.text = data.coord.lon.toString()+"°"

                Glide.with(this).load("https://openweathermap.org/img/wn/" + data.weather.get(0).icon + "@2x.png")
                    .into(findViewById(R.id.img_weather_icon))


            }
        })

        viewModel.weather_load.observe(this, Observer { load ->
            load?.let{

                if(it){
                    val pb_loading = findViewById<ProgressBar>(R.id.pb_loading)
                    pb_loading.visibility = View.VISIBLE
                    val tv_error = findViewById<TextView>(R.id.tv_error)
                    tv_error.visibility = View.GONE
                    val ll_data_view = findViewById<LinearLayout>(R.id.ll_data_view)
                    ll_data_view.visibility = View.GONE
                }else{
                    val pb_loading = findViewById<ProgressBar>(R.id.pb_loading)
                    pb_loading.visibility = View.GONE
                }

            }
        })

        viewModel.weather_error.observe(this, Observer { error ->
            error?.let{
                if(it) {
                    val tv_error = findViewById<TextView>(R.id.tv_error)
                    tv_error.visibility = View.VISIBLE
                    val ll_data_view = findViewById<LinearLayout>(R.id.ll_data_view)
                    ll_data_view.visibility = View.GONE
                    val pb_loading = findViewById<ProgressBar>(R.id.pb_loading)
                    pb_loading.visibility = View.GONE
                }
                else{
                    val tv_error = findViewById<TextView>(R.id.tv_error)
                    tv_error.visibility = View.GONE
                }
            }
        })
    }
}