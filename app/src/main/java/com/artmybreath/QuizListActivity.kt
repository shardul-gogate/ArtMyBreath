package com.artmybreath

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_quizlist.*

class QuizListActivity : Activity(), AdapterView.OnItemClickListener {
	private lateinit var quizCollection: CollectionReference

	private lateinit var quizAdapter: QuizListAdapter
	private lateinit var quizList: ListView

	private val quizzes: ArrayList<Quiz> = arrayListOf()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_quizlist)

		quizList=findViewById(R.id.quizList)
		getQuizTitles()
	}

	override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
		selectedQuiz=quizzes[position]
		Intent(this,PlayQuizActivity::class.java).also { startActivity(it) }
		finish()
	}

	private fun getQuizTitles() {
		showProgressBar()
		quizCollection=firebaseFirestore.collection("quiz")
		quizCollection.get().addOnSuccessListener {
			for(quizReference in it) {
				val quizID=quizReference.id
				val quizMap=quizReference.data
				val quizTitle=quizMap[QUIZ_TITLE] as String
				val questionCount=quizMap[QUESTION_COUNT] as Long
				val userID=quizMap[QUIZ_BY] as String

				firebaseFirestore.collection(USER_COLLECTION).document(userID).get().addOnCompleteListener { userInfo ->
					if(userInfo.isSuccessful) {
						val userDoc=userInfo.result
						if(userDoc==null) {
							val newQuiz=Quiz(quizTitle,questionCount.toInt(),userID,quizID)
							quizzes.add(newQuiz)
						}
						else {
							val quizBy="${userDoc.get(FIRST_NAME)} ${userDoc.get(LAST_NAME)}"
							val newQuiz=Quiz(quizTitle,questionCount.toInt(),quizBy,quizID)
							quizzes.add(newQuiz)
						}
						quizAdapter=QuizListAdapter(this.applicationContext,quizzes)
						quizList.adapter=quizAdapter
						quizList.isClickable=true
						quizList.onItemClickListener=this
					}
				}
			}
			hideProgressBar()
		}
	}

	private fun showProgressBar() {
		getQuizProgress.visibility=View.VISIBLE
	}

	private fun hideProgressBar() {
		getQuizProgress.visibility=View.GONE
	}
}
