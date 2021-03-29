package com.example.emergency.ui

import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emergency.R
import com.example.emergency.databinding.FragmentInformationBinding
import com.example.emergency.util.BaseFragment


/**
 * A simple [Fragment] subclass.
 */
class InformationFragment : BaseFragment() {
    override var bottomNavigationViewVisibility = false
    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!
    private val dataInput: ArrayList<String> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_close_24)
        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.info_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                Toast.makeText(requireContext(), "保存成功", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        repeat(11) {
            dataInput.add("")
        }


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
                "本人",
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
                InputHint.SEX.ordinal -> spinnerList[0]
                InputHint.BLOOD_TYPE.ordinal -> spinnerList[1]
                else -> spinnerList[2]
            }
        }

        val inputType = fun(position: Int): Int {
            return when (position) {
                InputHint.PHONE.ordinal, InputHint.WEIGHT.ordinal -> InputType.TYPE_CLASS_NUMBER
                in InputHint.MEDICAL_CONDITIONS.ordinal..InputHint.ADDRESS.ordinal ->
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                else -> InputType.TYPE_CLASS_TEXT
            }
        }


        val informationAdapter = InformationAdapter(
            inputHints,
            spinnerLists,
            inputType,
            dataInput
        )
        with(binding) {
            with(infoRecyclerView) {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = informationAdapter
            }
        }
    }


}