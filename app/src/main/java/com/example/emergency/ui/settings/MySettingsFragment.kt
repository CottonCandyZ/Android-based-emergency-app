package com.example.emergency.ui.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import cn.leancloud.AVUser
import com.example.emergency.R
import com.example.emergency.ui.main.MainActivity
import com.example.emergency.util.LogOut
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MySettingsFragment : PreferenceFragmentCompat(), CoroutineScope by MainScope() {
    @Inject
    lateinit var logOut: LogOut
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        (activity as MainActivity).setBottomNavigationVisibility(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findPreference<Preference>("logOut")!!
            .onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("确认要登出吗？")
            builder.setPositiveButton("确认") { _, _ ->
                launch {
                    logOut.clean()
                }
                AVUser.logOut()
                findNavController().navigate(R.id.action_mySettingsFragment_to_loginFragment)
            }
            builder.setNegativeButton("取消") { _, _ -> }
            builder.create().show()
            return@OnPreferenceClickListener true
        }
        findPreference<Preference>("changePassword")!!
            .onPreferenceClickListener = Preference.OnPreferenceClickListener {
            findNavController().navigate(R.id.action_mySettingsFragment_to_changePasswordFragment)
            return@OnPreferenceClickListener true
        }
    }

}