package com.example.futsalhub

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.example.futsalhub.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        auth=FirebaseAuth.getInstance()

        setFragmentResultListener("requestKey") { _, bundle ->

            val uid = bundle.getString("uid").toString()
            val username = bundle.getString("userName").toString()
            binding.tvEditname.setText(username)



            binding.btnEditName.setOnClickListener {
                val uname = binding.tvEditname.text.toString()

                val docRef = db.collection("Users").document(uid)
                docRef.update("userName", uname).addOnSuccessListener {
                    Log.i("updateName", "success")
                }.addOnFailureListener {
                    Log.i("updateName", "failed")
                }

            }

            binding.btnEditPassword.setOnClickListener {
                val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

                val email = user?.email
                val currentPassword = binding.tvcurrentPassword.text.toString()
                val credential = EmailAuthProvider.getCredential(email!!, currentPassword)

                user.reauthenticate(credential)
                    .addOnCompleteListener { reAuthTask ->
                        if (reAuthTask.isSuccessful) {
                            val newPasswordString= binding.tveditpass.text.toString()
                            // User has been re-authenticated, proceed to update password
                            user.updatePassword(newPasswordString)
                                .addOnCompleteListener { passwordUpdateTask ->
                                    if (passwordUpdateTask.isSuccessful) {
                                        Log.i("password123", "Password updated successfully")
                                    } else {
                                        Log.i("password123", "Password update failed: ${passwordUpdateTask.exception?.message}")
                                    }
                                }
                        } else {
                            Log.i("password123", "Re-authentication failed: ${reAuthTask.exception?.message}")
                        }
                    }

            }


        }
        return binding.root
    }

}