package com.atria.myapplication.adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.util.TimeUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.atria.myapplication.Constants
import com.atria.myapplication.Constants.current
import com.atria.myapplication.Constants.extras
import com.atria.myapplication.Constants.images
import com.atria.myapplication.Constants.links
import com.atria.myapplication.Constants.user
import com.atria.myapplication.Constants.viewdata
import com.atria.myapplication.ImagesFragment
import com.atria.myapplication.R
import com.atria.myapplication.WindowActivity
import com.atria.myapplication.callbacks.ImageUploadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import java.net.URL
import java.sql.Time
import java.util.*


class ImagesAdapter(
    val images: List<String>,
    val context: Context,
    val isUserProfile: Boolean,
    val fragment:ImagesFragment
) : RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>(),ImageUploadCallback {

    class ImagesViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val imageView = view.findViewById<ImageView>(R.id.roundedImageView)
    }

    companion object{
        private const val TAG = "ImagesAdapter"
        private val auth = FirebaseAuth.getInstance().currentUser
    }

    private val easyImage: EasyImage by lazy {
        EasyImage.Builder(context)
            .setCopyImagesToPublicGalleryFolder(false)
            .setFolderName("EasyImage sample")
            .allowMultiple(false)
            .build()
    }

    private val number:String? by lazy {
        FirebaseAuth.getInstance().currentUser?.phoneNumber
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.images_view, parent, false)
        return ImagesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        if(position == 0){
            holder.imageView.setImageResource(R.drawable.ic_upload)
            holder.imageView.setOnClickListener {
                easyImage.openGallery(fragment)
            }
        }else{
            val pos = if (isUserProfile) position-1
            else position
            ioScope.launch {
                val url = URL(images[pos])
                val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                mainScope.launch {
                    holder.imageView.setImageBitmap(bitmap)
                    holder.imageView.setOnClickListener {
                        val intent = Intent(context, WindowActivity::class.java)
                        intent.putExtra(extras, images.toTypedArray())
                        intent.putExtra(current, pos)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if(isUserProfile) images.size+1;
        else images.size
    }

    fun getImage():EasyImage{
        return easyImage
    }

    private val ioScope  = CoroutineScope(Dispatchers.IO)
    private val mainScope = CoroutineScope(Dispatchers.Main)


    override fun onImageSelected(media: MediaFile) {
            FirebaseStorage.getInstance().getReference("users/images/${auth?.phoneNumber}/")
                .child(System.currentTimeMillis().toString())
                .putFile(media.file.toUri())
                .addOnSuccessListener {
                    it.storage.downloadUrl.addOnSuccessListener {
                        uploadImageLink(it.toString())
                    }
                }
    }

    fun uploadImageLink(link:String){
        FirebaseFirestore.getInstance().collection(user)
            .document(number!!)
            .collection(viewdata)
            .document(Constants.images)
            .set(mapOf(Pair(links,images.toMutableList().apply { add(link) })))
            .addOnSuccessListener {
                mainScope.launch {
                    fragment.onImageUploaded()
                }
            }
    }

    override fun onImageUploaded() {}

}