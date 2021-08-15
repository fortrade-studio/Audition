package com.atria.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.atria.myapplication.databinding.FragmentAboutBinding
import com.atria.myapplication.viewModel.about.AboutFragmentViewModel
import com.atria.myapplication.viewModel.about.AboutFragmentViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AboutFragment : Fragment() {


    private lateinit var aboutFragmentBinding : FragmentAboutBinding
    private lateinit var aboutFragmentViewModel : AboutFragmentViewModel

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

        ioScope.launch {
            aboutFragmentViewModel.getPersonalData {
                mainScope.launch {
                    aboutFragmentBinding.textView.text = it
                }
            }
        }


    }

    companion object{
        private val mainScope = CoroutineScope(Dispatchers.Main)
        private val ioScope = CoroutineScope(Dispatchers.IO)
    }


}