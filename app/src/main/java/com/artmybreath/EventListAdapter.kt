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
		val rowView: View = inflater.inflate(R.layout.layout_quizlist, parent, false)
		val eventTitle = events[position].eventTitle
		val eventFullDate =
			"${events[position].eventDate} - ${events[position].eventMonth} - ${events[position].eventYear}"
		val eventVenue = events[position].eventVenue
		rowView.findViewById<TextView>(R.id.eventTitleLabel).text = eventTitle
		rowView.findViewById<TextView>(R.id.eventListDateLabel).text = eventFullDate.toString()
		rowView.findViewById<TextView>(R.id.eventVenueLabel).text = eventVenue
		return rowView
	}
}