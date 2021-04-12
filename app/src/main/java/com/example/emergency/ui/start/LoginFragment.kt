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
import androidx.navigation.fragment.findNavController
import cn.leancloud.AVUser
import com.example.emergency.R
import com.example.emergency.databinding.FragmentLoginBinding
import com.example.emergency.util.BaseFragment
import com.example.emergency.util.getErrorMessage
import com.example.emergency.util.showMessage
import io.reactivex.Observer
import io.reactivex.disposables.Disposable


/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : BaseFragment() {

    override var bottomNavigationViewVisibility = false

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
            buttonLogin.setOnClickListener {
                progressBar.visibility = VISIBLE
                buttonLogin.isEnabled = false
                val phone = loginPhoneNumberText.text.toString().trim()
                val pwd = loginPasswordText.text.toString().trim()

                AVUser.logIn("+86$phone", pwd)
                    .subscribe(object : Observer<AVUser> {
                        override fun onSubscribe(d: Disposable) {}

                        override fun onNext(t: AVUser) {
                            showMessage(requireContext(), "欢迎回来")
                            findNavController().navigate(R.id.action_loginFragment_to_emergency)
                        }

                        override fun onError(e: Throwable) {
                            progressBar.visibility = View.INVISIBLE
                            buttonLogin.isEnabled = true
                            showMessage(requireContext(), getErrorMessage(e))
                        }

                        override fun onComplete() {}
                    })

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