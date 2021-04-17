package com.example.emergency.ui.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.emergency.R
import com.example.emergency.data.remote.SignUpService
import com.example.emergency.databinding.FragmentChangePasswordBinding
import com.example.emergency.util.BaseFragment
import com.example.emergency.util.getErrorMessage
import com.example.emergency.util.showMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class ChangePasswordFragment : BaseFragment(), CoroutineScope by MainScope() {
    override var bottomNavigationViewVisibility = false
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var signUpService: SignUpService


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            saveButton.isEnabled = false
            val watcher = object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val pwd = newPasswordText.text.toString().trim()
                    val pwdVerify = newPasswordVerifyText.text.toString().trim()
                    val passwordNotEmpty = pwd.isNotEmpty()
                    val passwordIsIdentical = pwd == pwdVerify
                    if (passwordIsIdentical) {
                        newPasswordVerify.error = null
                    } else {
                        if (pwdVerify.isNotEmpty()) {
                            newPasswordVerify
                                .error = getString(R.string.password_not_unanimous)
                        }

                    }
                    saveButton.isEnabled = passwordNotEmpty && passwordIsIdentical
                }

                override fun afterTextChanged(p0: Editable?) {}
            }

            newPasswordText.addTextChangedListener(watcher)
            newPasswordVerifyText.addTextChangedListener(watcher)

            saveButton.setOnClickListener {
                progressBar4.visibility = View.VISIBLE
                saveButton.isEnabled = false

                launch {
                    try {
                        signUpService.setUserPassword(
                            binding.newPasswordText.text.toString().trim()
                        )
                    } catch (e: Exception) {
                        progressBar4.visibility = View.INVISIBLE
                        showMessage(requireContext(), getErrorMessage(e))
                        saveButton.isEnabled = true
                        return@launch
                    }
                    showMessage(requireContext(), "密码已保存")
                    findNavController().navigateUp()
                }
            }
        }
    }


}