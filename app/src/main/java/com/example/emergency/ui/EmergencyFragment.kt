package com.example.emergency.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cn.leancloud.AVUser
import com.example.emergency.R
import com.example.emergency.databinding.FragmentEmergencyBinding
import com.example.emergency.util.BaseFragment
import kotlinx.coroutines.*

/**
 * A simple [Fragment] subclass.
 */
class EmergencyFragment : BaseFragment(), CoroutineScope by MainScope() {
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
        if (AVUser.getCurrentUser() == null) {
            findNavController().navigate(R.id.action_emergency_to_loginFragment)
        }
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
            launch {
                wait()
                setHasOptionsMenu(true)
            }
        }
    }

    private suspend fun wait() = withContext(Dispatchers.IO) {
        Thread.sleep(5000)
    }
}