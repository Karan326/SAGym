package com.sa.gym.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.sa.gym.R
import com.sa.gym.utils.FragmentTransaction

class SignUpFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    @SuppressLint("CommitPrefEdits")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        FirebaseApp.initializeApp(activity!!.applicationContext)
        //sign-up button click add details in FireStore "admin" collection
        button_sign_up.setOnClickListener {
            val email = edit_email_sign_up.text.toString()
            val password = edit_password_sign_up.text.toString()
            when {
                !isValidEmail(edit_email_sign_up.text.toString()) -> edit_email_sign_up.error =
                    getString(R.string.insert_email_first)
                !isValidPassword(edit_password_sign_up.text.toString()) -> edit_password_sign_up.error =
                    getString(R.string.insert_password_first)
                else -> //create user with email and ic_password
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                FragmentTransaction()
                                    .FragTransactionReplacewithoutBackStack(
                                        requireFragmentManager(),
                                        LoginFragment(),
                                        R.id.container
                                    )
                                emailValidation()
                            } else {
                                edit_email_sign_up.error = getString(R.string.insert_email_properly)
                            }
                        }
            }
        }
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun isValidPassword(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) || !TextUtils.isDigitsOnly(target) || target.length <= 6 || target.length >= 15
    }

    private fun emailValidation() {
        FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        context,
                        getString(R.string.verification_email_sent_to) + FirebaseAuth.getInstance().currentUser?.email,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.failed_to_send_verification_email),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}