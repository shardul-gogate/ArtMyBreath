package com.artmybreath

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)

		hideProgressBar()

		setListeners()
	}

	private fun setListeners() {
		memberSignUpLabel.setOnClickListener {
			hideProgressBar()
			Intent(this,RegisterActivity::class.java).also { startActivity(it) }
		}

		loginButton.setOnClickListener {
			userSignIn()
		}
	}

	private fun userSignIn(){
		showProgressBar()

		val email=loginEmailField.text.toString()
		val password=loginPasswordField.text.toString()

		if(email.isEmpty() || password.isEmpty()) {
			Snackbar.make(loginActivityLayout,"Enter your credentials", Snackbar.LENGTH_LONG).show()
			hideProgressBar()
			return
		}

		firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
			hideProgressBar()
			if(it.isSuccessful) {
				Toast.makeText(this,"Sign-in successful",Toast.LENGTH_SHORT).show()
				Intent(this,HomeScreen::class.java).also { intent -> startActivity(intent) }
				finish()
			}
			else {
				Toast.makeText(this,"Sign-in failed. Incorrect username or password",Toast.LENGTH_SHORT).show()
				return@addOnCompleteListener
			}
		}
	}



	private fun showProgressBar() {
		loginProgressBar.visibility= View.VISIBLE
	}

	private fun hideProgressBar() {
		loginProgressBar.visibility= View.GONE
	}
}
