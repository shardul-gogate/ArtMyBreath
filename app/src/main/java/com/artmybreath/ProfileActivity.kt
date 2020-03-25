package com.artmybreath

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : Activity(), AdapterView.OnItemClickListener {

	private lateinit var portfolioAdapter: PortfolioAdapter
	private lateinit var portfolioList: ListView
	private var portfolios: ArrayList<Portfolio> = arrayListOf()

	private lateinit var loadingAlertDialog: AlertDialog

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_profile)

		createLoadingAlert()

		setUserProfile()

		setListeners()

		portfolioList = findViewById(R.id.portfoliosList)
		getUserPortfolios()
	}

	override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
		val portfolioExpansionDialog = AlertDialog.Builder(this)
		portfolioExpansionDialog.setTitle("Portfolio")
		portfolioExpansionDialog.setMessage(portfolios[position].description)
		portfolioExpansionDialog.setPositiveButton("Ok") { _, _ -> }
		portfolioExpansionDialog.setNegativeButton("Delete") { _, _ ->
			showLoadingAlert()
			PORTFOLIO_COLLECTION_REFERENCE.document(portfolios[position].portfolioID).delete()
				.addOnSuccessListener {
					portfolios.removeAt(position)
					portfolioAdapter.notifyDataSetChanged()
					Snackbar.make(
						profileActivityLayout,
						"Portfolio successfully deleted",
						Snackbar.LENGTH_LONG
					).show()
					hideLoadingAlert()
				}
		}
		portfolioExpansionDialog.show()
	}

	private fun getUserPortfolios() {
		showLoadingAlert()
		PORTFOLIO_COLLECTION_REFERENCE.whereEqualTo(PORTFOLIO_OF, currUser.getUserID()).get()
			.addOnSuccessListener {
				for (userPortfolio in it) {
					val category = userPortfolio[CATEGORY] as String
					val subCategory = userPortfolio[SUB_CATEGORY] as String
					val description = userPortfolio[DESCRIPTION] as String
					val portfolioID = userPortfolio.id
					val newPortfolio = Portfolio(category, subCategory, description, portfolioID)
					portfolios.add(newPortfolio)
				}
				portfolioAdapter = PortfolioAdapter(this, portfolios)
				portfolioList.adapter = portfolioAdapter
				portfolioList.isClickable = true
				portfolioList.onItemClickListener = this
				hideLoadingAlert()
			}
	}

	private fun saveChanges() {
		showLoadingAlert()
		val currUserDoc: DocumentReference =
			USER_COLLECTION_REFERENCE.document(currUser.getUserID())
		val newFirstName = updateFirstName.text.toString()
		val newLastName = updateLastName.text.toString()
		val newPhone = updatePhone.text.toString()
		val newProfileMap: HashMap<String, Any> = hashMapOf(
			FIRST_NAME to newFirstName,
			LAST_NAME to newLastName,
			EMAIL to currUser.getEmail(),
			PHONE_NUMBER to newPhone
		)
		currUserDoc.update(newProfileMap).addOnSuccessListener {
			Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
			currUser.updateUser(newFirstName, newLastName, newPhone)
			hideLoadingAlert()
			Intent(this, MainActivity::class.java).also { startActivity(it) }
			finish()

		}.addOnFailureListener {
			hideLoadingAlert()
			profileDisplayConstraint.visibility = View.VISIBLE
			updateProfileLinear.visibility = View.GONE
			Snackbar.make(profileActivityLayout, "Failed to update profile", Snackbar.LENGTH_LONG)
				.show()
		}
	}

	private fun setUserProfile() {
		profileNameField.text = "${currUser.getFirstName()} ${currUser.getLastName()}"
		profileEmailField.text = currUser.getEmail()
		profilePhoneField.text = currUser.getPhone()
	}

	private fun setListeners() {
		updateProfileButton.setOnClickListener {
			fadeInFields()
			populateTextFields()
			profileDisplayConstraint.visibility = View.GONE
			updateProfileLinear.visibility = View.VISIBLE
		}

		saveChangesButton.setOnClickListener {
			fadeInProfile()
			saveChanges()
		}

		createPortfolioActionButton.setOnClickListener {
			Intent(this, AddPortfolioActivity::class.java).also { startActivity(it) }
			finish()
		}
	}

	private fun fadeInProfile() {
		val fadeOutFields = ObjectAnimator.ofFloat(updateProfileLinear, View.ALPHA, 0f)
		fadeOutFields.start()
		val fadeInProfile = ObjectAnimator.ofFloat(profileDisplayConstraint, View.ALPHA, 1f)
		fadeInProfile.start()
	}

	private fun fadeInFields() {
		val fadeOutProfile = ObjectAnimator.ofFloat(profileDisplayConstraint, View.ALPHA, 0f)
		fadeOutProfile.start()
		val fadeInFields = ObjectAnimator.ofFloat(updateProfileLinear, View.ALPHA, 1f)
		fadeInFields.start()
	}

	private fun populateTextFields() {
		updateFirstName.setText(currUser.getFirstName())
		updateLastName.setText(currUser.getLastName())
		updatePhone.setText(currUser.getPhone())
	}

	private fun createLoadingAlert() {
		loadingAlertDialog = AlertDialog.Builder(this).create()
		loadingAlertDialog.setTitle("")
		val alertLayout: View = layoutInflater.inflate(R.layout.layout_loadingalert, null)
		loadingAlertDialog.setView(alertLayout)
		loadingAlertDialog.setCancelable(false)
	}

	private fun showLoadingAlert() {
		loadingAlertDialog.show()
	}

	private fun hideLoadingAlert() {
		loadingAlertDialog.dismiss()
	}

	override fun onBackPressed() {
		if (profileDisplayConstraint.visibility == View.VISIBLE)
			super.onBackPressed()
		else {
			updateProfileLinear.visibility = View.GONE
			profileDisplayConstraint.visibility = View.VISIBLE
			fadeInProfile()
		}
	}
}
