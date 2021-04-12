package com.example.emergency.ui.info

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leancloud.AVException
import com.example.emergency.R
import com.example.emergency.data.succeeded
import com.example.emergency.databinding.FragmentInfoBinding
import com.example.emergency.ui.InfoState
import com.example.emergency.ui.MyViewModel
import com.example.emergency.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class InfoFragment : BaseFragment(), CoroutineScope by MainScope() {
    override var bottomNavigationViewVisibility = false

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    private val myViewModel: MyViewModel by activityViewModels()

    private lateinit var editMenuItem: MenuItem
    private lateinit var deleteMenuItem: MenuItem
    private lateinit var dividerItemDecoration: DividerItemDecoration

    @Inject
    lateinit var hints: Hints


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_close_24)
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
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
                    showMessage(requireContext(), getErrorMessage(e))
                }
                binding.progressBar3.visibility = View.INVISIBLE
                showMessage(requireContext(), "保存成功")
                myViewModel.fetchAbstractInfo(true)
                findNavController().navigateUp()
            }
        }
        when (item.itemId) {
            R.id.save -> {
                if (myViewModel.inputData.inputInfo[InputHint.REAL_NAME] == ""
                    || myViewModel.inputData.inputInfo[InputHint.BIRTHDATE] == ""
                    || myViewModel.inputData.inputInfo[InputHint.PHONE].length != 11
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
                            showMessage(requireContext(), getErrorMessage(e))
                        }
                        showMessage(requireContext(), "删除成功")
                        findNavController().navigateUp()
                    }
                }
                builder.setNegativeButton("取消") { _, _ -> }
                builder.create().show()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        myViewModel.infoFragmentTitle.observe(viewLifecycleOwner) {
            (activity as AppCompatActivity).supportActionBar?.title = it
        }
        dividerItemDecoration = DividerItemDecoration(
            requireContext(),
            DividerItemDecoration.VERTICAL
        )




        @SuppressLint("NotifyDataSetChanged")
        when (myViewModel.infoState.value) {
            InfoState.SHOW, InfoState.EDIT -> {
                val showInfoAdapter = ShowInfoAdapter(myViewModel, hints.inputHints)
                with(binding.infoRecyclerView) {
                    layoutManager = LinearLayoutManager(requireContext())
                    addItemDecoration(
                        dividerItemDecoration
                    )
                    adapter = showInfoAdapter
                }
                myViewModel.showInfo.observe(viewLifecycleOwner) {
                    when {
                        it.succeeded -> {
                            showInfoAdapter.updateDataList(
                                it.data!!.inputInfo,
                                it.data.emergencyNumber
                            )
                        }
                        it.message == ID_NOT_FOUND_ERROR -> {
                            findNavController().navigateUp()
                            showMessage(requireContext(), "数据似乎已经被删除了")
                        }
                        else -> {
                            showMessage(requireContext(), it.message!!)
                            showInfoAdapter.updateDataList(
                                it.data!!.inputInfo,
                                it.data.emergencyNumber
                            )
                        }
                    }
                }
                launch {
                    myViewModel.fetchInfo()
                }
            }
            InfoState.NEW -> {
                createEditView()
            }
        }
    }

    private fun createEditView() {
        val spinnerLists: (Int) -> List<String> = { position ->
            when (position) {
                InputHint.SEX -> hints.spinnerList[0]
                InputHint.BLOOD_TYPE -> hints.spinnerList[1]
                else -> hints.spinnerList[2]
            }
        }

        val inputType: (Int) -> Int = { position ->
            when (position) {
                InputHint.PHONE, InputHint.WEIGHT -> InputType.TYPE_CLASS_NUMBER
                in InputHint.MEDICAL_CONDITIONS..InputHint.ADDRESS ->
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                else -> InputType.TYPE_CLASS_TEXT
            }
        }

        val icon: (Int) -> Int = { position ->
            when (position) {
                InputHint.REAL_NAME -> R.drawable.ic_phone_icon_24
                InputHint.SEX -> R.drawable.ic_gender_icon
                InputHint.BIRTHDATE -> R.drawable.ic_birthdate_icon
                InputHint.PHONE -> R.drawable.ic_baseline_phone_24
                InputHint.WEIGHT -> R.drawable.ic_weight_icon
                InputHint.BLOOD_TYPE -> R.drawable.ic_blood_type_icon
                InputHint.MEDICAL_CONDITIONS -> R.drawable.ic_medical_conditions_24
                InputHint.MEDICAL_NOTES -> R.drawable.ic_medical_notes_icon
                InputHint.ALLERGY -> R.drawable.ic_allergy_icon
                InputHint.MEDICATIONS -> R.drawable.ic_pill_icon_24
                InputHint.ADDRESS -> R.drawable.ic_baseline_home_24
                else -> -1
            }
        }

        val informationAdapter = EditInfoAdapter(
            spinnerLists,
            inputType,
            icon,
            hints.inputHints,
            myViewModel
        )
        with(binding.infoRecyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            removeItemDecoration(dividerItemDecoration)
            adapter = informationAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}