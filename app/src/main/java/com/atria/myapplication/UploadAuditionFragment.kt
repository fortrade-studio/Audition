package com.atria.myapplication

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.atria.myapplication.databinding.FragmentUploadAuditionBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlin.properties.Delegates


class UploadAuditionFragment : Fragment() {

    private lateinit var fragmentUploadAuditionBinding: FragmentUploadAuditionBinding
    private lateinit var but: ImageView
    private lateinit var videoPreview: VideoView
    private lateinit var videoURITextview: TextView
    private lateinit var spinner: Spinner
    private lateinit var builder: MaterialAlertDialogBuilder
    private var isUserProfile by Delegates.notNull<Boolean>()
    var category = resources.getStringArray(R.array.Category)
   // private val categoryOfAudition: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_audition, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isUserProfile = Constants.profile_id == FirebaseAuth.getInstance().currentUser?.phoneNumber
        spinner = view.findViewById(R.id.spinner)
        if (spinner != null) {
            val adapter= ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line,category)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
              
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
        }
        }

//        videoFragmentViewModel = ViewModelProvider(this,VideoFragmentViewModelFactory(requireContext(),requireView())).get(VideoFragmentViewModel::class.java)
//        videoFragmentBinding.videosRecyclerView.layoutManager = GridLayoutManager(requireContext(),3,GridLayoutManager.VERTICAL,false)
//
//        videoFragmentViewModel.getVideos {
//            videoFragmentBinding.videosRecyclerView.adapter = VideoAdapter(it,requireContext(),this,isUserProfile)
//        }
         videoPreview = view.findViewById(R.id.uploadVideoVideoView)
         videoURITextview = view.findViewById(R.id.videoURIdisplayTextview)


        but = view.findViewById(com.atria.myapplication.R.id.uploadVideoImageView)
        Log.i("TAG", isUserProfile.toString())
        but.setOnClickListener{
            showdialogfun()
        }

        if (isUserProfile){
            but.setOnClickListener{
                showdialogfun()
            }
        }else
        {
            but.setOnClickListener{
            }
        }


    }

    private fun showdialogfun() {
        val upl=Html.fromHtml(getString(R.string.UPLOAD))


        builder =
            MaterialAlertDialogBuilder(requireContext())
                .setBackground(getResources().getDrawable(R.drawable.dialogbox_background))
                .setMessage(upl)
                .setPositiveButton("Upload from gallery") { dialog, which ->
                    Log.i("upload from gallery", "true ")
                    Dexter.withContext(context)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(object : PermissionListener {
                            override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                                val intent = Intent()
                                intent.type = "video/*"
                                intent.action = Intent.ACTION_GET_CONTENT

                                // fragment.activity?.
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
                                startActivityForResult(intent, 1)

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

        builder.show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val builders = MaterialAlertDialogBuilder(requireContext())
            builders.setCancelable(false)
            val videoView: VideoView = VideoView(context)
            val videoUri = data?.data
            val str1=Html.fromHtml(getString(R.string.some_text))

            videoView.setVideoURI(videoUri)
            videoView.start()

            builders
                .setBackground(getResources().getDrawable(R.drawable.videopreview_bg))
                .setCancelable(true)
                .setPositiveButton(
                    str1,
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            Toast.makeText(requireContext(), "uploaded", Toast.LENGTH_SHORT).show()
                            //uploadCameraVideoToFirebase(videoUri)

                        }
                    })
            builders.setView(videoView).show()

        }

        if (resultCode == Activity.RESULT_OK && requestCode == 8) {
            if (data?.data != null) {
                val builders = MaterialAlertDialogBuilder(requireContext())
                builders.setCancelable(false)
                val videoView: VideoView = VideoView(context)
                val str1=Html.fromHtml(getString(R.string.some_text))

                var uri: Uri = data.data!!
                videoView.setVideoURI(uri)
                var mediaController: MediaController = MediaController(context)
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, Uri.parse(uri.toString()))
                val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                var timeInMillisec = time?.toLong();
                retriever.release()
                if (timeInMillisec != null) {
                    if (timeInMillisec <= 60000) {
                        videoView.setMediaController(mediaController)
                        videoView.start()
                        builders
                            .setBackground(getResources().getDrawable(R.drawable.videopreview_bg))
                            .setCancelable(true)
                            .setPositiveButton(
                                str1,
                                object : DialogInterface.OnClickListener {
                                    override fun onClick(dialog: DialogInterface?, which: Int) {
                                        videoURITextview.text= uri.toString()
                                        videoPreview.setVideoPath("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4")
                                        videoPreview.alpha=1f
                                        videoPreview.start()
//                                        fun uploadVideo(uri:Uri){
//                                            storage.getReference("users/videos/${auth.currentUser?.phoneNumber}/")
//                                                .child(System.currentTimeMillis().toString())
//                                                .putFile(uri)
//                                                .addOnSuccessListener {
//                                                    it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
//                                                        firebase.collection(Constants.user)
//                                                            .document()
//                                                            .collection()
//                                                            .document()
//                                                            .set()
//                                                            .addOnSuccessListener {
//                                                                Log.i("TAG", "uploadVideo: DONE VIDEO")
//                                                            }
//                                                    }
//                                                }
//                                        }

                                    }
                                })

                        builders.setView(videoView).show()
                        Toast.makeText(
                            getActivity(),
                            "File selected successfully",
                            Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        Toast.makeText(
                            getActivity(),
                            "Duration of video exceeds 60 seconds",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
    companion object {
        val firebase = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val storage = FirebaseStorage.getInstance()
    }






}


