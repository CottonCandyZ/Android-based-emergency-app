package com.example.emergency.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.emergency.R
import com.example.emergency.data.remote.WebService
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
    lateinit var webService: WebService


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
            with(newPasswordVerifyText) {
                doOnTextChanged { text, _, _, _ ->
                    if (text.toString().trim() != binding.newPasswordText.text.toString()
                            .trim()
                    ) {
                        saveButton.isEnabled = false
                    } else {
                        saveButton.isEnabled = true
                        newPasswordVerify.error = null
                    }

                }
                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus && text.toString()
                            .trim() != binding.newPasswordText.text.toString()
                            .trim()
                    ) {
                        newPasswordVerify
                            .error = getString(R.string.password_not_unanimous)
                    }
                }
            }
            saveButton.setOnClickListener {
                progressBar4.visibility = View.VISIBLE
                saveButton.isEnabled = false

                launch {
                    try {
                        webService.setUserPassword(binding.newPasswordText.text.toString().trim())
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