package com.example.emergency.ui.myPage

import android.os.Bundle
import android.view.*
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
import com.example.emergency.util.LogOut
import com.example.emergency.util.USER_NOT_EXIST
import com.example.emergency.util.showMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class MyPageFragment : BaseFragment(), CoroutineScope by MainScope() {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private val myViewModel: MyViewModel by activityViewModels()

    @Inject
    lateinit var logOut: LogOut

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.my_page_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                findNavController().navigate(R.id.action_user_to_mySettingsFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        val myPageAdapter = MyPageAdapter(myViewModel)
        with(binding) {
            button.setOnClickListener {
                launch {
                    logOut.clean()
                }
                AVUser.logOut()
                findNavController().navigate(R.id.action_user_to_loginFragment)
            }

            with(myPageRecyclerView) {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = myPageAdapter
            }

            // 用户信息
            fun setUser(name: String, phone: String) {
                userName.text = name
                userPhoneNumber.text = phone
            }
            myViewModel.user.observe(viewLifecycleOwner) {
                when {
                    it.succeeded -> {
                        setUser(it.data!!.name, it.data.phone)
                    }
                    it.message == USER_NOT_EXIST -> {
                        showMessage(requireContext(), "已登陆用户不存在")
                        launch {
                            logOut.clean()
                            AVUser.logOut()
                        }
                    }
                    else -> {
                        showMessage(requireContext(), it.message!!)
                        setUser(it.data!!.name, it.data.phone)
                    }
                }
            }

            // 呼救人列表
            myViewModel.abstractInfo.observe(viewLifecycleOwner) {
                if (!myViewModel.shouldUpdate) {
                    myViewModel.shouldUpdate = true
                    return@observe
                }
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