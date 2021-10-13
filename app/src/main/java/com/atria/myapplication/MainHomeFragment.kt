package com.atria.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.atria.myapplication.adapter.ActiveAuditionAdapter
import com.atria.myapplication.adapter.AuditionAdapter
import com.atria.myapplication.databinding.FragmentMainHomeBinding
import com.atria.myapplication.viewModel.home.HomeFragmentViewModel
import com.atria.myapplication.viewModel.home.HomeFragmentViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore


class MainHomeFragment : Fragment() , Thread.UncaughtExceptionHandler {

    private lateinit var mainHomeFragmentBinding : FragmentMainHomeBinding
    private lateinit var homeFragmentViewModel: HomeFragmentViewModel
    var callback : MutableLiveData<Int>  = MutableLiveData(0)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainHomeFragmentBinding = FragmentMainHomeBinding.inflate(inflater, container, false)
        return mainHomeFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(this)
        Constants.mainHomeFragment =this
        homeFragmentViewModel  = ViewModelProvider(
            this, HomeFragmentViewModelFactory(
                requireView(),
                requireContext()
            )
        ).get(HomeFragmentViewModel::class.java)

        callback.observe(viewLifecycleOwner){
            if(it == 1){
                Constants.mainHomeFragment?.callback?.value = 0
//                findNavController().navigate(R.id.action_mainHomeFragment_to_searchFragment)
            }
        }

        mainHomeFragmentBinding.newestOpeningRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        homeFragmentViewModel.getNewestOpenings {
            mainHomeFragmentBinding.newestOpeningRecyclerView.adapter = AuditionAdapter(it, this)
        }

        mainHomeFragmentBinding.activelyHiringRecyclerView.isNestedScrollingEnabled = false
        mainHomeFragmentBinding.activelyHiringRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        homeFragmentViewModel.getActivelyOpening {
            mainHomeFragmentBinding.activelyHiringRecyclerView.adapter =ActiveAuditionAdapter(
                it,
                this
            )
        }
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