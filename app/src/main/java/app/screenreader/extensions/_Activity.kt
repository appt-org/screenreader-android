package app.screenreader.extensions

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import app.screenreader.R
import app.screenreader.helpers.Preferences
import com.google.android.play.core.review.ReviewManagerFactory

fun Activity.requestReview() {
    // Only prompt once per session
    if (Preferences.isReviewPrompted()) {
        return
    }

    // At least 5 completed events
    val count = Preferences.getGesturesCompleted() + Preferences.getActionsCompleted()
    if (count < 5) {
        return
    }

    // Ensure user only gets prompted once per session
    Preferences.setReviewPrompted(true)

    // Check if user wants to leave a review
    AlertDialog.Builder(this)
        .setTitle(getSpannable(R.string.app_review))
        .setPositiveButton(getSpannable(R.string.action_continue)) { _, _ ->
            // Request review flow
            val manager = ReviewManagerFactory.create(this)
            val request = manager.requestReviewFlow()

            request.addOnCompleteListener { requestTask ->
                if (requestTask.isSuccessful) {
                    // Launch review flow
                    val reviewInfo = requestTask.result
                    manager.launchReviewFlow(this, reviewInfo)
                }
            }
        }
        .setNegativeButton(getSpannable(R.string.action_cancel)) { _, _ ->
            // Ignored
        }
        .show()
}
