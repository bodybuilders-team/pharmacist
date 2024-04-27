package pt.ulisboa.ist.pharmacist.ui.screens.shared.navigation

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

/**
 * Navigates to the specified activity.
 *
 * @param clazz the class of the activity to navigate to
 * @param beforeNavigation a function that is called before the navigation is performed
 */
fun <T> Context.navigateTo(
    clazz: Class<T>,
    beforeNavigation: Intent.() -> Unit = {}
) {
    val intent = Intent(this, clazz)

    beforeNavigation(intent)
    startActivity(intent)
}

/**
 * Navigates to the specified activity.
 *
 * @param beforeNavigation a function that is called before the navigation is performed
 */
inline fun <reified T> Context.navigateTo(
    noinline beforeNavigation: Intent.() -> Unit = {}
) {
    navigateTo(T::class.java, beforeNavigation)
}

/**
 * Navigates to the specified activity that returns a result to [activityResultLauncher]'s callback.
 *
 * @param activityResultLauncher the activity result launcher to use
 * @param clazz the class of the activity to navigate to
 * @param beforeNavigation a function that is called before the navigation is performed
 */
fun <T> Context.navigateToForResult(
    activityResultLauncher: ActivityResultLauncher<Intent>,
    clazz: Class<T>,
    beforeNavigation: Intent.() -> Unit = {}
) {
    val intent = Intent(this, clazz)

    beforeNavigation(intent)
    activityResultLauncher.launch(intent)
}

inline fun <reified T> Context.navigateToForResult(
    activityResultLauncher: ActivityResultLauncher<Intent>,
    noinline beforeNavigation: Intent.() -> Unit = {}
) {
    navigateToForResult(activityResultLauncher, T::class.java, beforeNavigation)
}
