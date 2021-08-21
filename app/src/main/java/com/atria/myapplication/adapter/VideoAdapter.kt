package com.atria.myapplication.adapter

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.atria.myapplication.Constants
import com.atria.myapplication.Constants.current
import com.atria.myapplication.Constants.extras
import com.atria.myapplication.R
import com.atria.myapplication.VideoFragment
import com.atria.myapplication.WindowActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import java.lang.ClassCastException



class VideoAdapter(
    val videos:List<String>,
    val context:Context,
    val fragment:VideoFragment,
    val isUserProfile:Boolean
) : RecyclerView.Adapter<VideoAdapter.VideoHolder>() {

    private lateinit var builder: AlertDialog.Builder

    companion object{
        private const val TAG = "VideoAdapter"
    }

    class VideoHolder(view: View) : RecyclerView.ViewHolder(view) {
        val videoView = view.findViewById<VideoView>(R.id.videoView)
        val imageView = view.findViewById<ImageView>(R.id.roundedImageView)
    }


    private val number:String? by lazy {
        FirebaseAuth.getInstance().currentUser?.phoneNumber
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        if (viewType == 11) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.images_view, parent, false)
            return VideoHolder(view)
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.videos_view, parent, false)
        return VideoHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return 11;
        return super.getItemViewType(position)
    }

    private fun showDialog() {
        builder =
            AlertDialog.Builder(context)
                .setMessage("UPLOAD VIDEO")
                .setPositiveButton("Upload from gallery") { dialog, which ->
                    Log.i("upload from gallery", "true ")
                    Dexter.withContext(context)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(object : PermissionListener {
                            override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                                val intent = Intent()
                                intent.type = "video/*"
                                intent.action = Intent.ACTION_GET_CONTENT

                                fragment.activity?.startActivityForResult(intent, 8)

                            }

                            override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {}
                            override fun onPermissionRationaleShouldBeShown(
                                permissionRequest: PermissionRequest,
                                permissionToken: PermissionToken
                            ) {
                                permissionToken.continuePermissionRequest()
                            }
                        }).check()


                }.setNeutralButton("Cancel") { dialog, which ->
                    Log.i("cancel", "true ")
                }.setNegativeButton("Record using camera") { dialog, which ->
                    Log.i("upload from camera", "true ")
                    Dexter.withContext(context)
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(object : PermissionListener {
                            override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                                val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60)
                                fragment.activity?.startActivityForResult(intent, 1)

                            }

                            override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {}
                            override fun onPermissionRationaleShouldBeShown(
                                permissionRequest: PermissionRequest,
                                permissionToken: PermissionToken
                            ) {
                                permissionToken.continuePermissionRequest()
                            }
                        }).check()
                }

    }

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        if (position == 0) {
            holder.imageView.setImageResource(R.drawable.ic_upload)
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            Log.i(TAG, "onBindViewHolder: ${videos.toString()}")
            val link = videos[position.minus(1)]
            holder.videoView.setVideoURI(Uri.parse(link))
            val random = Random()
            holder.videoView.setOnPreparedListener {
                it.setVolume(0f, 0f)
                it.start()
                holder.videoView.setOnClickListener {
                    val intent = Intent(context, WindowActivity::class.java)
                    intent.putExtra(extras, videos.toTypedArray())
                    intent.putExtra(current, if(isUserProfile) position-1 else position)
                    intent.putExtra(Constants.videos, true)
                    context.startActivity(intent)
                }
            }
        }

    }




//    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
//            val builders = AlertDialog.Builder(context)
//            builders.setCancelable(false)
//            val videoView: VideoView = VideoView(context)
//            val videoUri = data?.data
//
//            videoView.setVideoURI(videoUri)
//            videoView.start()
//
//            builders.setView(videoView).show()
//
//        }
//
//        if (resultCode == Activity.RESULT_OK && requestCode == 8) {
//            if (data?.data != null) {
//                val builders = AlertDialog.Builder(context)
//                builders.setCancelable(false)
//                val videoView: VideoView = VideoView(context)
//                var uri: Uri = data.data!!
//                videoView.setVideoURI(uri)
//                var mediaController: MediaController = MediaController(context)
//                val retriever = MediaMetadataRetriever()
//                retriever.setDataSource(context, Uri.parse(uri.toString()))
//                val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
//                var timeInMillisec = time?.toLong();
//                retriever.release()
//                if (timeInMillisec != null) {
//                    if (timeInMillisec <= 60000) {
//
//
//                    } else {
//                        Toast.makeText(
//                            context,
//                            "Duration of video more than 60 seconds",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//        }
//    }


    override fun getItemCount(): Int {
            if(isUserProfile) {
                return videos.size+1;
            }else{
                return videos.size
            }
        }






}