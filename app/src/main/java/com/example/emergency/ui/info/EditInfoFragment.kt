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
import com.example.emergency.databinding.FragmentEditInfoBinding
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
class EditInfoFragment : BaseFragment(), CoroutineScope by MainScope() {
    override var bottomNavigationViewVisibility = false
    private var _binding: FragmentEditInfoBinding? = null
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
        _binding = FragmentEditInfoBinding.inflate(inflater, container, false)
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
        with(binding) {
            with(infoRecyclerView) {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = informationAdapter
            }
        }
    }


}