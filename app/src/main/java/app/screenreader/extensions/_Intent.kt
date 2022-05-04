package app.screenreader.extensions

import android.content.Intent
import app.screenreader.model.Action
import app.screenreader.model.Gesture

/**
 * Created by Jan Jaap de Groot on 06/11/2020
 * Copyright 2020 Stichting Appt
 */

/** Gesture **/
private const val KEY_GESTURE = "gesture"
fun Intent.getGesture() = getSerializableExtra(KEY_GESTURE) as? Gesture
fun Intent.setGesture(gesture: Gesture) = putExtra(KEY_GESTURE, gesture)

/** Gestures **/
private const val KEY_GESTURES = "gestures"
fun Intent.getGestures() = getSerializableExtra(KEY_GESTURES) as? ArrayList<Gesture>
fun Intent.setGestures(gestures: ArrayList<Gesture>) = putExtra(KEY_GESTURES, gestures)

/** Instructions **/
private const val KEY_INSTRUCTIONS = "instructions"
fun Intent.getInstructions() = getBooleanExtra(KEY_INSTRUCTIONS, true)
fun Intent.setInstructions(instructions: Boolean) = putExtra(KEY_INSTRUCTIONS, instructions)

/** Launch **/
private const val KEY_LAUNCH = "launch"
fun Intent.getLaunch() = getBooleanExtra(KEY_LAUNCH, false)
fun Intent.setLaunch(launch: Boolean) = putExtra(KEY_LAUNCH, launch)

/** Title **/
private const val KEY_TITLE = "title"
fun Intent.getTitle() = getStringExtra(KEY_TITLE)
fun Intent.setTitle(title: String) = putExtra(KEY_TITLE, title)

/** Text **/
private const val KEY_TEXT = "text"
fun Intent.getText() = getStringExtra(KEY_TEXT)
fun Intent.setText(text: String) = putExtra(KEY_TEXT, text)

/** Action **/
private const val KEY_ACTION = "action"
fun Intent.doGetAction() = getSerializableExtra(KEY_ACTION) as? Action
fun Intent.doSetAction(action: Action) = putExtra(KEY_ACTION, action)