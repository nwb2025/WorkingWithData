package com.example.androiddata.data

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.androiddata.LOG_TAG
import com.example.androiddata.WEB_SERVICE_URL
import com.example.androiddata.utilities.FileHelper
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MonsterRepo(val app:Application) {

    // to not always get data through calling the method we use LiveData
    val monsterData = MutableLiveData<List<Monster>>()
    private val monsterDao = MonsterDB.getMonsterDB(app).monsterDao()

    init {
        Log.i(LOG_TAG,"Network available: ${networkAvailable()}")
        CoroutineScope(Dispatchers.IO).launch {
            val data = monsterDao.getAll()
            if (data.isEmpty()) {
                callWebService()
            } else {
                monsterData.postValue(data)
                withContext(Dispatchers.Main) {
                    Toast.makeText(app, "monsterLogging: using db data ", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Very useful piece  of code
        // first we try read data from local storage and if we dont have it we retrieve data from web server
        /* val data = readDataFromCache()
        if (data.isEmpty()){
            refreshDataFromWeb()
        }else{
            monsterData.value = data
            Log.i(LOG_TAG, "monsterLogging: using local data ")
        } */

    }

    @WorkerThread // indicator that this func will be called in a background thread
    suspend fun callWebService(){
        if (networkAvailable()){
            Log.i(LOG_TAG, "Calling web service")
            val retrofit = Retrofit.Builder()
                .baseUrl(WEB_SERVICE_URL)
                .addConverterFactory(MoshiConverterFactory.create()) // we don't need to create an adapter like before
                .build()
            val service = retrofit.create(MonsterService::class.java)
            val serviceData = service.getMonsterData().body() ?: emptyList()
            monsterData.postValue(serviceData)
            monsterDao.deleteAll()
            monsterDao.insertMonsters(serviceData) // inserting data to Db
            // saveDataToCache(serviceData)
        }
    }


    // find out the status of internet connection
    @Suppress("DEPRECATION")
    private fun networkAvailable(): Boolean{
        val connectivityManager = app.getSystemService( Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager
         val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting ?: false
    }


    fun refreshDataFromWeb(){
        // two main dispatchers for Android
        // IO - background thread
        // Main - foreground thread
        CoroutineScope(Dispatchers.IO).launch {
            callWebService()
        }
    }
    private fun saveDataToCache(monsterData: List<Monster>){
        if (ContextCompat.checkSelfPermission(app,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager. PERMISSION_GRANTED){
            val moshi = Moshi.Builder().build()
            val listType = Types.newParameterizedType( List::class.java, Monster::class.java)
            val adapter: JsonAdapter<List<Monster>> = moshi.adapter(listType)
            val json = adapter.toJson(monsterData)

            FileHelper.saveTextToExternalStorage(app, json)
        }
    }

    private fun readDataFromCache(): List<Monster> {
        val json = FileHelper.readTextFromExternalStorage(app) ?: return emptyList()
        val moshi = Moshi.Builder().build()
        val listType = Types.newParameterizedType(List::class.java, Monster::class.java)
        val adapter: JsonAdapter<List<Monster>> = moshi.adapter(listType)
        return adapter.fromJson(json) ?: emptyList()
    }

    private fun readDataFromDB(){

    }


    // parsing json file to Entity object (data class) and getting local data
    /*private fun getMonsterDataLocal() {
        // listType is used to parse data from json to data class
        val listType = Types.newParameterizedType(
            List::class.java, Monster::class.java)
        val text = FileHelper.getTextFromRes(app, R.raw.monster_data)
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter: JsonAdapter<List<Monster>> = moshi.adapter(listType)

        // if i get nullable i'll return an empty list
        // publishing the data to the rest of app
        monsterData.value = adapter.fromJson(text) ?: emptyList()
    }*/
}