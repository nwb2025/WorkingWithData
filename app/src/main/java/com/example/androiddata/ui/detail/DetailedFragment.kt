package com.example.androiddata.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.androiddata.LOG_TAG
import com.example.androiddata.R
import com.example.androiddata.databinding.FragmentDetailedBinding
import com.example.androiddata.ui.shared.SharedViewModel

class DetailedFragment : Fragment() {

    private lateinit var viewModel:SharedViewModel
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        setHasOptionsMenu(true) // to enable fragment to listen to clicks on app bar
        navController = Navigation.findNavController(
            requireActivity(), R.id.nav_host
        )

        viewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel::class.java)
        // another way to get data from another fragment is using shared viewmodel
        viewModel.selectedMonster.observe(this, Observer {
            Log.i(LOG_TAG,"Selected monster: ${it.description}")
        })
        // for databinding a new class is generated
        val binding = FragmentDetailedBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item?.itemId == android.R.id.home){
            navController.navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

}