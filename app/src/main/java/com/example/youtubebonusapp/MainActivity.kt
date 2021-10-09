package com.example.youtubebonusapp


import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var gridMain: GridView
    lateinit var videosList: MutableList<MutableMap<String, Any>>
    lateinit var adapter: GridAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("Alsultan_youtube_Grid", MODE_PRIVATE)

        videosList = arrayListOf(
            mutableMapOf(
                "Views" to sharedPreferences.getInt("ViewsVid0", 0),
                "ID" to "ZbZFMDk69IA",
            ),
            mutableMapOf(
                "Views" to sharedPreferences.getInt("ViewsVid1", 0),
                "ID" to "G_XYXuC8b9M",
            ),
            mutableMapOf(
                "Views" to sharedPreferences.getInt("ViewsVid2", 0),
                "ID" to "OUk-UCdiV6M&t=903s"
            ),
            mutableMapOf(
                "Views" to sharedPreferences.getInt("ViewsVid3", 0),
                "ID" to "Up63iX6gzfk"
            )
        )


        gridMain = findViewById(R.id.gridMain)
        adapter = GridAdapter(videosList, sharedPreferences, this)
        gridMain.adapter = adapter

        val cm = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        if(activeNetwork?.isConnectedOrConnecting == true) {
            fetchImage(this)
            gridMain.visibility = View.VISIBLE
        }
        else
            gridMain.visibility = View.GONE
    }


    fun update(position: Int, views: Int){
        Log.d("ppp", views.toString())
        videosList[position]["Views"] = views
        adapter.notifyDataSetChanged()
    }

    private fun fetchImage(t: Context){
        CoroutineScope(IO).launch {
            for(i in 0..videosList.size-1){
                val response = async {
                    var tmp = ""
                    try {
                        tmp = URL("https://www.youtube.com/oembed?url=youtube.com/watch?v=${videosList[i]["ID"]}&format=json").readText(Charsets.UTF_8)
                    }catch (e: Exception){
                        Log.d("ppppppp", "efeoinfo")
                    }
                    tmp
                }.await()
                if(response.isNotEmpty()) {
                    addData(i, response)
                    Log.d("Infooo", videosList[i].toString())
                }
                else
                    videosList[i]["Image_url"] = R.drawable.iic
            }
        }
    }

    private suspend fun addData(idx: Int, response: String){
        withContext(Main){
            try {
                val obj = JSONObject(response)
                videosList[idx]["Title2"] = obj.getString("title")
                videosList[idx]["Image"] = obj.getString("thumbnail_url")
                adapter.notifyDataSetChanged()
            }catch (e: Exception){

            }
        }
    }
}