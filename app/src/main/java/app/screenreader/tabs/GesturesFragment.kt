package app.screenreader.tabs

import app.screenreader.adapters.headerAdapterDelegate
import app.screenreader.adapters.trainingAdapterDelegate
import app.screenreader.model.Gesture
import app.screenreader.widgets.ListFragment
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class GesturesFragment() : ListFragment() {

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

    private fun onGestureClicked(gesture: Gesture) {

    }

    companion object {
        private const val REQUEST_CODE_SINGLE = 1
        private const val REQUEST_CODE_MULTIPLE = 2
    }
}