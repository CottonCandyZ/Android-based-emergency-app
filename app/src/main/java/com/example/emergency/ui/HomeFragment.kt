package com.example.emergency.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.emergency.R
import com.example.emergency.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        val controller = navHostFragment.navController
        val barConfiguration =
            AppBarConfiguration.Builder(binding.bottomNavigationView.menu).build()
        NavigationUI.setupActionBarWithNavController(
            requireActivity() as AppCompatActivity,
            controller,
            barConfiguration
        )
        NavigationUI.setupWithNavController(binding.bottomNavigationView, controller)
        binding.bottomNavigationView
    }

}