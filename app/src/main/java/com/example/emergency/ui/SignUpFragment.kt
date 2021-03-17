package com.example.emergency.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.emergency.R
import com.example.emergency.databinding.FragmentSignUpBinding

/**
 * A simple [Fragment] subclass.
 */
class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var times = 0
        // test
        view?.findViewById<Button>(R.id.buttonNextStep)?.setOnClickListener {
            when (times) {
                0 -> {
                    binding.signUpCode.visibility = View.VISIBLE
                    binding.buttonGetCode.visibility = View.VISIBLE
                }
                1 -> {
                    binding.signUpCode.visibility = View.GONE
                    binding.buttonGetCode.visibility = View.GONE
                    binding.signUpPhone.visibility = View.GONE
                    binding.signUpPasswordFirst.visibility = View.VISIBLE
                    binding.signUpPasswordSecond.visibility = View.VISIBLE
                }
            }
            times++
        }
    }
}