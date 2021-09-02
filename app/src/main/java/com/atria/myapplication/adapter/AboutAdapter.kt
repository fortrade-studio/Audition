package com.atria.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.atria.myapplication.R
import com.atria.myapplication.viewModel.about.AboutFragmentViewModel
import com.atria.myapplication.viewModel.verify.VerificationFragmentViewModel

class AboutAdapter(
    val aboutFragmentViewModel: AboutFragmentViewModel
) : RecyclerView.Adapter<AboutAdapter.AboutViewHolder>() {

    inner class AboutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // details view
        val firstNameTextView = view.findViewById<TextView>(R.id.firstNameTextView)
        val lastNameTextView = view.findViewById<TextView>(R.id.lastNameTextView)
        val phoneTextView = view.findViewById<TextView>(R.id.phoneNumberTextView)
        val dobTextView  = view.findViewById<TextView>(R.id.dobTextView)
        val genderTextView = view.findViewById<TextView>(R.id.genderTextView)
        val cityTextView = view.findViewById<TextView>(R.id.cityTextView)
        val emailTextView = view.findViewById<TextView>(R.id.emailEditText)

        // residence view
        val stateTextView = view.findViewById<TextView>(R.id.stateTextView)
        val countryTextView = view.findViewById<TextView>(R.id.countryTextView)

        // career view
        val artistTypeTextView = view.findViewById<TextView>(R.id.artistTypeTextView)
        val enthnicityTextView = view.findViewById<TextView>(R.id.ethnicityTextView)
        val languagesTextView= view.findViewById<TextView>(R.id.languageTextView)
        val preferenceTextView = view.findViewById<TextView>(R.id.preferenceTextView)
        val experienceTextView = view.findViewById<TextView>(R.id.experienceTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutViewHolder {
        return if (viewType == 0) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.details_view, parent, false)
            AboutViewHolder(view)
        } else if (viewType == 1) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.residence_view, parent, false)
            AboutViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.career_view, parent, false)
            AboutViewHolder(view)
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return 0;
        } else if (position == 1) {
            return 1;
        }
        return 3;
    }

    override fun onBindViewHolder(holder: AboutViewHolder, position: Int) {
        if (position == 0) {
            // this is the details fragment
            aboutFragmentViewModel.getPersonalData {
                holder.firstNameTextView.text = it.name.split(" ")[0]
                if (it.name.split(" ").size>1) {
                    holder.lastNameTextView.text = it.name.split(" ")[1]
                }
                holder.phoneTextView.text = it.phNumber
                holder.dobTextView.text = it.date
                holder.genderTextView.text = it.gender
                holder.cityTextView.text = it.city
                holder.emailTextView.text = it.email
            }
        } else if (position == 1) {

            aboutFragmentViewModel.getPersonalData {
                holder.countryTextView.text = it.country
                holder.stateTextView.text = it.state
                holder.cityTextView.text = it.city
            }

        } else {
            aboutFragmentViewModel.getPersonalData {
                holder.artistTypeTextView.text = it.artistType
                holder.enthnicityTextView.text = it.ethnicity
                holder.languagesTextView.text = it.languages
                holder.preferenceTextView.text = it.preferences
                holder.experienceTextView.text = it.experience
            }
        }
    }

    override fun getItemCount(): Int {
        return 3;
    }


}