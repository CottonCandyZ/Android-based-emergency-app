package com.example.emergency.ui.info

import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leancloud.AVException
import com.example.emergency.R
import com.example.emergency.databinding.FragmentInfoBinding
import com.example.emergency.model.EmergencyContact
import com.example.emergency.ui.InfoState
import com.example.emergency.ui.MyViewModel
import com.example.emergency.ui.MyViewModelFactory
import com.example.emergency.util.BaseFragment
import com.example.emergency.util.showError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 */
class InfoFragment : BaseFragment(), CoroutineScope by MainScope() {
    override var bottomNavigationViewVisibility = false
    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var myViewModel: MyViewModel
    private lateinit var editMenuItem: MenuItem
    private lateinit var deleteMenuItem: MenuItem
    private lateinit var dividerItemDecoration: DividerItemDecoration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_close_24)

        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        myViewModel = ViewModelProvider(
            requireActivity(), MyViewModelFactory(
                requireContext()
            )
        ).get(MyViewModel::class.java)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.info_menu, menu)
        val saveMenuItem = menu.findItem(R.id.save)
        editMenuItem = menu.findItem(R.id.edit)
        deleteMenuItem = menu.findItem(R.id.delete)

        myViewModel.infoState.observe(this) {
            @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
            when (it) {
                InfoState.SHOW -> {
                    saveMenuItem.isVisible = false
                    editMenuItem.isVisible = true
                    deleteMenuItem.isVisible = true
                }
                InfoState.NEW -> {
                    saveMenuItem.isVisible = true
                    editMenuItem.isVisible = false
                    deleteMenuItem.isVisible = false
                }
                InfoState.EDIT -> {
                    saveMenuItem.isVisible = true
                    deleteMenuItem.isVisible = false
                    editMenuItem.isVisible = false
                }
            }
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        fun save() {
            launch {
                item.isEnabled = false
                binding.progressBar3.visibility = View.VISIBLE
                try {
                    myViewModel.save()
                } catch (e: AVException) {
                    item.isEnabled = true
                    showError(e, requireContext())
                }
                binding.progressBar3.visibility = View.INVISIBLE

                Toast.makeText(requireContext(), "保存成功", Toast.LENGTH_SHORT).show()
                myViewModel.fromSaveInfo = true
                findNavController().navigateUp()
            }
        }
        when (item.itemId) {
            R.id.save -> {
                if (myViewModel.inputInfo[InputHint.REAL_NAME] == ""
                    || myViewModel.inputInfo[InputHint.BIRTHDATE] == ""
                    || myViewModel.inputInfo[InputHint.PHONE].length != 11
                ) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("请确认输入是否完整")
                    builder.setPositiveButton("确认") { _, _ ->
                    }
                    builder.create().show()

                } else {
                    save()
                }
            }
            R.id.edit -> {
                myViewModel.changeInfoState(InfoState.EDIT)
                myViewModel.emergencyNumber.add(EmergencyContact())
                createEditView()

            }
            R.id.delete -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("确认要删除吗？（不可恢复）")
                builder.setPositiveButton("确认") { _, _ ->
                    launch {
                        try {
                            myViewModel.deleteInfoWithEmergencyContact()
                        } catch (e: Exception) {
                            showError(e.cause!!, requireContext())
                        }
                        Toast.makeText(requireContext(), "删除成功", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                }
                builder.setNegativeButton("取消") { _, _ -> }
                builder.create().show()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)
        myViewModel.infoFragmentTitle.observe(viewLifecycleOwner) {
            (activity as AppCompatActivity).supportActionBar?.title = it
        }
        dividerItemDecoration = DividerItemDecoration(
            requireContext(),
            DividerItemDecoration.VERTICAL
        )


        when (myViewModel.infoState.value) {
            InfoState.SHOW, InfoState.EDIT -> {
                val myPageAdapter = ShowInfoAdapter(myViewModel)

                with(binding.infoRecyclerView) {
                    layoutManager = LinearLayoutManager(requireContext())
                    addItemDecoration(
                        dividerItemDecoration
                    )
                    adapter = myPageAdapter
                }
                launch {
                    try {
                        if (!myViewModel.fetchInfo(true)) {
                            // 这里是一个错误处理 暂时没有包装
                            findNavController().navigateUp()
                            Toast.makeText(requireContext(), "数据似乎已经被删除了", Toast.LENGTH_SHORT)
                                .show()
                            try {
                            } catch (e: Throwable) {
                                showError(e, requireContext())
                            }
                        }
                    } catch (e: Throwable) {
                        showError(e, requireContext())
                        myViewModel.fetchInfo(false)
                        editMenuItem.isEnabled = false
                        deleteMenuItem.isEnabled = false
                    }

                    myPageAdapter.updateDataList(myViewModel.inputInfo, myViewModel.emergencyNumber)
                }
            }
            InfoState.NEW -> {
                myViewModel.cleanup()
                createEditView()
            }
        }
    }

    private fun createEditView() {
        val spinnerLists = fun(position: Int): List<String> {
            return when (position) {
                InputHint.SEX -> myViewModel.spinnerList[0]
                InputHint.BLOOD_TYPE -> myViewModel.spinnerList[1]
                else -> myViewModel.spinnerList[2]
            }
        }

        val inputType = fun(position: Int): Int {
            return when (position) {
                InputHint.PHONE, InputHint.WEIGHT -> InputType.TYPE_CLASS_NUMBER
                in InputHint.MEDICAL_CONDITIONS..InputHint.ADDRESS ->
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                else -> InputType.TYPE_CLASS_TEXT
            }
        }

        val informationAdapter = EditInfoAdapter(
            spinnerLists,
            inputType,
            myViewModel
        )
        with(binding.infoRecyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            removeItemDecoration(dividerItemDecoration)
            adapter = informationAdapter
        }
    }
}