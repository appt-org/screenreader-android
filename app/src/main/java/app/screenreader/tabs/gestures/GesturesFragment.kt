package app.screenreader.tabs.gestures

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import app.screenreader.R
import app.screenreader.adapters.headerAdapterDelegate
import app.screenreader.adapters.trainingAdapterDelegate
import app.screenreader.extensions.setGesture
import app.screenreader.extensions.setGestures
import app.screenreader.extensions.setInstructions
import app.screenreader.extensions.showError
import app.screenreader.model.Gesture
import app.screenreader.services.ScreenReaderService
import app.screenreader.widgets.ListFragment
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import nl.appt.accessibility.Accessibility
import nl.appt.accessibility.isTalkBackEnabled

class GesturesFragment : ListFragment() {

    override fun getItems(): List<Any> {
        return listOf(
            "Vegen met 1 vinger",
            Gesture.ONE_FINGER_TOUCH,
            Gesture.ONE_FINGER_SWIPE_RIGHT,
            Gesture.ONE_FINGER_SWIPE_LEFT,
            Gesture.ONE_FINGER_SWIPE_UP,
            Gesture.ONE_FINGER_SWIPE_DOWN,
            "Vegen met 2 vingers",
            Gesture.TWO_FINGER_SWIPE_UP,
            Gesture.TWO_FINGER_SWIPE_DOWN,
            Gesture.TWO_FINGER_SWIPE_RIGHT,
            Gesture.TWO_FINGER_SWIPE_LEFT,
            "Vegen met 3 vingers",
            Gesture.THREE_FINGER_SWIPE_UP,
            Gesture.THREE_FINGER_SWIPE_DOWN,
            "Tikken met 1 vinger",
            Gesture.ONE_FINGER_DOUBLE_TAP,
            Gesture.ONE_FINGER_DOUBLE_TAP_HOLD,
            "Tikken met 2 vingers",
            Gesture.TWO_FINGER_TAP,
            Gesture.TWO_FINGER_DOUBLE_TAP,
            Gesture.TWO_FINGER_DOUBLE_TAP_HOLD,
            Gesture.TWO_FINGER_TRIPLE_TAP,
            "Tikken met 3 vingers",
            Gesture.THREE_FINGER_TAP,
            Gesture.THREE_FINGER_DOUBLE_TAP,
            Gesture.THREE_FINGER_DOUBLE_TAP_HOLD,
            Gesture.THREE_FINGER_TRIPLE_TAP,
            "Tikken met 4 vingers",
            Gesture.FOUR_FINGER_TAP,
            Gesture.FOUR_FINGER_DOUBLE_TAP,
            Gesture.FOUR_FINGER_DOUBLE_TAP_HOLD,
            "Snelkoppelingen",
            Gesture.ONE_FINGER_SWIPE_UP_THEN_DOWN,
            Gesture.ONE_FINGER_SWIPE_DOWN_THEN_UP,
            Gesture.ONE_FINGER_SWIPE_RIGHT_THEN_LEFT,
            Gesture.ONE_FINGER_SWIPE_LEFT_THEN_RIGHT,
            Gesture.ONE_FINGER_SWIPE_DOWN_THEN_LEFT,
            Gesture.ONE_FINGER_SWIPE_UP_THEN_LEFT,
            Gesture.ONE_FINGER_SWIPE_LEFT_THEN_UP,
            Gesture.ONE_FINGER_SWIPE_RIGHT_THEN_DOWN,
            Gesture.ONE_FINGER_SWIPE_LEFT_THEN_DOWN,
            Gesture.ONE_FINGER_SWIPE_UP_THEN_RIGHT,
            Gesture.ONE_FINGER_SWIPE_DOWN_THEN_RIGHT,
        )
    }

    override fun getAdapter(): ListDelegationAdapter<List<Any>> {
        return ListDelegationAdapter(
            headerAdapterDelegate(),
            trainingAdapterDelegate<Gesture> { gesture ->
                onGestureClicked(gesture)
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.practice, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_practice) {
            onPracticeClicked()
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getAdapter().notifyDataSetChanged()
    }

    private fun onGestureClicked(gesture: Gesture) {
        if (Accessibility.isTalkBackEnabled(context)) {
            context?.showError(R.string.service_reason_message)
            return
        }

        startActivity<GestureActivity>(REQUEST_CODE_SINGLE) {
            setGesture(gesture)
        }
    }

    private fun onPracticeClicked() {
        AlertDialog.Builder(requireContext())
            .setMessage("Wil je oefenen met of zonder instructies?")
            .setPositiveButton("Met instructies") { _, _ ->
                startPractice(true)
            }
            .setNeutralButton("Zonder instructies") { _, _ ->
                startPractice(false)
            }
            .setNegativeButton("Annuleren") { _, _ ->
                // Ignored
            }
            .show()
    }

    private fun startPractice(instructions: Boolean) {
        val context = this.context ?: return

        if (Accessibility.isTalkBackEnabled(context)) {
            if (!ScreenReaderService.isEnabled(context)) {
                ScreenReaderService.enable(context, instructions)
                return
            }
        }

        val gestures = Gesture.randomized()
        gestures.forEach { gesture ->
            gesture.completed(context, false)
        }

        startActivity<GestureActivity>(REQUEST_CODE_MULTIPLE) {
            setGestures(gestures)
            setInstructions(instructions)
        }
    }

    companion object {
        private const val REQUEST_CODE_SINGLE = 1
        private const val REQUEST_CODE_MULTIPLE = 2
    }
}