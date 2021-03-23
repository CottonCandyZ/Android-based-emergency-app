package com.example.emergency.ui

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        // test
//        val addNumber =
//            layoutInflater.inflate(R.layout.add_emergency_person, binding.emergencyPerson, false)
//        list.add(addNumber)
//        binding.emergencyPerson.addView(addNumber)


        // 创建两个下拉选框
//        createSpinner(binding.sexTextView, listOf("男", "女"))
//        createSpinner(
//            binding.bloodTypeTextView,
//            listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
//        )


        val inputHints = arrayOf(
            getString(R.string.info_add_real_name_hint),
            getString(R.string.info_add_sex_hint),
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

        val informationAdapter = InformationAdapter(inputHints)
        with(binding) {
            with(infoRecyclerView) {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = informationAdapter
            }
        }


    }

    private fun createSpinner(view: AutoCompleteTextView, list: List<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, list)
        view.setAdapter(adapter)
    }
}