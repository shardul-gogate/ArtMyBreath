package com.artmybreath

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import kotlinx.android.synthetic.main.activity_playquiz.*

class PlayQuizActivity : AppCompatActivity() {

	private lateinit var questionCollection: CollectionReference

	private lateinit var loadingAlertDialog: AlertDialog

	private val questions: ArrayList<Question> = arrayListOf()

	private val questionCount: Int = selectedQuiz.questionCount
	private var currQuestion: Int = 0
	private var selectedAnswer: Int = 0
	private var points: Int = 0

	private var wrongAnswerReview: ArrayList<String> = arrayListOf()
	private var wrongAnswerIndex: Int = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_playquiz)

		createLoadingAlert()

		showLoadingAlert()

		questionCollection =
			QUIZ_COLLECTION_REFERENCE.document(selectedQuiz.quizID).collection(QUESTIONS_COLLECTION)

		getAllQuestions()

		setRadioListeners()

		submitAnswer.setOnClickListener {
			showNextQuestion()
		}
	}

	private fun getAllQuestions() {
		questionCollection.get().addOnSuccessListener {
			for (questionReference in it) {
				val questionMap = questionReference.data
				val questionTitle = questionMap[QUESTION_TITLE] as String
				val answerOne = questionMap[ANSWER_ONE] as String
				val answerTwo = questionMap[ANSWER_TWO] as String
				val answerThree = questionMap[ANSWER_THREE] as String
				val answerFour = questionMap[ANSWER_FOUR] as String
				val correctAnswer = questionMap[CORRECT_ANSWER] as Long
				val newQuestion = Question(
					questionTitle,
					answerOne,
					answerTwo,
					answerThree,
					answerFour,
					correctAnswer.toInt()
				)
				questions.add(newQuestion)
			}
			hideLoadingAlert()
			showFirstQuestion()
		}.addOnFailureListener {
			Snackbar.make(playQuizActivityLayout,"Could not load questions. Try again later",Snackbar.LENGTH_LONG).show()
			hideLoadingAlert()
		}
	}

	private fun showNextQuestion() {
		resetSelection()
		if (selectedAnswer == questions[currQuestion].correctAnswer) {
			points++
		} else {
			val wrongAnswer = getAnswerAtIndex(selectedAnswer)
			val correctAnswer = getAnswerAtIndex(questions[currQuestion].correctAnswer)
			wrongAnswerReview.add("Question: ${questions[currQuestion].questionTitle}\n\nYou answered: $wrongAnswer\n\nCorrect Answer: $correctAnswer\n")
		}
		currQuestion++
		if (currQuestion >= questionCount) {
			quizEndDialog()
		} else {
			playQuizQuestion.text = questions[currQuestion].questionTitle
			answerOneRadio.text = questions[currQuestion].answerOne
			answerTwoRadio.text = questions[currQuestion].answerTwo
			answerThreeRadio.text = questions[currQuestion].answerThree
			answerFourRadio.text = questions[currQuestion].answerFour
		}
		if (currQuestion == questionCount - 1) {
			submitAnswer.text = "Submit and Finish"
		}
	}

	private fun quizEndDialog() {
		val reviewAnswers: AlertDialog.Builder = AlertDialog.Builder(this)
		reviewAnswers.setTitle("Reviewing incorrect answers")
		reviewAnswers.setCancelable(false)
		reviewAnswers.setNeutralButton("Next") { _, _ ->
			if (wrongAnswerIndex < wrongAnswerReview.size) {
				reviewAnswers.setMessage(wrongAnswerReview[wrongAnswerIndex])
				reviewAnswers.show()
				wrongAnswerIndex++
			} else
				finish()
		}

		val endQuiz: AlertDialog.Builder = AlertDialog.Builder(this)
		endQuiz.setTitle("End of quiz")
		endQuiz.setMessage("Quiz has ended. You scored $points / $questionCount")
		endQuiz.setCancelable(false)
		endQuiz.setNeutralButton("Exit") { _, _ ->
			finish()
		}
		if (!firebaseAuth.currentUser!!.isAnonymous) {
			endQuiz.setPositiveButton("Review incorrect answers") { _, _ ->
				if (wrongAnswerIndex < wrongAnswerReview.size) {
					reviewAnswers.setMessage(wrongAnswerReview[wrongAnswerIndex])
					reviewAnswers.show()
					wrongAnswerIndex++
				} else
					finish()
			}
		}
		endQuiz.show()
	}

	private fun createLoadingAlert() {
		loadingAlertDialog = AlertDialog.Builder(this).create()
		loadingAlertDialog.setTitle("")
		val alertLayout: View = layoutInflater.inflate(R.layout.layout_loadingalert, null)
		loadingAlertDialog.setView(alertLayout)
		loadingAlertDialog.setCancelable(false)
	}

	private fun showLoadingAlert() {
		playQuizQuestion.visibility = View.GONE
		quizAnswersRadioGroup.visibility = View.GONE
		submitAnswer.visibility = View.GONE
		loadingAlertDialog.show()
	}

	private fun hideLoadingAlert() {
		playQuizQuestion.visibility = View.VISIBLE
		quizAnswersRadioGroup.visibility = View.VISIBLE
		submitAnswer.visibility = View.VISIBLE
		loadingAlertDialog.dismiss()
	}

	private fun setRadioListeners() {
		answerOneRadio.setOnClickListener { selectedAnswer = 1 }
		answerTwoRadio.setOnClickListener { selectedAnswer = 2 }
		answerThreeRadio.setOnClickListener { selectedAnswer = 3 }
		answerFourRadio.setOnClickListener { selectedAnswer = 4 }
	}

	private fun showFirstQuestion() {
		playQuizQuestion.text = questions[0].questionTitle
		answerOneRadio.text = questions[0].answerOne
		answerTwoRadio.text = questions[0].answerTwo
		answerThreeRadio.text = questions[0].answerThree
		answerFourRadio.text = questions[0].answerFour
	}

	private fun resetSelection() {
		answerOneRadio.isChecked = false
		answerTwoRadio.isChecked = false
		answerThreeRadio.isChecked = false
		answerFourRadio.isChecked = false
	}

	private fun getAnswerAtIndex(index: Int): String = when (index) {
		1 -> questions[currQuestion].answerOne
		2 -> questions[currQuestion].answerTwo
		3 -> questions[currQuestion].answerThree
		4 -> questions[currQuestion].answerFour
		else -> "ANSWER_ERROR"
	}
}
