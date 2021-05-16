package com.example.emergency.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.emergency.R
import com.example.emergency.databinding.FragmentEmergencyBinding
import com.example.emergency.model.EmergencyViewModel
import com.example.emergency.model.STATE
import com.example.emergency.util.BaseFragment
import com.example.emergency.util.showMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class EmergencyFragment : BaseFragment(), CoroutineScope by MainScope() {
    private var _binding: FragmentEmergencyBinding? = null
    private val binding get() = _binding!!


    private val emergencyViewModel: EmergencyViewModel by activityViewModels()

    private var permissionResult = true

    // 初始化
    private val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                showMessage(requireContext(), "请给予位置权限以使用紧急呼救")
                permissionResult = false
            }
        }
    private val requestCallPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                showMessage(requireContext(), "请给予电话权限以使用紧急呼救")
                permissionResult = false
            }
        }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.emergency_menu, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmergencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cancel -> {
                emergencyViewModel.setState(STATE.Call.CANCEL)
                setHasOptionsMenu(false)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkLocationPermission()

        with(binding) {
            buttonEmergency.setOnClickListener {
                if (!checkLocationPermission() || !checkCallPermission() || emergencyViewModel.getState() != STATE.Call.INIT) {
                    return@setOnClickListener
                }
                if (emergencyViewModel.getState() == STATE.Call.ERROR) {
                    showMessage(requireContext(), "请检查网络连接")
                }
                emergencyViewModel.setState(STATE.Call.CALLING)
            }
            emergencyViewModel.currentText.observe(viewLifecycleOwner) {
                emergencyHint.text = it
            }


            emergencyViewModel.state.observe(viewLifecycleOwner) {
                if (it == STATE.Call.CALLING) {
                    setHasOptionsMenu(true)
                } else {
                    setHasOptionsMenu(false)
                }
                if (it == STATE.Call.ERROR) {
                    showMessage(requireContext(), emergencyViewModel.errorMessage)
                }
                if (it == STATE.Call.COMPLETE) {
                    val callIntent =
                        Intent(
                            Intent.ACTION_CALL,
                            Uri.parse("tel:" + emergencyViewModel.handlerPhone)
                        )
                    startActivity(callIntent)
                }
            }
        }
    }


    // 权限请求
    private fun checkLocationPermission(): Boolean {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("需要位置权限以紧急呼救")
                builder.setPositiveButton("确认") { _, _ ->
                }
                builder.create().show()
            }
            else -> {
                requestLocationPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
        return permissionResult
    }

    private fun checkCallPermission(): Boolean {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE) -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("需要电话权限以紧急呼救")
                builder.setPositiveButton("确认") { _, _ ->
                }
                builder.create().show()
            }
            else -> {
                requestCallPermissionLauncher.launch(
                    Manifest.permission.CALL_PHONE
                )
            }
        }
        return permissionResult
    }
}