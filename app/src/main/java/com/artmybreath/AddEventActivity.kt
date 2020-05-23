package com.artmybreath

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_addevent.*

class AddEventActivity : AppCompatActivity() {

	private lateinit var loadingAlertDialog: AlertDialog

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_addevent)

		createLoadingAlert()

		setListeners()
	}

	override fun onBackPressed() {
		val exitAlertDialog = AlertDialog.Builder(this)
		exitAlertDialog.setTitle("Confirm exit")
		exitAlertDialog.setMessage("Do you want to exit event creation?")
		exitAlertDialog.setPositiveButton("Yes") { _, _ ->
			Intent(this,EventsActivity::class.java).also { startActivity(it) }
			finish()
		}
		exitAlertDialog.setNegativeButton("No") { _, _ -> }
		exitAlertDialog.show()
	}

	private fun addEvent() {
		showLoadingAlert()

		val eventTitle = eventTitleField.text.toString()
		val eventDescription = eventDescriptionField.text.toString()
		val isBookable: Boolean = bookableRadio.isChecked
		var eventVenue = ""
		var eventDay = 0
		var eventMonth = 0
		var eventYear = 0
		var eventHour = 0
		var eventMinute = 0
		if (!isBookable) {
			eventVenue = eventVenueField.text.toString()
			eventDay = eventFullDatePicker.dayOfMonth
			eventMonth = eventFullDatePicker.month
			eventYear = eventFullDatePicker.year
			eventHour = eventTimePicker.hour
			eventMinute = eventTimePicker.minute

			if (eventVenue.isEmpty()) {
				Snackbar.make(eventFormConstraint, "Some fields are empty", Snackbar.LENGTH_LONG)
					.show()
				return
			}
		}

		if (eventDescription.isEmpty() || eventTitle.isEmpty()) {
			Snackbar.make(eventFormConstraint, "Some fields are empty", Snackbar.LENGTH_LONG).show()
			return
		}

		val eventMap = hashMapOf<String, Any>(
			EVENT_TITLE to eventTitle,
			EVENT_VENUE to eventVenue,
			EVENT_DESCRIPTION to eventDescription,
			EVENT_DAY to eventDay,
			EVENT_MONTH to eventMonth,
			EVENT_YEAR to eventYear,
			EVENT_HOUR to eventHour,
			EVENT_MINUTE to eventMinute,
			EVENT_BOOKABLE to isBookable,
			EVENT_CREATOR to currUser.getUserID()
		)

		EVENT_COLLECTION_REFERENCE.document().set(eventMap).addOnSuccessListener {
			hideLoadingAlert()
			Toast.makeText(this,"Event has been successfully created",Toast.LENGTH_SHORT).show()
			finish()
		}.addOnFailureListener {
			hideLoadingAlert()
			Toast.makeText(this,"Event couldn't be added. Something went wrong",Toast.LENGTH_SHORT).show()
			finish()
		}
	}

	private fun setListeners() {
		addEventButton.setOnClickListener {
			addEvent()
			Intent(this,EventsActivity::class.java).also { startActivity(it) }
			finish()
		}

		bookableRadio.setOnClickListener { hidePublishableFields() }

		publishableRadio.setOnClickListener { showPublishableFields() }
	}

	private fun showPublishableFields() {
		eventVenueField.visibility = View.VISIBLE
		eventDateLabel.visibility = View.VISIBLE
		eventFullDatePicker.visibility = View.VISIBLE
		eventTimeLabel.visibility = View.VISIBLE
		eventTimePicker.visibility = View.VISIBLE
	}

	private fun hidePublishableFields() {
		eventVenueField.visibility = View.GONE
		eventDateLabel.visibility = View.GONE
		eventFullDatePicker.visibility = View.GONE
		eventTimeLabel.visibility = View.GONE
		eventTimePicker.visibility = View.GONE
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
}
