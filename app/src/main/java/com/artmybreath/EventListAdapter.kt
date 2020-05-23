package com.artmybreath

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class EventListAdapter(private val cont: Context, private val events: ArrayList<Event>) :
	ArrayAdapter<Event>(cont, R.layout.layout_eventlist, events) {
	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
		return createItemView(position, parent)
	}

	private fun createItemView(position: Int, parent: ViewGroup): View {
		val inflater: LayoutInflater =
			cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		val rowView: View = inflater.inflate(R.layout.layout_eventlist, parent, false)
		val eventTitleLabel = rowView.findViewById<TextView>(R.id.eventTitleLabel)
		val eventListDateLabel = rowView.findViewById<TextView>(R.id.eventListDateLabel)
		val eventVenueLabel = rowView.findViewById<TextView>(R.id.eventVenueLabel)

		val eventFullDate =
			"${events[position].eventDay} - ${events[position].eventMonth} - ${events[position].eventYear}"

		eventTitleLabel.text = events[position].eventTitle

		if (!events[position].isBookable) {
			eventListDateLabel.text = eventFullDate
			eventVenueLabel.text = events[position].eventVenue
		} else {
			eventListDateLabel.text = events[position].eventCreatorName
			eventVenueLabel.visibility = View.GONE
		}
		return rowView
	}
}