package com.example.youtubebonusapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.youtube_item_row.view.*


class GridAdapter(private val videoList: List<Map<String, Any>>, private val sharedPreferences: SharedPreferences, val t: Context):
    BaseAdapter() {

    val selectedVideoToPlay = arrayListOf<Int>()
    var canPlayVideos = false
    var currentVideo = -1

    override fun getCount(): Int  = videoList.size

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var inflater: LayoutInflater = t!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var appview: View = inflater.inflate(R.layout.youtube_item_row, null)

        val video = videoList[p0]

        if(video["Title2"].toString().length < 25)
            appview.video_title2.text = video["Title2"].toString()
        else
            appview.video_title2.text = "${video["Title2"].toString().substring(0, 25)}.."
        appview.video_views.text = "Views: ${video["Views"]}"
        if(video["Image"] != R.drawable.iic){
            Picasso.get().load(video["Image"].toString()).into(appview.video_image)
        }



        appview.card_view.setOnClickListener {
            if(p0 != currentVideo){
                currentVideo = p0
                val views = video["Views"].toString().toInt() + 1
                sharedPreferences.edit().putInt("ViewsVid$p0", views).apply()
                val activity = t as MainActivity
                activity.videosList[p0]["Views"] = views
                activity.update(p0, views)
                val intent = Intent(t, PlayVideo::class.java)
                intent.putExtra("ID", video["ID"].toString())
                t.startActivity(intent)
            }
        }

        appview.video_checkbox.setOnClickListener {
            if(appview.video_checkbox.isChecked)
                selectedVideoToPlay.add(p0)
            else
                selectedVideoToPlay.remove(p0)

            canPlayVideos = selectedVideoToPlay.size > 0
        }

        return appview
    }
}