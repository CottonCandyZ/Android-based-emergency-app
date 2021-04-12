package com.example.emergency.ui.start

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import cn.leancloud.sms.AVSMS
import cn.leancloud.sms.AVSMSOption
import com.example.emergency.R
import com.example.emergency.databinding.FragmentSignUpBinding
import com.example.emergency.util.BaseFragment
import com.example.emergency.util.getErrorMessage
import com.example.emergency.util.showMessage
import kotlinx.coroutines.*


/**
 * A simple [Fragment] subclass.
 */
class SignUpFragment : BaseFragment(), CoroutineScope by MainScope() {
    override var bottomNavigationViewVisibility = false

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var myCountDownTimer: CountDownTimer
    private var step = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            // 下一步由一个变量记录阶段，在某一个阶段完成时，则 step + 1
            with(buttonNextStep) {
                isEnabled = false
                var phone = ""
                setOnClickListener {
                    when (step) {
                        0 -> {
                            signUpCodeLayout.visibility = View.VISIBLE
                            buttonGetCode.visibility = View.VISIBLE
                            isEnabled = false
                            step++
                        }
                        1 -> {
                            binding.progressBar2.visibility = View.VISIBLE
                            phone = signUpPhoneText.text.toString().trim()
                            val code = signUpCodeText.text.toString().trim()
                            isEnabled = false
                            launch {
                                try {
                                    if (judgeUserIfExist(phone)) { // 若是老用户
                                        checkCodeToSignUpOrLogin(phone, code)
                                        showMessage(requireContext(), "已注册，登陆成功")
                                        findNavController().navigate(R.id.action_signUpFragment_to_emergency)
                                    } else { // 新用户
                                        checkCodeToSignUpOrLogin(phone, code)
                                        showMessage(requireContext(), "注册成功")
                                        // 转变视图
                                        changeViewToSetPwd()
                                    }
                                } catch (e: Exception) {
                                    // 如果出现错误，用户需要可再次请求
                                    binding.buttonNextStep.isEnabled = true
                                    // 给出错误提示
                                    binding.progressBar2.visibility = View.INVISIBLE
                                    showMessage(requireContext(), getErrorMessage(e))
                                    return@launch
                                }
                                binding.progressBar2.visibility = View.INVISIBLE
                                step++
                            }
                        }
                        2 -> {
                            binding.progressBar2.visibility = View.VISIBLE
                            isEnabled = false
                            val userName = signUpUsernameText.text.toString().trim()
                            val pwd = signUpPasswordText.text.toString().trim()
                            launch {
                                try {
                                    saveUser(phone, userName)
                                    setUserPassword(pwd)
                                    showMessage(requireContext(), "设置密码成功")
                                    findNavController().navigate(R.id.action_signUpFragment_to_emergency)
                                } catch (e: Exception) {
                                    binding.progressBar2.visibility = View.INVISIBLE
                                    binding.buttonNextStep.isEnabled = true
                                    showMessage(requireContext(), getErrorMessage(e))
                                    return@launch
                                }
                            }

                        }
                    }
                }
            }


            // 手机号需要 11 位
            with(signUpPhoneText) {
                doOnTextChanged { text, _, _, _ ->
                    if (text.toString().trim().length != 11) {
                        buttonNextStep.isEnabled = false
                    } else {
                        buttonNextStep.isEnabled = true
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
                val phone = signUpPhoneText.text.toString().trim()
                launch {
                    try {
                        sendCodeForSignUp(phone)
                    } catch (e: Exception) {
                        showMessage(requireContext(), getErrorMessage(e))
                    }
                    showMessage(requireContext(), "发送成功")
                    myCountDownTimer.start()
                }

            }

            // 验证验证码合法性，不得少于 6 位
            with(signUpCodeText) {
                doOnTextChanged { text, _, _, _ ->
                    if (text.toString().trim().length != 6) {
                        buttonNextStep.isEnabled = false
                    } else {
                        buttonNextStep.isEnabled = true
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

            // 验证两次输入的密码是否一致
            with(signUpPasswordVerifyText) {
                doOnTextChanged { text, _, _, _ ->
                    if (text.toString().trim() != binding.signUpPasswordText.text.toString()
                            .trim()
                    ) {
                        buttonNextStep.isEnabled = false
                    } else {
                        buttonNextStep.isEnabled = true
                        signUpPasswordVerifyLayout.error = null
                    }

                }
                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus && text.toString()
                            .trim() != binding.signUpPasswordText.text.toString()
                            .trim()
                    ) {
                        signUpPasswordVerifyLayout
                            .error = getString(R.string.password_not_unanimous)
                    }
                }
            }

        }
    }

    // 以下是网络请求，全走 IO 线程

    // 判断用户是否已注册
    private suspend fun judgeUserIfExist(phone: String) = withContext(Dispatchers.IO) {
        val query = AVQuery<AVObject>("UserSignUp")
        query.whereEqualTo("phone", phone)
        return@withContext query.count() == 1
    }


    // 发送验证码
    private suspend fun sendCodeForSignUp(phone: String) =
        withContext(Dispatchers.IO) {
            val option = AVSMSOption()
            // 未提供函数
            AVSMS.requestSMSCodeInBackground(
                "+86$phone",
                option
            ).blockingSubscribe()
        }


    // 检测登陆或注册验证码是否正确
    private suspend fun checkCodeToSignUpOrLogin(phone: String, code: String) =
        withContext(Dispatchers.IO) {
            AVUser.signUpOrLoginByMobilePhone("+86$phone", code)
        }


    // 为用户设置密码
    private suspend fun setUserPassword(pwd: String) =
        withContext(Dispatchers.IO) {
            val user = AVUser.getCurrentUser()
            user.password = pwd
            user.save()
        }


    // 保存用户
    private suspend fun saveUser(phone: String, name: String) =
        withContext(Dispatchers.IO) {
            val newUser = AVObject("UserSignUp")
            newUser.put("name", name)
            newUser.put("phone", "+86$phone")
            newUser.save()
        }

    // 设置密码界面
    private fun changeViewToSetPwd() {
        with(binding) {
            titleTextView.text = "请设定一个密码"
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