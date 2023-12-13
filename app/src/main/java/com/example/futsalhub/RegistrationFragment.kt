package com.example.futsalhub

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.futsalhub.databinding.FragmentRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationFragment : Fragment() {
    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnRegister.setOnClickListener {
            val name = binding.etRegisterName.text.toString()
            val email = binding.etRegisterEmail.text.toString()
            val password = binding.etRegisterPassword.text.toString()
            val confirmPassword = binding.etRegisterConfirmPassword.text.toString()


            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { it ->
                            if (it.isSuccessful) {
                                firebaseAuth.currentUser?.sendEmailVerification()
                                    ?.addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            val cid = FirebaseAuth.getInstance().currentUser?.uid
                                            // Store customer data in db
                                            val user = hashMapOf(
                                                "userId" to cid,
                                                "userName" to name,
                                                "email" to email,
                                                "accessLevel" to "0",
                                            )

                                            if (cid != null) {
                                                db.collection("Users")
                                                    .document(cid)
                                                    .set(user)
                                                    .addOnSuccessListener {
                                                        Log.d(
                                                            ContentValues.TAG,
                                                            "DocumentSnapshot added"
                                                        )
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Log.w(
                                                            ContentValues.TAG,
                                                            "Error adding document",
                                                            e
                                                        )
                                                    }
                                            }

                                            Toast.makeText(
                                                activity,
                                                "Link sent to your email. Please verify your email",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            findNavController().navigate(R.id.action_registrationScreen_to_loginScreen)
                                        } else {
                                            Toast.makeText(
                                                activity,
                                                "Registration Failed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    activity,
                                    it.exception?.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    binding.tfRegisterConfirmPassword.error = "Password and Confirm Password do not match"
                    binding.etRegisterConfirmPassword.addTextChangedListener {
                        binding.tfRegisterConfirmPassword.error = null
                    }
                }
            } else {
                if (name.isEmpty()) {
                    binding.tfRegisterName.error = "Enter name"
                    binding.etRegisterName.addTextChangedListener {
                        binding.tfRegisterName.error = null
                    }
                }
                if (email.isEmpty()) {
                    binding.tfRegisterEmail.error = "Enter email"
                    binding.etRegisterEmail.addTextChangedListener {
                        binding.tfRegisterEmail.error = null
                    }
                }
                if (password.isEmpty()) {
                    binding.tfRegisterPassword.error = "Enter password"
                    binding.etRegisterPassword.addTextChangedListener {
                        binding.tfRegisterPassword.error = null
                    }
                }
                if (confirmPassword.isEmpty()) {
                    binding.tfRegisterConfirmPassword.error = "Enter confirm password"
                    binding.etRegisterConfirmPassword.addTextChangedListener {
                        binding.tfRegisterConfirmPassword.error = null
                    }
                }
            }
        }

        binding.tvSignIn.setOnClickListener {
            it.findNavController().navigate(R.id.action_registrationScreen_to_loginScreen)
        }

        return binding.root
    }
}