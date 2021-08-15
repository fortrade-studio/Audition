package com.atria.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.atria.myapplication.adapter.ImagesAdapter
import com.atria.myapplication.callbacks.ImageUploadCallback
import com.atria.myapplication.databinding.FragmentImagesBinding
import com.atria.myapplication.viewModel.images.ImagesFragmentViewModel
import com.atria.myapplication.viewModel.images.ImagesFragmentViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import kotlin.properties.Delegates

class ImagesFragment : Fragment() , ImageUploadCallback {

    private lateinit var imagesFragmentBinding: FragmentImagesBinding
    private lateinit var imagesFragmentViewModel: ImagesFragmentViewModel
    private lateinit var imagesAdapter: ImagesAdapter
    private var isUserProfile by Delegates.notNull<Boolean>()

    companion object{
        private const val TAG = "ImagesFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        imagesFragmentBinding = FragmentImagesBinding.inflate(inflater, container, false)
        return imagesFragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isUserProfile = Constants.profile_id == FirebaseAuth.getInstance().currentUser?.phoneNumber

        imagesFragmentViewModel = ViewModelProvider(
            this,
            ImagesFragmentViewModelFactory(requireContext(), requireView())
        ).get(ImagesFragmentViewModel::class.java)
        imagesFragmentBinding.imagesRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
        imagesFragmentViewModel.getImagesOfUser {
            if (it.size == 8) isUserProfile = false
            imagesAdapter = ImagesAdapter(it, requireContext(), isUserProfile, this)
            imagesFragmentBinding.imagesRecyclerView.isNestedScrollingEnabled = false
            imagesFragmentBinding.imagesRecyclerView.adapter = imagesAdapter
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        imagesAdapter.getImage().handleActivityResult(requestCode,
            resultCode,
            data,
            this.requireActivity(),
            object : DefaultCallback() {

                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    imagesAdapter.onImageSelected(imageFiles[0])
                }

            })

    }

    override fun onImageSelected(media: MediaFile) {}

    override fun onImageUploaded() {
        imagesFragmentViewModel.getImagesOfUser {
            Log.i(TAG, "onImageUploaded: "+it.toString()+"  "+it.size)
            if (it.size == 8) isUserProfile = false
            imagesAdapter = ImagesAdapter(it, requireContext(), isUserProfile, this)
            imagesFragmentBinding.imagesRecyclerView.adapter = imagesAdapter
        }

    }

}