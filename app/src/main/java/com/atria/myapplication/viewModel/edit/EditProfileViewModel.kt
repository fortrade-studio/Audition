package com.atria.myapplication.viewModel.edit

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.lifecycle.ViewModel
import com.atria.myapplication.Constants.big_link
import com.atria.myapplication.Constants.circular_link
import com.atria.myapplication.Constants.links
import com.atria.myapplication.Constants.user
import com.atria.myapplication.Constants.viewdata
import com.atria.myapplication.adapter.ImagesAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

class EditProfileViewModel(
    val context: Context,
    val view: View
) : ViewModel() {

    companion object {
        private val firebase = FirebaseFirestore.getInstance()
        private val auth = FirebaseAuth.getInstance().currentUser
        private val storage = FirebaseStorage.getInstance()
        private const val TAG = "EditProfileViewModel"
    }

    fun editProfile(map: MutableMap<String, String>, onSuccess: () -> Unit) {

        firebase.collection(user)
            .document(auth?.phoneNumber!!)
            .set(map, SetOptions.merge())
            .addOnSuccessListener {
                uploadElseWhere(map["name"], map["summary"]) {
                    onSuccess()
                }
            }

    }

    fun uploadProfileImage(profile: Uri? = null, big: Uri? = null, onSuccess: () -> Unit) {
        if (profile != null && big != null) {
            if (big_link == "https://www.knivesindia.com/ecom/wp-content/uploads/2017/06/wood-blog-placeholder.jpg") {
                if (circular_link == "https://holmesbuilders.com/wp-content/uploads/2016/12/male-profile-image-placeholder.png") {
                    storage.getReference("users/images/${auth?.phoneNumber}/")
                        .child(System.currentTimeMillis().toString())
                        .putFile(profile)
                        .addOnSuccessListener {
                            it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                                val map = mutableMapOf(Pair("circular", it.toString()))
                                storage.getReference("users/images/${auth?.phoneNumber}")
                                    .child(System.currentTimeMillis().toString())
                                    .putFile(big)
                                    .addOnSuccessListener {
                                        it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                                            map.put("big", it.toString())
                                            firebase.collection(user)
                                                .document(auth?.phoneNumber!!)
                                                .collection(viewdata)
                                                .document(links)
                                                .set(map, SetOptions.merge())
                                                .addOnSuccessListener {
                                                    big_link = map["big"].toString()
                                                    circular_link = map["circular"].toString()
                                                    onSuccess()
                                                }
                                        }
                                    }
                            }
                        }

                } else {
                    storage.getReference("users/images/${auth?.phoneNumber}/")
                        .child(circular_link.substringBefore("?alt").split("%2F").last())
                        .delete()
                        .addOnSuccessListener {
                            storage.getReference("users/images/${auth?.phoneNumber}/")
                                .child(System.currentTimeMillis().toString())
                                .putFile(profile)
                                .addOnSuccessListener {
                                    it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                                        val map = mutableMapOf(Pair("circular", it.toString()))
                                        firebase.collection(user)
                                            .document(auth?.phoneNumber!!)
                                            .collection(viewdata)
                                            .document(links)
                                            .set(map, SetOptions.merge())
                                            .addOnSuccessListener {
                                                circular_link =
                                                    map["circular"].toString()
                                                onSuccess()
                                            }
                                    }
                                }
                        }
                }
            } else {
                storage.getReference("users/images/${auth?.phoneNumber}/")
                    .child(big_link.substringBefore("?alt").split("%2F").last())
                    .delete()
                    .addOnSuccessListener {
                        storage.getReference("users/images/${auth?.phoneNumber}/")
                            .child(circular_link.substringBefore("?alt").split("%2F").last())
                            .delete()
                            .addOnSuccessListener {
                                storage.getReference("users/images/${auth?.phoneNumber}/")
                                    .child(System.currentTimeMillis().toString())
                                    .putFile(profile)
                                    .addOnSuccessListener {
                                        it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                                            val map = mutableMapOf(Pair("circular", it.toString()))
                                            storage.getReference("users/images/${auth?.phoneNumber}")
                                                .child(System.currentTimeMillis().toString())
                                                .putFile(big)
                                                .addOnSuccessListener {
                                                    it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                                                        map.put("big", it.toString())
                                                        firebase.collection(user)
                                                            .document(auth?.phoneNumber!!)
                                                            .collection(viewdata)
                                                            .document(links)
                                                            .set(map, SetOptions.merge())
                                                            .addOnSuccessListener {
                                                                big_link = map["big"].toString()
                                                                circular_link =
                                                                    map["circular"].toString()
                                                                onSuccess()
                                                            }
                                                    }
                                                }
                                        }
                                    }
                            }
                    }
            }

        } else if (profile != null && big == null) {
            if (circular_link == "https://holmesbuilders.com/wp-content/uploads/2016/12/male-profile-image-placeholder.png") {
                storage.getReference("users/images/${auth?.phoneNumber}/")
                    .child(System.currentTimeMillis().toString())
                    .putFile(profile)
                    .addOnSuccessListener {
                        it.metadata?.reference?.downloadUrl?.addOnSuccessListener { u ->
                            val map = mutableMapOf(Pair("circular", u.toString()))
                            firebase.collection(user)
                                .document(auth?.phoneNumber!!)
                                .collection(viewdata)
                                .document(links)
                                .set(map, SetOptions.merge())
                                .addOnSuccessListener {
                                    circular_link = u.toString()
                                    onSuccess()
                                }
                        }
                    }
            } else {
                storage.getReference("users/images/${auth?.phoneNumber}/")
                    .child(circular_link.substringBefore("?alt").split("%2F").last())
                    .delete()
                    .addOnSuccessListener {
                        storage.getReference("users/images/${auth?.phoneNumber}/")
                            .child(System.currentTimeMillis().toString())
                            .putFile(profile)
                            .addOnSuccessListener {
                                it.metadata?.reference?.downloadUrl?.addOnSuccessListener { u ->
                                    val map = mutableMapOf(Pair("circular", u.toString()))
                                    firebase.collection(user)
                                        .document(auth?.phoneNumber!!)
                                        .collection(viewdata)
                                        .document(links)
                                        .set(map, SetOptions.merge())
                                        .addOnSuccessListener {
                                            circular_link = u.toString()
                                            onSuccess()
                                        }
                                }
                            }
                    }
            }
        } else if (big != null && profile == null) {
            if (big_link == "https://www.knivesindia.com/ecom/wp-content/uploads/2017/06/wood-blog-placeholder.jpg") {
                storage.getReference("users/images/${auth?.phoneNumber}/")
                    .child(System.currentTimeMillis().toString())
                    .putFile(big)
                    .addOnSuccessListener {
                        it.metadata?.reference?.downloadUrl?.addOnSuccessListener { u ->
                            val map = mutableMapOf(Pair("big", u.toString()))
                            firebase.collection(user)
                                .document(auth?.phoneNumber!!)
                                .collection(viewdata)
                                .document(links)
                                .set(map, SetOptions.merge())
                                .addOnSuccessListener {
                                    big_link = u.toString()
                                    onSuccess()
                                }
                        }
                    }
            }else {
                storage.getReference("users/images/${auth?.phoneNumber}/")
                    .child(big_link.substringBefore("?alt").split("%2F").last())
                    .delete()
                    .addOnSuccessListener {
                        storage.getReference("users/images/${auth?.phoneNumber}/")
                            .child(System.currentTimeMillis().toString())
                            .putFile(big)
                            .addOnSuccessListener {
                                it.metadata?.reference?.downloadUrl?.addOnSuccessListener { u ->
                                    val map = mutableMapOf(Pair("big", u.toString()))
                                    firebase.collection(user)
                                        .document(auth?.phoneNumber!!)
                                        .collection(viewdata)
                                        .document(links)
                                        .set(map, SetOptions.merge())
                                        .addOnSuccessListener {
                                            big_link = u.toString()
                                            onSuccess()
                                        }
                                }
                            }
                    }
            }
        } else {
            onSuccess()
        }
    }

    private fun uploadElseWhere(name: String?, summary: String?, onSuccess: () -> Unit) {
        if (name.isNullOrEmpty() && summary.isNullOrEmpty()) {
            onSuccess()
            return
        }
        val map = mutableMapOf<String, String>()
        if (!name.isNullOrEmpty()) {
            map["name"] = name
        }
        if (!summary.isNullOrEmpty()) {
            map["summary"] = summary
        }
        firebase.collection(user)
            .document(auth?.phoneNumber!!)
            .collection(viewdata)
            .document(links)
            .set(map, SetOptions.merge())
            .addOnSuccessListener {
                onSuccess()
            }
    }
}