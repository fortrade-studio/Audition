package com.atria.myapplication

import android.content.Context
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.viewpager.widget.ViewPager
import com.atria.myapplication.adapter.CategoryCardAdapter
import com.atria.myapplication.databinding.FragmentCategoryBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.system.exitProcess


class CategoryFragment : Fragment() , Thread.UncaughtExceptionHandler{

    private lateinit var categoryFragmentBinding: FragmentCategoryBinding

    companion object {
        private const val TAG = "CategoryFragment"
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val button=
        categoryFragmentBinding = FragmentCategoryBinding.inflate(inflater, container, false)
        return categoryFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun onResume() {
        super.onResume()
        if(checkRecord() == 0){
            activity?.finish()
            exitProcess(0)
        }
    }

    fun checkRecord():Int{
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return -1
        val highScore = sharedPref.getInt("saved", -1)
        return highScore
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