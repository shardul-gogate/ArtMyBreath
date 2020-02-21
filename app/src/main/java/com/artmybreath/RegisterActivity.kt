package com.artmybreath

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity() {
	private lateinit var profileMap: HashMap<String, Any>
	private lateinit var userID: String

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_register)

		registerProgressBar.visibility= View.GONE

		registerButton.setOnClickListener {
			registerUser()
		}
	}

	private fun registerUser() {
		showProgressBar()

		val firstName=firstNameField.text.toString()
		val lastName=lastNameField.text.toString()
		val email=registerEmailField.text.toString()
		val phoneNumber=registerPhoneField.text.toString()
		val password=registerPasswordField.text.toString()
		val confirmPassword=confirmPasswordField.text.toString()

		if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
			make(registerActivityLayout,"Fill all the details", LENGTH_LONG).show()
			hideProgressBar()
			return
		}

		if(password != confirmPassword) {
			make(registerActivityLayout,"Passwords do not match", LENGTH_LONG).show()
			hideProgressBar()
			return
		}

		if(password.length<6) {
			make(registerActivityLayout,"Password must be at-least 6 characters in length", LENGTH_LONG).show()
			hideProgressBar()
			return
		}

		firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
			if (it.isSuccessful) {
				val currentUser= firebaseAuth.currentUser ?: return@addOnCompleteListener
				userID = currentUser.uid
				profileMap = hashMapOf()
				profileMap.put(FIRST_NAME,firstName)
				profileMap.put(LAST_NAME,lastName)
				profileMap.put(EMAIL,email)
				profileMap.put(PHONE_NUMBER,phoneNumber)

				firebaseFirestore.collection(firebaseUserCollection).document(userID).set(profileMap).addOnFailureListener { e ->
					make(registerActivityLayout,"Sorry! There was a problem while creating your profile",LENGTH_LONG).show()
					firebaseAuth.signOut()
					currentUser.delete()
				}.addOnSuccessListener {
					Toast.makeText(this,"Account created! Welcome to Art My Breath",Toast.LENGTH_SHORT).show()
					Intent(this,HomeScreen::class.java).also { intent -> startActivity(intent) }
					hideProgressBar()
					finish()
				}
			}
			else {
				make(registerActivityLayout,"Sorry! There was a problem while creating your account",LENGTH_LONG).show()
				return@addOnCompleteListener
			}

			hideProgressBar()
		}
	}

	private fun showProgressBar() {
		registerProgressBar.visibility= View.VISIBLE
	}

	private fun hideProgressBar() {
		registerProgressBar.visibility= View.GONE
	}
}
