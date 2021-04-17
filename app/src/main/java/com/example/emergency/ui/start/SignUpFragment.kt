package com.example.emergency.ui.start

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.emergency.R
import com.example.emergency.databinding.FragmentSignUpBinding
import com.example.emergency.model.STATUS
import com.example.emergency.model.SignUpViewModel
import com.example.emergency.ui.activity.MainActivity
import com.example.emergency.util.showMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class SignUpFragment : Fragment(), CoroutineScope by MainScope() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var myCountDownTimer: CountDownTimer
    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var phone: String? = null
        var code: String? = null
        var userName: String? = null
        var pwd: String? = null

        // 创建一个计时器
        myCountDownTimer = object : CountDownTimer(
            60000,
            1000
        ) {
            @SuppressLint("SetTextI18n")
            override fun onTick(p0: Long) {
                with(binding.buttonGetCode) {
                    isEnabled = false
                    text = "${p0 / 1000}秒"
                }
            }

            override fun onFinish() {
                with(binding.buttonGetCode) {
                    text = "重新获取"
                    isEnabled = true
                }
            }
        }

        with(binding) {
            // 观察状态
            signUpViewModel.status.observe(viewLifecycleOwner) {
                when (it) {
                    STATUS.SignUp.INIT -> {
                        signUpCodeLayout.visibility = View.GONE
                        buttonGetCode.visibility = View.GONE
                        buttonNextStep.isEnabled = false
                    }
                    STATUS.SignUp.CAN_ENTER_CODE -> {
                        signUpCodeLayout.visibility = View.VISIBLE
                        buttonGetCode.visibility = View.VISIBLE
                        phone = signUpPhoneText.text.toString()
                    }
                    STATUS.SignUp.SEND_CODE_SUCCESS -> {
                        showMessage(requireContext(), "发送成功")
                        myCountDownTimer.start()
                    }
                    STATUS.SignUp.AFTER_ENTER_CODE -> {

                        code = signUpCodeText.text.toString()
                        buttonNextStep.isEnabled = true
                    }
                    STATUS.SignUp.OLD_UER_LOGIN -> {
                        showMessage(requireContext(), "已注册，登陆成功")
                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                    STATUS.SignUp.NEW_USER -> {
                        showMessage(requireContext(), "验证码正确")
                        binding.progressBar2.visibility = View.INVISIBLE
                        changeViewToSetPwd()
                    }
                    STATUS.SignUp.SAVE_USER_SUCCESS -> {
                        showMessage(requireContext(), "注册成功")
                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                    STATUS.SignUp.SIGN_UP_ERROR -> {
                        // 如果出现错误，用户需要可再次请求
                        binding.buttonNextStep.isEnabled = true
                        // 给出错误提示
                        binding.progressBar2.visibility = View.INVISIBLE
                        showMessage(requireContext(), signUpViewModel.errorMessage)
                    }
                    STATUS.SignUp.ERROR -> {
                        showMessage(requireContext(), signUpViewModel.errorMessage)
                    }
                    null -> {
                    }
                }
            }

            with(buttonNextStep) {
                setOnClickListener {
                    isEnabled = false
                    binding.progressBar2.visibility = View.VISIBLE
                    if (signUpViewModel.getStatus() == STATUS.SignUp.AFTER_ENTER_CODE) {
                        signUpViewModel.signUp(phone!!, code!!)
                    } else if (signUpViewModel.getStatus() == STATUS.SignUp.NEW_USER) {
                        signUpViewModel.saveUser(phone!!, userName!!, pwd!!)
                    }
                }
            }

            // 手机号需要 11 位
            with(signUpPhoneText) {
                doOnTextChanged { text, _, _, _ ->
                    if (text.toString().trim().length != 11) {
                        if (signUpViewModel.getStatus() != STATUS.SignUp.INIT) {
                            signUpViewModel.setStatus(STATUS.SignUp.INIT)
                        }
                    } else {
                        signUpViewModel.setStatus(STATUS.SignUp.CAN_ENTER_CODE)
                        signUpPhoneLayout.error = null
                    }
                }
                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus && text.toString().length != 11) {
                        signUpPhoneLayout
                            .error = getString(R.string.phone_number_len_not_correct_hint)
                    }
                }
            }

            // 获得验证码
            buttonGetCode.setOnClickListener {
                signUpViewModel.sendCodeForSignUp(phone!!)
            }

            // 验证验证码合法性，不得少于 6 位
            with(signUpCodeText) {
                doOnTextChanged { text, _, _, _ ->
                    if (text.toString().trim().length != 6) {
                        buttonNextStep.isEnabled = false
                    } else {
                        signUpViewModel.setStatus(STATUS.SignUp.AFTER_ENTER_CODE)
                        signUpCodeLayout.error = null
                    }

                }
                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus && text.toString().length != 6) {
                        signUpCodeLayout
                            .error = getString(R.string.code_len_not_correct_hint)
                    }
                }
            }

            val watcher = object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    userName = signUpUsernameText.text.toString().trim()
                    val userNameNotEmpty = userName!!.isNotEmpty()
                    if (userNameNotEmpty) {
                        signUpUsername.error = null
                    }
                    pwd = signUpPasswordText.text.toString().trim()
                    val pwdVerify = signUpPasswordVerifyText.text.toString().trim()
                    val passwordNotEmpty = pwd!!.isNotEmpty()
                    val passwordIsIdentical = pwd == pwdVerify
                    if (passwordIsIdentical) {
                        signUpPasswordVerifyLayout.error = null
                    } else {
                        if (pwdVerify.isNotEmpty()) {
                            signUpPasswordVerifyLayout
                                .error = getString(R.string.password_not_unanimous)
                        }

                    }
                    buttonNextStep.isEnabled =
                        userNameNotEmpty && passwordNotEmpty && passwordIsIdentical
                }

                override fun afterTextChanged(p0: Editable?) {}
            }

            signUpPasswordText.addTextChangedListener(watcher)
            signUpPasswordVerifyText.addTextChangedListener(watcher)

            with(signUpUsernameText) {
                addTextChangedListener(watcher)
                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus && text.toString().trim().isEmpty()) {
                        signUpUsername.error = "请输入用户名"
                    }
                }
            }
        }

    }

    // 设置密码界面
    private fun changeViewToSetPwd() {
        with(binding) {
            titleTextView.text = "请输入用户名和密码"
            signUpCodeLayout.visibility = View.GONE
            buttonGetCode.visibility = View.GONE
            signUpPhoneLayout.visibility = View.GONE
            signUpUsername.visibility = View.VISIBLE
            signUpPasswordLayout.visibility = View.VISIBLE
            signUpPasswordVerifyLayout.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}