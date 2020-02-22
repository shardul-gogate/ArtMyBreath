package com.artmybreath

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.CollectionReference
import kotlinx.android.synthetic.main.activity_playquiz.*

class PlayQuizActivity : AppCompatActivity() {

	private lateinit var questionCollection: CollectionReference

	private val questions: ArrayList<Question> = arrayListOf()

	private val questionCount: Int= selectedQuiz.questionCount
	private var currQuestion: Int=0
	private var selectedAnswer: Int=0
	private var points: Int=0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_playquiz)

		showProgressBar()

		questionCollection= firebaseFirestore.collection(QUIZ_COLLECTION).document(selectedQuiz.quizID).collection(QUESTIONS_COLLECTION)

		getAllQuestions()

		setRadioListeners()

		submitAnswer.setOnClickListener {
			showNextQuestion()
		}
	}

	private fun getAllQuestions() {
		questionCollection.get().addOnSuccessListener {
			for(questionReference in it) {
				val questionMap=questionReference.data
				val questionTitle= questionMap[QUESTION_TITLE] as String
				val answerOne=questionMap[ANSWER_ONE] as String
				val answerTwo=questionMap[ANSWER_TWO] as String
				val answerThree=questionMap[ANSWER_THREE] as String
				val answerFour=questionMap[ANSWER_FOUR] as String
				val correctAnswer=questionMap[CORRECT_ANSWER] as Long
				val newQuestion=Question(questionTitle,answerOne,answerTwo,answerThree,answerFour,correctAnswer.toInt())
				questions.add(newQuestion)
			}
			hideProgressBar()
			showFirstQuestion()
		}
	}

	private fun showNextQuestion() {
		resetSelection()
		if(selectedAnswer==questions[currQuestion].correctAnswer) {
			points++
		}
		currQuestion++
		if(currQuestion>=questionCount) {
			quizEndAlert()
		}
		else {
			playQuizQuestion.text=questions[currQuestion].questionTitle
			answerOneRadio.text=questions[currQuestion].answerOne
			answerTwoRadio.text=questions[currQuestion].answerTwo
			answerThreeRadio.text=questions[currQuestion].answerThree
			answerFourRadio.text=questions[currQuestion].answerFour
		}
		if(currQuestion==questionCount-1) {
			submitAnswer.text="Submit and Finish"
		}
	}

	private fun quizEndAlert() {
		val endQuiz: AlertDialog.Builder=AlertDialog.Builder(this)
		endQuiz.setTitle("End of quiz")
		endQuiz.setMessage("Quiz has ended. You scored $points / $questionCount")
		endQuiz.setCancelable(false)
		endQuiz.setNeutralButton("Ok") { _,_ ->
			finish()
		}
		endQuiz.show()
	}

	private fun showProgressBar() {
		playQuizQuestion.visibility= View.GONE
		quizAnswersRadioGroup.visibility= View.GONE
		submitAnswer.visibility= View.GONE
		quizProgressBar.visibility= View.VISIBLE
	}

	private fun hideProgressBar() {
		playQuizQuestion.visibility= View.VISIBLE
		quizAnswersRadioGroup.visibility= View.VISIBLE
		submitAnswer.visibility= View.VISIBLE
		quizProgressBar.visibility= View.GONE
	}

	private fun setRadioListeners() {
		answerOneRadio.setOnClickListener{ selectedAnswer=1 }
		answerTwoRadio.setOnClickListener{ selectedAnswer=2 }
		answerThreeRadio.setOnClickListener{ selectedAnswer=3 }
		answerFourRadio.setOnClickListener{ selectedAnswer=4 }
	}

	private fun showFirstQuestion() {
		playQuizQuestion.text=questions[0].questionTitle
		answerOneRadio.text=questions[0].answerOne
		answerTwoRadio.text=questions[0].answerTwo
		answerThreeRadio.text=questions[0].answerThree
		answerFourRadio.text=questions[0].answerFour
	}

	private fun resetSelection() {
		answerOneRadio.isChecked=false
		answerTwoRadio.isChecked=false
		answerThreeRadio.isChecked=false
		answerFourRadio.isChecked=false
	}
}
