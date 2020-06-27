package com.example.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    var emailEditText: EditText? = null
    var pwEditText: EditText? = null
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        emailEditText = findViewById(R.id.editText)
        pwEditText = findViewById(R.id.editText2)

    }

    fun signupClicked(view : View) {
        mAuth.createUserWithEmailAndPassword(emailEditText?.text.toString(), pwEditText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    task.result?.user?.uid?.let {
                        FirebaseDatabase.getInstance().getReference().child("users").child(
                            it
                        ).child("email").setValue(emailEditText?.text.toString())
                    }

                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    Toast.makeText(applicationContext,"SignUp Successfull...",Toast.LENGTH_LONG).show()
                }
                else {
                    Toast.makeText(
                        applicationContext,
                        "SignUp Failed. Try Again..",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun loginClicked(view : View) {
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
