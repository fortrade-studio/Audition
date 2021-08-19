package com.atria.myapplication


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.media.Image
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
//import kotlinx.android.synthetic.main.item_video.*
import java.io.File


class CameraFragment : Fragment() {

    private lateinit var captureVideo: ImageButton
    private lateinit var gallery: ImageView
    private lateinit var videoRec: VideoView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_camera, container, false)
        captureVideo = view.findViewById(R.id.captureVideo)
        gallery = view.findViewById(R.id.gallery)
        videoRec = view.findViewById(R.id.videoRec)




        captureVideo.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,60)
            startActivityForResult(intent,1)
        }

        gallery.setOnClickListener {
            Dexter.withContext(context)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                        val intent = Intent()
                        intent.type = "video/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        startActivityForResult(intent, 8)
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

        return view
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            videoRec.setVideoURI(data?.data)
            videoRec.start()
        }

        if (resultCode == Activity.RESULT_OK && requestCode == 8) {
            if (data?.data != null) {
                val uri:Uri = data.data!!
                videoRec.setVideoURI(uri)
                val mediaController: MediaController = MediaController(context)
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, Uri.parse(uri.toString()))
                val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val timeInMillisec = time?.toLong();
                retriever.release()
                if (timeInMillisec != null) {
                    if (timeInMillisec<=60000)
                    {
                        videoRec.setMediaController(mediaController)
                        videoRec.start()
                    }
                    else
                    {
                        Toast.makeText(getActivity(),"Duration of video more than 60 seconds",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

}