package com.artmybreath

import android.icu.util.Calendar
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_addevent.*

class AddEventActivity : AppCompatActivity() {

	val months = arrayListOf<Int>(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
	val years = arrayListOf<Int>()

	private lateinit var loadingAlertDialog: AlertDialog

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_addevent)

		createLoadingAlert()

		populateYearsList()

		setListeners()
	}

	override fun onBackPressed() {
		val exitAlertDialog = AlertDialog.Builder(this)
		exitAlertDialog.setTitle("Confirm exit")
		exitAlertDialog.setMessage("Do you want to exit event creation?")
		exitAlertDialog.setPositiveButton("Yes") { _, _ ->
			finish()
		}
		exitAlertDialog.setNegativeButton("No") { _, _ -> }
		exitAlertDialog.show()
	}

	private fun addEvent() {
		showLoadingAlert()

		val eventTitle = eventTitleField.text.toString()
		val eventVenue = eventVenueField.text.toString()
		val eventDescription = eventDescriptionField.text.toString()
		var ifBookable: Boolean = true
		if (publishableRadio.isChecked) ifBookable = false
		val day = eventDaySpinner.selectedItem as Int
		val month = eventMonthSpinner.selectedItem as Int
		val year = eventYearSpinner.selectedItem as Int

		if (eventDescription.isEmpty() || eventVenue.isEmpty() || eventTitle.isEmpty()) {
			Snackbar.make(eventFormConstraint, "Some fields are empty", Snackbar.LENGTH_LONG).show()
			return
		}

		val eventMap = hashMapOf<String, Any>()
	}

	private fun imageChooser() {}

	private fun removeImage() {
		eventBannerImage.setBackgroundResource(R.mipmap.ic_default_banner)
	}

	private fun setListeners() {
		removeBannerImage.setOnClickListener { removeImage() }

		chooseBannerImage.setOnClickListener { imageChooser() }

		addEventButton.setOnClickListener { addEvent() }
	}

	private fun createLoadingAlert() {
		loadingAlertDialog = AlertDialog.Builder(this).create()
		val alertLayout: View = layoutInflater.inflate(R.layout.layout_loadingalert, null)
		loadingAlertDialog.setTitle("")
		loadingAlertDialog.setView(alertLayout)
		loadingAlertDialog.setCancelable(false)
	}

	private fun showLoadingAlert() {
		loadingAlertDialog.show()
	}

	private fun hideLoadingAlert() {
		loadingAlertDialog.dismiss()
	}

	private fun populateYearsList() {
		val currentYear = Calendar.getInstance().get(Calendar.YEAR)
		for(i in 0..10)
			years.add(currentYear + i)
	}
}
