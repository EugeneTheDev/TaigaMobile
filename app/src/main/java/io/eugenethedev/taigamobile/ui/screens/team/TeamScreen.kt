package io.eugenethedev.taigamobile.ui.screens.team

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.google.accompanist.glide.rememberGlidePainter
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.TeamMember
import io.eugenethedev.taigamobile.ui.components.appbars.ProjectAppBar
import io.eugenethedev.taigamobile.ui.components.loaders.CircularLoader
import io.eugenethedev.taigamobile.ui.components.texts.NothingToSeeHereText
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError

@Composable
fun TeamScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: TeamViewModel = viewModel()
    remember {
        viewModel.start()
        null
    }

    val team by viewModel.team.observeAsState()
    team?.subscribeOnError(onError)

    TeamScreenContent(
        projectName = viewModel.projectName,
        team = team?.data.orEmpty(),
        isLoading = team?.resultStatus == ResultStatus.LOADING,
        onTitleClick = {
            navController.navigate(Routes.projectsSelector)
            viewModel.reset()
        },
        navigateBack = navController::popBackStack
    )
}

@Composable
fun TeamScreenContent(
    projectName: String,
    team: List<TeamMember> = emptyList(),
    isLoading: Boolean = false,
    onTitleClick: () -> Unit = {},
    navigateBack: () -> Unit = {}
) = Column(Modifier.fillMaxSize()) {
    ProjectAppBar(
        projectName = projectName,
        onTitleClick = onTitleClick,
        navigateBack = navigateBack
    )

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularLoader()
            }
        }
        team.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                NothingToSeeHereText()
            }
        }
        else -> {
            LazyColumn(Modifier.padding(horizontal = mainHorizontalScreenPadding)) {
                items(team) {
                    TeamMemberItem(it)
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
private fun TeamMemberItem(
    teamMember: TeamMember
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(0.6f)
    ) {
        Image(
            painter = rememberGlidePainter(
                request = teamMember.avatarUrl ?: R.drawable.default_avatar,
                fadeIn = true,
                requestBuilder = { error(R.drawable.default_avatar) },
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(50.dp).clip(CircleShape)
        )

        Spacer(Modifier.width(6.dp))

        Column {
            Text(
                text = teamMember.name,
                style = MaterialTheme.typography.subtitle1
            )

            Text(
                text = teamMember.role,
                color = Color.Gray,
                style = MaterialTheme.typography.body1,
            )
        }
    }

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.weight(0.4f)
    ) {
        Text(
            text = teamMember.totalPower.toString(),
            style = MaterialTheme.typography.h5
        )

        Spacer(Modifier.width(4.dp))

        Text(
            text = stringResource(R.string.power),
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun TeamScreenPreview() = TaigaMobileTheme {
    TeamScreenContent(
        projectName = "Name",
        team = List(3) {
            TeamMember(
                id = 0L,
                avatarUrl = null,
                name = "First Last",
                role = "Cool guy",
                username = "username",
                totalPower = 14
            )
        }
    )
}