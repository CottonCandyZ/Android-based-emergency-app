package com.example.emergency.ui.info

import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emergency.R
import com.example.emergency.databinding.FragmentInfoBinding
import com.example.emergency.model.InfoViewModel
import com.example.emergency.model.STATUS
import com.example.emergency.util.BaseFragment
import com.example.emergency.util.Hints
import com.example.emergency.util.showMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class InfoFragment : BaseFragment(), CoroutineScope by MainScope() {
    override var bottomNavigationViewVisibility = false

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    private val infoViewModel: InfoViewModel by viewModels()

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
        val editMenuItem = menu.findItem(R.id.edit)
        val deleteMenuItem = menu.findItem(R.id.delete)

        infoViewModel.status.observe(this) {
            when (it) {
                STATUS.Info.SHOW -> {
                    saveMenuItem.isVisible = false
                    editMenuItem.isVisible = true
                    deleteMenuItem.isVisible = true
                }
                STATUS.Info.NEW -> {
                    saveMenuItem.isVisible = true
                    editMenuItem.isVisible = false
                    deleteMenuItem.isVisible = false
                }
                STATUS.Info.EDIT -> {
                    saveMenuItem.isVisible = true
                    deleteMenuItem.isVisible = false
                    editMenuItem.isVisible = false
                }
                STATUS.Info.SAVE_ERROR -> {
                    binding.progressBar3.visibility = View.INVISIBLE
                    saveMenuItem.isEnabled = true
                    showMessage(requireContext(), infoViewModel.errorMessage)
                }
                STATUS.Info.SAVE_SUCCESS -> {
                    showMessage(requireContext(), "保存成功")
                    findNavController().navigateUp()
                }
                STATUS.Info.DELETE_SUCCESS -> {
                    showMessage(requireContext(), "删除成功")
                    findNavController().navigateUp()
                }


                null -> {
                }
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                if (infoViewModel.inputData.inputInfo[InputHint.REAL_NAME] == ""
                    || infoViewModel.inputData.inputInfo[InputHint.BIRTHDATE] == ""
                    || infoViewModel.inputData.inputInfo[InputHint.PHONE].length != 11
                ) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("请确认输入是否完整")
                    builder.setPositiveButton("确认") { _, _ ->
                    }
                    builder.create().show()
                } else {
                    item.isEnabled = false
                    binding.progressBar3.visibility = View.VISIBLE
                    infoViewModel.save()
                }
            }

            R.id.edit -> {
                infoViewModel.setStatus(STATUS.Info.EDIT)
                createEditView()
            }

            R.id.delete -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("确认要删除吗？（不可恢复）")
                builder.setPositiveButton("确认") { _, _ ->
                    infoViewModel.deleteInfoWithEmergencyContact()
                }
                builder.setNegativeButton("取消") { _, _ -> }
                builder.create().show()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        infoViewModel.setStatus(arguments?.get("INFO_STATUS") as STATUS.Info)
        setHasOptionsMenu(true)
        infoViewModel.infoFragmentTitle.observe(viewLifecycleOwner) {
            (activity as AppCompatActivity).supportActionBar?.title = it
        }


        dividerItemDecoration = DividerItemDecoration(
            requireContext(),
            DividerItemDecoration.VERTICAL
        )




        when (infoViewModel.getStatus()) {
            STATUS.Info.SHOW -> {
                infoViewModel.setInfoFragmentTitle(arguments?.getString("INFO_NAME")!! + "的信息")
                val showInfoAdapter = ShowInfoAdapter(hints.inputHints)
                with(binding.infoRecyclerView) {
                    layoutManager = LinearLayoutManager(requireContext())
                    addItemDecoration(
                        dividerItemDecoration
                    )
                    adapter = showInfoAdapter
                }
                infoViewModel.showInfo.observe(viewLifecycleOwner) {
                    showInfoAdapter.updateDataList(
                        it.inputInfo,
                        it.emergencyNumber
                    )
                }
                infoViewModel.fetchInfo(arguments?.getString("INFO_ID")!!)
            }
            STATUS.Info.NEW -> {
                infoViewModel.setInfoFragmentTitle("添加新的呼救人")
                createEditView()
            }
            else -> {
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
            infoViewModel
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