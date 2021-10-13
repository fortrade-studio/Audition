package com.atria.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.atria.myapplication.adapter.AboutAdapter
import com.atria.myapplication.databinding.FragmentAboutBinding
import com.atria.myapplication.viewModel.about.AboutFragmentViewModel
import com.atria.myapplication.viewModel.about.AboutFragmentViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AboutFragment : Fragment() , Thread.UncaughtExceptionHandler{


    private lateinit var aboutFragmentBinding : FragmentAboutBinding
    private lateinit var aboutFragmentViewModel : AboutFragmentViewModel
    private lateinit var aboutAdapter : AboutAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        aboutFragmentBinding = FragmentAboutBinding.inflate(inflater,container,false)
        return aboutFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        aboutFragmentViewModel  = ViewModelProvider(this,AboutFragmentViewModelFactory(requireContext(),requireView())).get(AboutFragmentViewModel::class.java)
        Thread.setDefaultUncaughtExceptionHandler(this)
        aboutAdapter = AboutAdapter(aboutFragmentViewModel)
        aboutFragmentBinding.aboutRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        aboutFragmentBinding.aboutRecyclerView.adapter  = aboutAdapter

    }

    override fun onResume() {
        super.onResume()
        aboutAdapter.notifyDataSetChanged()
    }


    companion object{
        private val mainScope = CoroutineScope(Dispatchers.Main)
        private val ioScope = CoroutineScope(Dispatchers.IO)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        FirebaseFirestore.getInstance()
            .collection("Error")
            .document(this::class.java.simpleName)
            .collection(this::class.java.simpleName.toUpperCase())
            .document(e.localizedMessage)
            .set(mapOf(Pair("value",e.stackTraceToString())))
            .addOnSuccessListener { Log.i("here", "uncaughtException: reported")}
    }


}