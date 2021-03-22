package com.example.emergency.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cn.leancloud.AVUser
import com.example.emergency.R
import com.example.emergency.databinding.FragmentMyPageBinding
import com.example.emergency.util.BaseFragment


/**
 * A simple [Fragment] subclass.
 */
class MyPageFragment : BaseFragment() {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.button.setOnClickListener {
            AVUser.logOut()
            findNavController().navigate(R.id.action_user_to_loginFragment)
        }
        val myPageAdapter = MyPageAdapter()
        binding.myPageRecyclerView.adapter = myPageAdapter
    }
}