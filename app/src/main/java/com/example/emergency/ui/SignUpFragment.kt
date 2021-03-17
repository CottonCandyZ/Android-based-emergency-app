package com.example.emergency.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.emergency.R
import com.google.android.material.textfield.TextInputLayout

/**
 * A simple [Fragment] subclass.
 */
class SignUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // test
        view?.findViewById<Button>(R.id.buttonNextStep)?.setOnClickListener {
            view?.findViewById<TextInputLayout>(R.id.signUpCode)?.visibility = View.VISIBLE
            view?.findViewById<Button>(R.id.buttonGetCode)?.visibility = View.VISIBLE
        }
    }
}