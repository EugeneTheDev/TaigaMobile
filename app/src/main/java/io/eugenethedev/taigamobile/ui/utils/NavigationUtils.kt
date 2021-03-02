package io.eugenethedev.taigamobile.ui.utils

import android.os.Parcelable
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.ui.screens.main.Routes

/**
 * This is workaround because navigation component for compose cannot pass Parcelable arguments
 */
fun NavController.navigate(route: String, vararg args: Pair<String, Parcelable>) {
    currentBackStackEntry?.arguments?.let { bundle ->
        args.forEach { (key, value) -> bundle.putParcelable(key, value) }
    }
    navigate(route)
}

/**
 * Since navigating to task screen requires several arguments, here are some utils
 * to make navigation code more readable
 */
typealias NavigateToTask = (id: Long, type: CommonTaskType, ref: Int, projectSlug: String) -> Unit

fun NavController.navigateToTaskScreen(
    id: Long,
    type: CommonTaskType,
    ref: Int,
    projectSlug: String
) = navigate("${Routes.commonTask}/$id/$type/$ref/$projectSlug")