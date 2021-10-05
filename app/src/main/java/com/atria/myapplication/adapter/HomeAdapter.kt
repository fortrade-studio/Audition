package com.atria.myapplication.adapter

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.atria.myapplication.Constants
import com.atria.myapplication.R
import com.atria.myapplication.diffutils.VideoData
import com.atria.myapplication.diffutils.VideoDiffUtils
import com.atria.myapplication.notification.NotificationData
import com.atria.myapplication.notification.PushNotification
import com.atria.myapplication.notification.RetrofitInstance
import com.atria.myapplication.utils.NumberToUniqueStringGenerator
import com.atria.myapplication.viewModel.home.HomeParentViewModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeAdapter(
    val context: Context,
    val view: View,
    val list: ArrayList<VideoData>
) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    companion object {
        var mediaPlayer: MediaPlayer? = null
        val db = FirebaseDatabase.getInstance()
        val firebase = FirebaseFirestore.getInstance()
        val phNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber
        private const val TAG = "HomeAdapter"
        val liked = "Liked"
    }

    inner class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val videoView = view.findViewById<VideoView>(R.id.videoView)
        val progressView = view.findViewById<ProgressBar>(R.id.progressBar)
        val beatsImageView = view.findViewById<ImageView>(R.id.beatsImageView)
        val volumeImageView = view.findViewById<ImageView>(R.id.volumeImageView)
        val likeButton = view.findViewById<ImageButton>(R.id.likeButton)


        val usernameTextView = view.findViewById<TextView>(R.id.usernameTextView)
        val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
        val profilePicImageView = view.findViewById<ImageView>(R.id.profileImageView)
        val profileButton = view.findViewById<ImageView>(R.id.profileButton)
        val searchButton = view.findViewById<ImageView>(R.id.searchButton)
    }

    fun updateList(newList: List<VideoData>) {
        val diff = DiffUtil.calculateDiff(VideoDiffUtils(newList, list))
        list.clear()
        list.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shorts_view, parent, false)
        return HomeViewHolder(view)
    }

    private fun HomeViewHolder.loadProfile(position: Int) = CoroutineScope(Dispatchers.IO).launch {
        firebase.collection("Users")
            .document(list[position].userid)
            .collection(Constants.viewdata)
            .document(Constants.links)
            .get()
            .addOnSuccessListener {
                val click = {
                    val bundle = Bundle()
                    bundle.putString("ph", list[position].userid)
                    Navigation.findNavController(view)
                        .navigate(R.id.action_homeFragment_to_profileFragment, bundle)
                }
                usernameTextView.text = it.get("username").toString()
                nameTextView.text = it.get("name").toString()
                Glide.with(context)
                    .load(it.get(Constants.circular))
                    .into(profilePicImageView)

                usernameTextView.setOnClickListener { click() }
                nameTextView.setOnClickListener { click() }
                profilePicImageView.setOnClickListener { click() }

            }
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.progressView.visibility = View.VISIBLE
        holder.videoView.setOnPreparedListener {
            it.isLooping = true
            mediaPlayer = it
            it.start()
            holder.progressView.visibility = View.INVISIBLE
        }
        var like: Boolean = false

        checkIfAlreadyLiked(list[position].uvid){
            if(it){
                like = it
                holder.likeButton.setBackgroundResource(R.drawable.ic_like)
            }
        }

        holder.profileButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("ph", phNumber!!)
            Navigation.findNavController(view)
                .navigate(R.id.action_homeFragment_to_profileFragment, bundle)
        }
        holder.searchButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_searchFragment)
        }

        val hideFunction = Handler()
        val hide = { holder.volumeImageView.visibility = View.INVISIBLE }

        holder.loadProfile(position)
        holder.videoView.setVideoURI(Uri.parse(list[position].link))

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val avd = context.getDrawable(R.drawable.beats) as AnimatedVectorDrawable
            holder.beatsImageView.setImageDrawable(avd)
            avd.start()
        }

        holder.likeButton.setOnClickListener {
            if (!like) {
                holder.likeButton.setBackgroundResource(R.drawable.ic_like)
                likePost(position) {
                    sendNotification(NumberToUniqueStringGenerator.numberToUniqueString(list[position].userid))
                }
            } else {
                holder.likeButton.setBackgroundResource(R.drawable.ic_like_unfill)
                unlikePost(position) {}
            }
            like = !like
        }

        var tapCounter = 0
        var mute: Boolean = true
        val postDelayed = Handler()
        val function = {
            tapCounter = if (tapCounter >= 2) {
                if (!like) {
                    holder.volumeImageView.visibility = View.VISIBLE
                    holder.volumeImageView.setImageResource(R.drawable.ic_like_big)
                    hideFunction.postDelayed(hide, 600)
                } else {
                    holder.volumeImageView.visibility = View.VISIBLE
                    holder.volumeImageView.setImageResource(R.drawable.ic_unlike_big)
                    hideFunction.postDelayed(hide, 600)
                }
                holder.likeButton.callOnClick()
                0
            } else {
                // user have clicked once
                if (mute) {
                    holder.volumeImageView.visibility = View.VISIBLE
                    holder.volumeImageView.setImageResource(R.drawable.ic_mute)
                    blockError { mediaPlayer?.setVolume(0f, 0f) }
                } else {
                    holder.volumeImageView.visibility = View.VISIBLE
                    holder.volumeImageView.setImageResource(R.drawable.ic_unmute)
                    blockError { mediaPlayer?.setVolume(1f, 1f) }
                }
                hideFunction.postDelayed(hide, 400)
                mute = !mute
                0
            }
        }

        val waitHandler = {
            if (tapCounter >= 2) {
                postDelayed.removeCallbacks(function)
            } else {
                postDelayed.postDelayed(function, 220)
            }
        }

        holder.videoView.setOnTouchListener { v, event ->
            if (event?.action == MotionEvent.ACTION_DOWN) {
                ++tapCounter;
                waitHandler()
            } else if (event?.action == MotionEvent.ACTION_SCROLL) {
                postDelayed.removeCallbacks(function)
            } else if (event?.action == MotionEvent.ACTION_MOVE) {
                postDelayed.removeCallbacks(function)
            }
            return@setOnTouchListener true
        }

    }

    private fun likePost(position: Int, onLiked: () -> Unit) =
        CoroutineScope(Dispatchers.IO).launch {
            db.getReference("videos")
                .child(list[position].uvid)
                .get()
                .addOnSuccessListener {
                    val value = it.getValue(VideoData::class.java)
                    Log.i(TAG, "likePost: ${it.value.toString()}")
                    db.getReference("videos")
                        .child(list[position].uvid)
                        .setValue(
                            VideoData(
                                value!!.link,
                                value.likes.plus(1),
                                value.userid,
                                value.uvid
                            )
                        )
                        .addOnSuccessListener {
                            saveToLikes(list[position].uvid)
                            onLiked()
                        }
                }
        }


    private fun unlikePost(position: Int, onUnLike: () -> Unit) =
        CoroutineScope(Dispatchers.IO).launch {
            db.getReference("videos")
                .child(list[position].uvid)
                .get()
                .addOnSuccessListener {
                    val value = it.getValue(VideoData::class.java)
                    db.getReference("videos")
                        .child(list[position].uvid)
                        .setValue(
                            VideoData(
                                value!!.link,
                                value.likes.plus(-1),
                                value.userid,
                                value.uvid
                            )
                        )
                        .addOnSuccessListener {
                            removeFromLikes(list[position].uvid)
                            onUnLike()
                        }
                }
        }

    private fun sendNotification(topic: String)  {
       sendNotification(
            PushNotification(
                NotificationData(
                    "Hello Arya", "Somebody liked your post"
                ), "/topics/$topic"
            )
        )
    }
    private fun sendNotification(notification: PushNotification){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if(response.isSuccessful){
                    Log.i(TAG, "sendNotification: ${response.body().toString()}")
                }else{
                    Log.i(TAG, "failed: ${response.errorBody().toString()}")
                }

            }catch (e: Exception){
                Log.e(TAG, "sendNotification: ", e)
            }
        }
    }

    private fun saveToLikes(uvid: String){
        firebase.collection(Constants.user)
            .document(phNumber!!)
            .collection(liked)
            .document(uvid)
            .set(mapOf(Pair("uvid",uvid)))
    }
    private fun removeFromLikes(uvid: String){
        firebase.collection(Constants.user)
            .document(phNumber!!)
            .collection(liked)
            .document(uvid)
            .delete()
    }

    private fun checkIfAlreadyLiked(uvid:String,onChecked:(Boolean)->Unit){
        firebase.collection(Constants.user)
            .document(phNumber!!)
            .collection(liked)
            .document(uvid)
            .get()
            .addOnSuccessListener {
                onChecked(it.exists())
            }
    }

    private fun blockError(code: () -> Unit) {
        try {
            code()
        } catch (e: Exception) {
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}