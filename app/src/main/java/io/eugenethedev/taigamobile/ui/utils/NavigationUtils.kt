package io.eugenethedev.taigamobile.ui.utils

import android.os.Parcelable
import androidx.navigation.NavController
import androidx.navigation.compose.navigate

/**
 * This is workaround because navigation component for compose cannot pass Parcelable arguments
 */
fun NavController.navigate(route: String, vararg args: Pair<String, Parcelable>) {
    currentBackStackEntry?.arguments?.let { bundle ->
        args.forEach { (key, value) -> bundle.putParcelable(key, value) }
    }
    navigate(route)
}