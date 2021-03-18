package com.example.emergency.ui

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.emergency.R
import com.example.emergency.databinding.FragmentInformationBinding


/**
 * A simple [Fragment] subclass.
 */
class InformationFragment : Fragment() {
    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!
    private lateinit var topBarButton: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.info_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.skip -> {
                findNavController().navigate(R.id.action_informationFragment_to_homeFragment)
            }
            R.id.save -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        // test
        val list: ArrayList<View> = ArrayList()
        val addNumber =
            layoutInflater.inflate(R.layout.add_emergency_person, binding.emergencyPerson, false)
        list.add(addNumber)
        binding.emergencyPerson.addView(addNumber)


        // 创建两个下拉选框
        createSpinner(binding.sexTextView, listOf("男", "女"))
        createSpinner(
            binding.bloodTypeTextView,
            listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        )


    }

    private fun createSpinner(view: AutoCompleteTextView, list: List<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, list)
        view.setAdapter(adapter)
    }
}