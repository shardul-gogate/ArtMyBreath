package com.artmybreath

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_addportfolio.*

class AddPortfolioActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

	private lateinit var loadingAlertDialog: AlertDialog

	private val categories = arrayListOf<String>("Sponsor", "Artist", "Teacher")
	private val sponsorSubCategories = arrayListOf<String>("Capillary", "Financial")
	private val artistSubCategories = arrayListOf<String>(
		"Classical dancer",
		"Pop dancer",
		"Painter",
		"Classical singer",
		"Pop singer",
		"Guitarist",
		"Sound engineer",
		"Drummer",
		"Keyboardist",
		"Flutist",
		"Tabla",
		"Harmonium",
		"Stage actor",
		"Screen actor",
		"Lighting engineer",
		"Photographer"
	)
	private val teacherSubCategories = arrayListOf<String>(
		"Classical dance",
		"Pop dance",
		"Painting",
		"Classical singing",
		"Pop singing",
		"Guitar",
		"Sound engineering",
		"Drumming",
		"Keyboard",
		"Flute",
		"Tabla",
		"Harmonium",
		"Stage acting",
		"Screen acting",
		"Light engineering",
		"Photography"
	)

	private lateinit var artistSpinnerAdapter: ArrayAdapter<String>
	private lateinit var teacherSpinnerAdapter: ArrayAdapter<String>
	private lateinit var sponsorSpinnerAdapter: ArrayAdapter<String>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_addportfolio)

		setListeners()

		createLoadingAlert()

		val categorySpinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories)
		categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		categorySpinner.adapter = categorySpinnerAdapter

		artistSpinnerAdapter =
			ArrayAdapter(this, android.R.layout.simple_spinner_item, artistSubCategories)
		artistSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

		teacherSpinnerAdapter =
			ArrayAdapter(this, android.R.layout.simple_spinner_item, teacherSubCategories)
		teacherSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		
		sponsorSpinnerAdapter =
			ArrayAdapter(this, android.R.layout.simple_spinner_item, sponsorSubCategories)
		sponsorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
	}

	override fun onBackPressed() {
		val exitAlertDialog = AlertDialog.Builder(this)
		exitAlertDialog.setTitle("Confirm exit")
		exitAlertDialog.setMessage("Are you sure you want to quit portfolio creation?")
		exitAlertDialog.setPositiveButton("Yes") { _, _ ->
			Intent(this, ProfileActivity::class.java).also { startActivity(it) }
			finish()
		}
		exitAlertDialog.setNegativeButton("No") { _, _ -> }
		exitAlertDialog.show()
	}

	override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
		subCategorySpinner.adapter = when (position) {
			0 -> sponsorSpinnerAdapter
			2 -> teacherSpinnerAdapter
			else -> artistSpinnerAdapter
		}
	}

	override fun onNothingSelected(parent: AdapterView<*>?) {
		Toast.makeText(this, "No category selected", Toast.LENGTH_SHORT).show()
	}

	private fun setListeners() {
		addPortfolioButton.setOnClickListener { addNewPortfolio() }

		categorySpinner.onItemSelectedListener = this
	}

	private fun addNewPortfolio() {
		showLoadingAlert()

		val category = categorySpinner.selectedItem as String
		val subCategory = subCategorySpinner.selectedItem as String
		val description = portfolioDescription.text.toString()

		if (description.isEmpty()) {
			Snackbar.make(addPortfolioLayout, "Enter relevant description", Snackbar.LENGTH_LONG)
				.show()
			return
		}

		val portfolioMap: HashMap<String, String> = hashMapOf(
			CATEGORY to category,
			SUB_CATEGORY to subCategory,
			DESCRIPTION to description,
			PORTFOLIO_OF to currUser.getUserID()
		)

		PORTFOLIO_COLLECTION_REFERENCE.document().set(portfolioMap).addOnSuccessListener {
			Toast.makeText(this, "Portfolio successfully added to your profile", Toast.LENGTH_SHORT)
				.show()
			hideLoadingAlert()
			Intent(this, ProfileActivity::class.java).also { startActivity(it) }
			finish()
		}.addOnFailureListener {
			Toast.makeText(
				this,
				"There was an error while adding this portfolio",
				Toast.LENGTH_SHORT
			).show()
			hideLoadingAlert()
			Intent(this, ProfileActivity::class.java).also { startActivity(it) }
			finish()
		}
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
}
