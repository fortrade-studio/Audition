package com.atria.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toUri
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.atria.myapplication.Constants.big_link
import com.atria.myapplication.Constants.circular_link
import com.atria.myapplication.Constants.profile
import com.atria.myapplication.databinding.FragmentEditProfileBinding
import com.atria.myapplication.viewModel.edit.EditProfileViewModel
import com.atria.myapplication.viewModel.edit.EditProfileViewModelFactory
import com.bumptech.glide.Glide
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.Wave
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource


class EditProfileFragment : Fragment(), Thread.UncaughtExceptionHandler {

    private lateinit var editProfileFragmentBinding: FragmentEditProfileBinding
    private lateinit var editProfileViewModel: EditProfileViewModel

    private val objects = listOf("Male", "Female")

    var big : Uri? = null
    var circular: Uri? = null

    companion object {
        private const val TAG = "EditProfileFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        editProfileFragmentBinding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return editProfileFragmentBinding.root
    }

    private val easyImage: EasyImage by lazy {
        EasyImage.Builder(requireContext())
            .setCopyImagesToPublicGalleryFolder(false)
            .setFolderName("EasyImage sample")
            .allowMultiple(false)
            .build()
    }
    private val easyBigImage: EasyImage by lazy {
        EasyImage.Builder(requireContext())
            .setCopyImagesToPublicGalleryFolder(false)
            .setFolderName("EasyImage")
            .allowMultiple(false)
            .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 10000001){
            editProfileFragmentBinding.bigScreenEditImageView.setImageURI(data?.data)
            big = data?.data
        }

        easyImage.handleActivityResult(requestCode, resultCode, data, requireActivity(),
            object : EasyImage.Callbacks {
                override fun onCanceled(source: MediaSource) {}
                override fun onImagePickerError(error: Throwable, source: MediaSource) {}

                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    editProfileFragmentBinding.profileEditImageView.setImageURI(imageFiles[0].file.toUri())
                    circular = imageFiles[0].file.toUri()
                }
            })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(this)


        editProfileFragmentBinding.backTextView.setOnClickListener {
            Constants.checkIfUser = 0
            findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
        }

        val objects = listOf("MALE", "FEMALE")

        editProfileFragmentBinding.included.genderEditText.adapter =
            ArrayAdapter(requireContext(), R.layout.spinner_item, objects)


        editProfileViewModel = ViewModelProvider(
            this, EditProfileViewModelFactory(
                requireContext(), requireView()
            )
        ).get(EditProfileViewModel::class.java)

        Glide.with(this)
            .load(big_link)
            .into(editProfileFragmentBinding.bigScreenEditImageView)

        Glide.with(this)
            .load(circular_link)
            .into(editProfileFragmentBinding.profileEditImageView)

        if (profile !=null && profile is com.atria.myapplication.room.User) {

            editProfileFragmentBinding.included.fnameEditText.setText(profile?.name)
            editProfileFragmentBinding.included.dateEditText.setText(profile?.date)
            if(profile?.gender?.trim().equals("MALE",true)){
                editProfileFragmentBinding.included.genderEditText.setSelection(0)
            }else{
                editProfileFragmentBinding.included.genderEditText.setSelection(1)
            }
            editProfileFragmentBinding.included.cityEditText.setText(profile?.city)
            editProfileFragmentBinding.included.emailEditText.setText(profile?.email)
            editProfileFragmentBinding.included.countryEditText.setText(profile?.country)
            editProfileFragmentBinding.included.summaryEditText.setText(profile?.summary)
            editProfileFragmentBinding.included.stateEditText.setText(profile?.state)
            editProfileFragmentBinding.included.artistTypeEditText.setText(profile?.artistType)
            editProfileFragmentBinding.included.ethnicityEditText.setText(profile?.ethnicity)
            editProfileFragmentBinding.included.languageEditText.setText(profile?.languages)
            editProfileFragmentBinding.included.preferenceEditText.setText(profile?.preferences)
            editProfileFragmentBinding.included.experienceEditText.setText(profile?.experience)
        }

        editProfileFragmentBinding.profileEditImageView.setOnClickListener {
            easyImage.openGallery(this)
        }
        editProfileFragmentBinding.bigScreenEditImageView.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"),10000001)
        }

        editProfileFragmentBinding.doneTextView.setOnClickListener {
            editProfileFragmentBinding.spinKit.visibility = View.VISIBLE
            editProfileFragmentBinding.spinKit.setIndeterminateDrawable(Wave())
            editProfileViewModel.uploadProfileImage(circular,big) {
                editProfileViewModel.editProfile(getMap()) {
                    editProfileFragmentBinding.spinKit.visibility = View.GONE
                    Toast.makeText(requireContext(), "Uploaded Successfully", Toast.LENGTH_SHORT)
                        .show()
                    Handler().postDelayed({
                        Constants.checkIfUser = 0
                        findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
                    },700)
                }
            }
        }

    }

    private fun getMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        editProfileFragmentBinding.included.personalDetailsContainer.children.forEach {
            if (it is EditText) {
                if (it.text.isNotEmpty()) {
                    map.put(it.tag.toString(), it.text.toString())
                }
            }else if (it is Spinner){
                val index = it.selectedItemPosition
                map[it.tag.toString()] = objects[index]
            }
            else if (it is LinearLayout) {
                for (child in it.children) {
                    if (child is EditText) {
                        if (child.text.isNotEmpty()) {
                            map.put(child.tag.toString(), child.text.toString())
                        }
                    }
                }
            }
        }

        return map;
    }
    override fun uncaughtException(t: Thread, e: Throwable) {
        FirebaseFirestore.getInstance()
            .collection("Error")
            .document(this::class.java.simpleName)
            .collection(this::class.java.simpleName.toUpperCase())
            .document(e.localizedMessage)
            .set(mapOf(Pair("value",e.stackTraceToString())))
            .addOnSuccessListener { Log.i(TAG, "uncaughtException: reported")}
    }

}