package com.artmybreath

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_addportfolio.*

class AddPortfolioActivity : AppCompatActivity() {

	private lateinit var loadingAlertDialog: AlertDialog

	private val categories = arrayListOf<String>("TC1","TC2","TC3","TC4")
	private val subCategories = arrayListOf<String>("TSC1","TSC2","TSC3","TSC4")

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_addportfolio)

		addPortfolioButton.setOnClickListener { addNewPortfolio() }

		createLoadingAlert()

		categorySpinner.adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,categories)
		subCategorySpinner.adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,subCategories)
	}

	override fun onBackPressed() {
		val exitAlertDialog = AlertDialog.Builder(this)
		exitAlertDialog.setTitle("Confirm exit")
		exitAlertDialog.setMessage("Are you sure you want to quit portfolio creation?")
		exitAlertDialog.setPositiveButton("Yes"){ _,_ ->
			Intent(this,ProfileActivity::class.java).also{ startActivity(it) }
			finish()
		}
		exitAlertDialog.setNegativeButton("No"){ _,_ -> }
		exitAlertDialog.show()
	}

	private fun addNewPortfolio() {
		showLoadingAlert()

		val category = categorySpinner.selectedItem as String
		val subCategory = subCategorySpinner.selectedItem as String
		val description = portfolioDescription.text.toString()

		val portfolioMap: HashMap<String, String> = hashMapOf(
			CATEGORY to category,
			SUB_CATEGORY to subCategory,
			DESCRIPTION to description,
			PORTFOLIO_OF to currUser.getUserID()
		)

		PORTFOLIO_COLLECTION_REFERENCE.document().set(portfolioMap).addOnSuccessListener {
			Toast.makeText(this,"Portfolio successfully added to your profile",Toast.LENGTH_SHORT).show()
			hideLoadingAlert()
			Intent(this,ProfileActivity::class.java).also{ startActivity(it) }
			finish()
		}.addOnFailureListener{
			Toast.makeText(this,"There was an error while adding this portfolio",Toast.LENGTH_SHORT).show()
			hideLoadingAlert()
			Intent(this,ProfileActivity::class.java).also{ startActivity(it) }
			finish()
		}
	}

	private fun createLoadingAlert() {
		loadingAlertDialog = AlertDialog.Builder(this).create()
		loadingAlertDialog.setTitle("")
		val alertLayout: View =layoutInflater.inflate(R.layout.layout_loadingalert,null)
		loadingAlertDialog.setView(alertLayout)
		loadingAlertDialog.setCancelable(false)
	}

	private fun showLoadingAlert() {
		loadingAlertDialog.show()
	}

	private fun hideLoadingAlert() {
		loadingAlertDialog.dismiss()
	}
}
