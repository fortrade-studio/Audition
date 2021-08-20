package com.atria.myapplication

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.atria.myapplication.adapter.VideoAdapter
import com.atria.myapplication.databinding.FragmentVideoBinding
import com.atria.myapplication.viewModel.video.VideoFragmentViewModel
import com.atria.myapplication.viewModel.video.VideoFragmentViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener


class VideoFragment : Fragment() {

    private lateinit var videoFragmentBinding : FragmentVideoBinding
    private lateinit var videoFragmentViewModel : VideoFragmentViewModel
    private lateinit var but: TextView
    private lateinit var builder: MaterialAlertDialogBuilder
    private lateinit var fragment:VideoFragment









    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        videoFragmentBinding = FragmentVideoBinding.inflate(inflater,container,false)
        return videoFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoFragmentViewModel = ViewModelProvider(this,VideoFragmentViewModelFactory(requireContext(),requireView())).get(VideoFragmentViewModel::class.java)
        videoFragmentBinding.videosRecyclerView.layoutManager = GridLayoutManager(requireContext(),3,GridLayoutManager.VERTICAL,false)

        videoFragmentViewModel.getVideos {
            videoFragmentBinding.videosRecyclerView.adapter = VideoAdapter(it,requireContext(),this)
        }
        but = view.findViewById(R.id.uploadVideoButton)
        but.setOnClickListener{
            showdialogfun()
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
                                //fragment.activity?.
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
                //val builders = AlertDialog.Builder(context)
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
                            .setPositiveButton(
                            str1,
                            object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    //uploadGalleryVideoToFirebase(uri)
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






}