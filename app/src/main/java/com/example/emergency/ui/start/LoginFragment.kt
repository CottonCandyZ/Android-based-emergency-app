package com.example.emergency.ui.start

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cn.leancloud.AVUser
import com.example.emergency.R
import com.example.emergency.databinding.FragmentLoginBinding
import com.example.emergency.model.LoginViewModel
import com.example.emergency.model.STATUS
import com.example.emergency.util.BaseFragment
import com.example.emergency.util.showMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class LoginFragment : BaseFragment(), CoroutineScope by MainScope() {

    override var bottomNavigationViewVisibility = false

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (AVUser.getCurrentUser() != null) {
            findNavController().navigate(R.id.action_loginFragment_to_emergency)
        }
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            buttonSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
            }
            progressBar.visibility = View.INVISIBLE
            buttonLogin.isEnabled = false
            with(loginPhoneNumberText) {
                addTextChangedListener(watcher)
                onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        if (text.toString().length != 11)
                            binding.loginPhoneLayout
                                .error = getString(R.string.phone_number_len_not_correct_hint)
                    }
                }
            }
            loginPasswordText.addTextChangedListener(watcher)
            loginViewModel.status.observe(viewLifecycleOwner) {
                when (it) {
                    STATUS.Login.SUCCESS -> {
                        showMessage(requireContext(), "欢迎回来")
                        findNavController().navigate(R.id.action_loginFragment_to_emergency)
                    }
                    STATUS.Login.ERROR -> {
                        progressBar.visibility = View.INVISIBLE
                        buttonLogin.isEnabled = true
                        showMessage(requireContext(), loginViewModel.errorMessage)
                    }
                    null -> {
                    }
                }
            }
            buttonLogin.setOnClickListener {
                progressBar.visibility = VISIBLE
                buttonLogin.isEnabled = false
                val phone = loginPhoneNumberText.text.toString().trim()
                val pwd = loginPasswordText.text.toString().trim()
                loginViewModel.login(phone, pwd)
            }
        }
    }

    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val t1 = binding.loginPhoneNumberText.text.toString().trim().length == 11
            if (t1) {
                binding.loginPhoneLayout.error = null
            }
            val t2 = binding.loginPasswordText.text.toString().isNotEmpty()
            binding.buttonLogin.isEnabled = t1 and t2
        }

        override fun afterTextChanged(p0: Editable?) {}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}