package com.example.emergency.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.emergency.R
import com.example.emergency.databinding.FragmentEmergencyBinding

/**
 * A simple [Fragment] subclass.
 */
class EmergencyFragment : Fragment() {
    private var _binding: FragmentEmergencyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.emergency_menu, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmergencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cancel -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        binding.buttonEmergency.setOnClickListener {
            setHasOptionsMenu(true)
        }
    }
}