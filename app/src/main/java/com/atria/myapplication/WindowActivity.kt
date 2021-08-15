package com.atria.myapplication

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.atria.myapplication.Constants.current
import com.atria.myapplication.Constants.extras
import com.atria.myapplication.databinding.ActivityWindowBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class WindowActivity : AppCompatActivity() {

    private lateinit var windowActivityBinding: ActivityWindowBinding
    private val list = ArrayList<String>()

    companion object {
        private val ioScope = CoroutineScope(Dispatchers.IO)
        private val mainScope = CoroutineScope(Dispatchers.Main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        windowActivityBinding = ActivityWindowBinding.inflate(layoutInflater)
        setContentView(windowActivityBinding.root)

        val stringArrayExtra: Array<String> = intent.getStringArrayExtra(extras)!!
        val isVideos = intent.getBooleanExtra(Constants.videos, false)
        val position = intent.getIntExtra(current, -1)
        list.addAll(stringArrayExtra)
        if (isVideos) {
            windowActivityBinding.bigVideoView.visibility = View.VISIBLE
            windowActivityBinding.bigImageView.visibility = View.GONE
            val link = list[position]
            windowActivityBinding.bigVideoView.setVideoURI(Uri.parse(link))
            windowActivityBinding.bigVideoView.setOnPreparedListener {
                it.start()
            }

        } else {
            ioScope.launch {
                val url = URL(list[position])
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                mainScope.launch {
                    windowActivityBinding.bigImageView.setImageBitmap(bmp)
                }
            }
        }

    }
}