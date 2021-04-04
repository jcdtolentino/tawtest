package com.example.tawktest

import android.app.ActionBar
import android.app.ActionBar.DISPLAY_SHOW_CUSTOM
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import com.example.tawktest.Api.APIManager
import com.example.tawktest.DataModels.UserModel
import com.example.tawktest.Database.LocalDb


class MainActivity : AppCompatActivity() {

    val apiManager = APIManager()

    companion object {
        lateinit var appDb: LocalDb
        var mainActivity: MainActivity? = null
    }

    interface CountriesActivityListener {
        fun initializeCustomActionBar()
    }

    var userList = MutableLiveData<List<UserModel>>()

    var mWifi: NetworkInfo? = null
    var isConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appDb = Room.databaseBuilder(this, LocalDb::class.java, "local_db").fallbackToDestructiveMigration().build()
        val connManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

        isConnected = mWifi!!.isConnected

    }
    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }


     fun initializeCustomActionBar() {
        val actionBar: android.app.ActionBar? = actionBar
        actionBar?.let {
            it.displayOptions = DISPLAY_SHOW_CUSTOM
            it.setCustomView(R.layout.custom_action_bar)
            it.show()
        }
    }



}
