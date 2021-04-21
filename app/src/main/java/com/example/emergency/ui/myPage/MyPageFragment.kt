package com.example.emergency.ui.myPage

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emergency.R
import com.example.emergency.databinding.FragmentMyPageBinding
import com.example.emergency.model.MyPageViewModel
import com.example.emergency.model.STATUS
import com.example.emergency.ui.activity.LoginActivity
import com.example.emergency.util.BaseFragment
import com.example.emergency.util.showMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class MyPageFragment : BaseFragment(), CoroutineScope by MainScope() {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private val myPageViewModel: MyPageViewModel by viewModels()

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
        val myPageAdapter = MyPageAdapter(myPageViewModel)
        with(binding) {
            myPageViewModel.status.observe(viewLifecycleOwner) {
                when (it) {
                    STATUS.MyPage.USER_NOT_FOUND -> {
                        showMessage(requireContext(), "已登陆用户不存在")
                        startActivity(Intent(requireActivity(), LoginActivity::class.java))
                        requireActivity().finish()
                    }
                    STATUS.MyPage.REFRESH_COMPLETE -> {
                        swipeRefresh.isRefreshing = false
                    }
                    STATUS.MyPage.REFRESH_ERROR -> {
                        swipeRefresh.isRefreshing = false
                        showMessage(requireContext(), myPageViewModel.errorMessage)
                    }
                    STATUS.MyPage.CHOSEN_ERROR -> {
                        showMessage(
                            requireContext(), "${myPageViewModel.errorMessage}\n" +
                                    "请尝试刷新后再试"
                        )
                    }
                    null -> {
                    }
                }
            }
            with(myPageRecyclerView) {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = myPageAdapter
            }

            myPageViewModel.user.observe(viewLifecycleOwner) {
                userName.text = it.name
                userPhoneNumber.text = it.phone
            }

            // 呼救人列表
            myPageViewModel.abstractInfoList.observe(viewLifecycleOwner) {
                myPageAdapter.submitList(it)
            }

            addInformation.setOnClickListener {
                Bundle().apply {
                    putSerializable("INFO_STATUS", STATUS.Info.NEW)
                    findNavController().navigate(R.id.action_user_to_informationFragment, this)
                }
            }

            myPageViewModel.currentChosen.observe(viewLifecycleOwner) {
                emergencyChosen.text = it
            }


            swipeRefresh.setOnRefreshListener {
                myPageViewModel.refreshInfo()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}