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
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leancloud.AVException
import com.example.emergency.R
import com.example.emergency.WebService
import com.example.emergency.data.AppDatabase
import com.example.emergency.data.InfoRepository
import com.example.emergency.databinding.FragmentInformationBinding
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
    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!

    //    private val myViewModel: MyViewModel by viewModels {
//        MyViewModelFactory(
//            InfoRepository(
//                AppDatabase.getInstance(requireContext()).infoDao(),
//                WebService()
//            )
//        )
//    }
    private lateinit var myViewModel: MyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_close_24)
        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        myViewModel = ViewModelProvider(
            requireActivity(), MyViewModelFactory(
                InfoRepository(
                    AppDatabase.getInstance(requireContext()).infoDao(),
                    WebService()
                ),
                requireContext()
            )
        ).get(MyViewModel::class.java)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.info_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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


            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        myViewModel.cleanup()

        val inputHints = arrayOf(
            getString(R.string.info_add_real_name_hint),
            getString(R.string.info_add_sex_hint),
//            getString(R.string.info_relationship),
            getString(R.string.info_add_birth_hint),
            getString(R.string.info_add_phone_hint),
            getString(R.string.info_add_weight_hint),
            getString(R.string.info_add_blood_type_hint),
            getString(R.string.info_add_medical_conditions_hint),
            getString(R.string.info_add_medical_notes_hint),
            getString(R.string.info_add_allergy_hint),
            getString(R.string.info_add_medications_hint),
            getString(R.string.info_add_address_hint)
        )

        val spinnerList = arrayOf(
            listOf("男", "女"),
            listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"),
            listOf(
                "家人",
                "母亲",
                "父亲",
                "父母",
                "兄弟",
                "姐妹",
                "儿子",
                "女儿",
                "子女",
                "朋友",
                "配偶",
                "伴侣",
                "助理",
                "上司",
                "医生",
                "紧急联系人",
                "家庭成员",
                "老师",
                "看护",
                "监护人",
                "社会工作者",
                "学校",
                "托儿所"
            )
        )

        val spinnerLists = fun(position: Int): List<String> {
            return when (position) {
                InputHint.SEX -> spinnerList[0]
                InputHint.BLOOD_TYPE -> spinnerList[1]
                else -> spinnerList[2]
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


        val informationAdapter = InfoAdapter(
            inputHints,
            spinnerLists,
            inputType,
            myViewModel
        )
        with(binding) {
            with(infoRecyclerView) {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = informationAdapter
            }
        }
    }


}