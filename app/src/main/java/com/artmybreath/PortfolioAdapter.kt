package com.artmybreath

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

class PortfolioAdapter(private val cont: Context, private val portfolios: ArrayList<Portfolio>): ArrayAdapter<Portfolio>(cont,R.layout.layout_portfoliolist,portfolios) {
	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
		return createItemView(position, parent)
	}
	private fun createItemView(position: Int, parent: ViewGroup): View {}
}