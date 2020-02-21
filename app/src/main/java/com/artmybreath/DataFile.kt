package com.artmybreath

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Quiz(val quizTitle: String, val questionCount: Int, val quizBy: String, val quizID: String)

class Question (val questionTitle: String, val answerOne: String, val answerTwo: String, val answerThree: String, val answerFour: String, val correctAnswer: Int)

lateinit var firebaseAuth: FirebaseAuth

lateinit var firebaseFirestore: FirebaseFirestore

lateinit var selectedQuiz: Quiz

val FIRST_NAME: String="firstName"

val LAST_NAME: String="lastName"

val EMAIL: String="email"

val PHONE_NUMBER: String="phoneNumber"

val firebaseUserCollection: String="users"

val QUIZ_TITLE: String="quizTitle"

val QUIZ_BY: String="quizBy"

val QUESTION: String="question"

val QUESTION_TITLE: String="questionTitle"

val ANSWER_ONE: String="answerOne"

val ANSWER_TWO: String="answerTwo"

val ANSWER_THREE: String="answerThree"

val ANSWER_FOUR: String="answerFour"

val CORRECT_ANSWER: String="correctAns"

val QUESTION_COUNT: String="questionCount"

val QUIZ_COLLECTION: String="quiz"

val QUESTIONS_COLLECTION: String="questions"

val USER_COLLECTION: String="users"