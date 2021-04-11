package com.example.emergency.ui.myPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leancloud.AVUser
import com.example.emergency.R
import com.example.emergency.data.succeeded
import com.example.emergency.databinding.FragmentMyPageBinding
import com.example.emergency.ui.InfoState
import com.example.emergency.ui.MyViewModel
import com.example.emergency.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class MyPageFragment : BaseFragment(), CoroutineScope by MainScope() {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    private val myViewModel: MyViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (myViewModel.fromSaveInfo) {
            launch {
                myViewModel.fetchAbstractInfo(true)
            }
            myViewModel.fromSaveInfo = false
        } else {
            launch {
                myViewModel.fetchAbstractInfo(false)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myPageAdapter = MyPageAdapter(myViewModel)
        with(binding) {
            button.setOnClickListener {
                AVUser.logOut()
                findNavController().navigate(R.id.action_user_to_loginFragment)
            }
            with(myPageRecyclerView) {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = myPageAdapter
            }
            myViewModel.abstractInfo.observe(viewLifecycleOwner) {
                if (it.succeeded) {
                    myPageAdapter.submitList(it.data)
                } else {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

            }

            addInformation.setOnClickListener {
                myViewModel.changeInfoTitle("添加呼救人信息")
                myViewModel.changeInfoState(InfoState.NEW)
                findNavController().navigate(R.id.action_user_to_informationFragment)
            }
            swipRefresh.setOnRefreshListener {
                launch {
                    myViewModel.fetchAbstractInfo(true)
                    swipRefresh.isRefreshing = false
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}