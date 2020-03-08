package com.artmybreath

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

	private lateinit var loadingAlertDialog: AlertDialog

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)

		createLoadingAlert()

		hideLoadingAlert()

		setListeners()
	}

	private fun setListeners() {
		memberSignUpLabel.setOnClickListener {
			Intent(this,RegisterActivity::class.java).also { startActivity(it) }
		}

		loginButton.setOnClickListener {
			userSignIn()
		}

		anonymousLogin.setOnClickListener {
			val anonymousAlert: AlertDialog.Builder=AlertDialog.Builder(this)
			anonymousAlert.setTitle("Continue anonymously")
			anonymousAlert.setMessage("By continuing anonymously, you wil forfeit features such as portfolio creation, quiz review, etc. Continue?")
			anonymousAlert.setPositiveButton("Yes") { _,_ ->
				showLoadingAlert()
				firebaseAuth.signInAnonymously().addOnCompleteListener {
					if(it.isSuccessful) {
						Toast.makeText(this,"Sign-in successful",Toast.LENGTH_SHORT).show()
						Intent(this,HomeScreen::class.java).also { intent -> startActivity(intent) }
						hideLoadingAlert()
						finish()
					}
					else {
						Snackbar.make(loginActivityLayout,"Something went wrong. Could not continue",Snackbar.LENGTH_LONG).show()
						hideLoadingAlert()
					}
				}
			}
			anonymousAlert.setNegativeButton("No"){ _,_ -> }
			anonymousAlert.show()
		}
	}

	private fun userSignIn(){
		showLoadingAlert()

		val email=loginEmailField.text.toString()
		val password=loginPasswordField.text.toString()

		if(email.isEmpty() || password.isEmpty()) {
			Snackbar.make(loginActivityLayout,"Enter your credentials", Snackbar.LENGTH_LONG).show()
			hideLoadingAlert()
			return
		}

		firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
			if(it.isSuccessful) {
				Toast.makeText(this,"Sign-in successful",Toast.LENGTH_SHORT).show()
				Intent(this,HomeScreen::class.java).also { intent -> startActivity(intent) }
				hideLoadingAlert()
				finish()
			}
			else {
				Toast.makeText(this,"Sign-in failed. Incorrect username or password",Toast.LENGTH_SHORT).show()
				hideLoadingAlert()
				return@addOnCompleteListener
			}
			hideLoadingAlert()
		}
	}

	private fun createLoadingAlert() {
		loadingAlertDialog = AlertDialog.Builder(this).create()
		loadingAlertDialog.setTitle("")
		val alertLayout: View=layoutInflater.inflate(R.layout.layout_loadingalert,null)
		loadingAlertDialog.setView(alertLayout)
	}

	private fun showLoadingAlert() {
		loadingAlertDialog.show()
	}

	private fun hideLoadingAlert() {
		loadingAlertDialog.dismiss()
	}
}
