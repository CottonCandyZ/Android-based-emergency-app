package com.example.emergency.ui.myPage

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leancloud.AVUser
import com.example.emergency.R
import com.example.emergency.WebService
import com.example.emergency.data.AppDatabase
import com.example.emergency.data.InfoRepository
import com.example.emergency.databinding.FragmentMyPageBinding
import com.example.emergency.ui.MyViewModel
import com.example.emergency.ui.MyViewModelFactory
import com.example.emergency.util.BaseFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 */
@ExperimentalCoroutinesApi
class MyPageFragment : BaseFragment(), CoroutineScope by MainScope() {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var myViewModel: MyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        myViewModel = ViewModelProvider(
            requireActivity(), MyViewModelFactory(
                InfoRepository(
                    AppDatabase.getInstance(requireContext()).infoDao(),
                    WebService()
                )
            )
        ).get(MyViewModel::class.java)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (myViewModel.fromSaveInfo) {
            launch {
                myViewModel.fetch(true)
            }

            myViewModel.fromSaveInfo = false
        } else {
            launch {
                myViewModel.fetch(false)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        with(binding) {
            button.setOnClickListener {
                AVUser.logOut()
                findNavController().navigate(R.id.action_user_to_loginFragment)
            }
            val myPageAdapter = MyPageAdapter()
            with(myPageRecyclerView) {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = myPageAdapter
            }
            myViewModel.abstractInfo.observe(viewLifecycleOwner, {
                myPageAdapter.updateDataList(it)
                myPageAdapter.notifyDataSetChanged()
            })

            addInformation.setOnClickListener {
                findNavController().navigate(R.id.action_user_to_informationFragment)
            }
            swipRefresh.setOnRefreshListener {
                launch {
                    myViewModel.fetch(true)
                    swipRefresh.isRefreshing = false
                }

            }
        }


    }
}