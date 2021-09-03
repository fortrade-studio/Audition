package com.atria.myapplication

import android.animation.TimeInterpolator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.atria.myapplication.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class HomeFragment : Fragment() {

    private lateinit var homeFragmentBinding: FragmentHomeBinding
    companion object {
        private const val TAG = "HomeFragment"
        private val main = CoroutineScope(Dispatchers.Main)
        private val io = CoroutineScope(Dispatchers.IO)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeFragmentBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return homeFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeFragmentBinding.profileImageView.setOnClickListener {
            homeFragmentBinding.profileImageView.animate()
                .rotation(45f)
                .setDuration(190L)
                .setInterpolator(AccelerateInterpolator())
                .withEndAction {
                    homeFragmentBinding.profileImageView.animate()
                        .rotation(-45f)
                        .setInterpolator(AccelerateInterpolator())
                        .withEndAction {
                            homeFragmentBinding.profileImageView.animate()
                                .rotation(0f)
                                .setInterpolator(AccelerateInterpolator())
                                .start()
                        }
                        .start()
                }
                .start()
        }

    }

}