package com.example.emergency.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.emergency.MainActivity

abstract class BaseFragment : Fragment() {
    protected open var bottomNavigationViewVisibility = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setBottomNavigationVisibility(bottomNavigationViewVisibility)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setBottomNavigationVisibility(bottomNavigationViewVisibility)
    }
}