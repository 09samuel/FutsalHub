package com.example.futsalhub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.futsalhub.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString()
            val password = binding.etLoginPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        checkAccessLevel()
                    } else {
                        binding.tfLoginPassword.error = "Invalid email or password"
                        removePasswordError()
                    }
                }

            } else {
                if (email.isEmpty()) {
                    binding.tfLoginEmail.error = "Enter email"
                    removeEmailError()
                }
                if (password.isEmpty()) {
                    binding.tfLoginPassword.error = "Enter password"
                    removePasswordError()
                }
            }
        }

        binding.tvSignUp.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
            //findNavController().navigate(R.id.action_loginScreen_to_registrationScreen)
        }

        binding.tvForgot.setOnClickListener {
            val email = binding.etLoginEmail.text.toString()
            if (email.isNotEmpty()) {
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            activity,
                            "Reset password link sent to your email",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        binding.tfLoginEmail.error = it.exception?.message.toString()
                        removeEmailError()
                    }
                }
            } else {
                binding.tfLoginEmail.error = "Enter your email"
                removeEmailError()
            }
        }
        return binding.root
    }

    private fun removeEmailError() {
        binding.etLoginEmail.addTextChangedListener {
            binding.tfLoginEmail.error = null
        }
    }

    private fun removePasswordError() {
        binding.etLoginPassword.addTextChangedListener {
            binding.tfLoginPassword.error = null
        }
    }

    private fun checkAccessLevel() {
        if (firebaseAuth.currentUser!!.isEmailVerified) {
            db = FirebaseFirestore.getInstance()
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val ref = uid?.let { db.collection("Users").document(it) }
            ref?.get()?.addOnSuccessListener {
                if (it != null) {
                    val accessLevel = it.data?.get("accessLevel").toString()
                    if (accessLevel == "0") {
                        val intent = Intent(activity, MainActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    } else if (accessLevel == "1") {
                        val intent = Intent(activity, GroundAdminActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    } else {
                        val intent = Intent(activity, OverallAdminActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }
                }
            }
                ?.addOnFailureListener {
                    Log.i("mytag", "fail")
                }
        } else {
            binding.tfLoginEmail.error = "Please verify your email"
        }
    }

    //remember login
    /*override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            if (firebaseAuth.currentUser!!.isEmailVerified) {
                checkAccessLevel()
            } else {
                binding.tfLoginEmail.error = "Please verify your email"
            }
        }
    }*/
}