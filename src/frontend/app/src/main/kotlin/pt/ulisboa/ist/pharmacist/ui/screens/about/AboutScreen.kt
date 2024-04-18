package pt.ulisboa.ist.pharmacist.ui.screens.about

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.about.components.AuthorInfo
import pt.ulisboa.ist.pharmacist.ui.screens.about.components.AuthorInfoView
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.GoBackButton
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.ScreenTitle

const val IMAGE_PADDING = 8
private val githubRepoUrl = Uri.parse("https://github.com/bodybuilders-team/pharmacist")

/**
 * About screen.
 *
 * Information shown for each author:
 * - Student number
 * - First and last name
 * - Personal github profile link
 * - Email contact
 *
 * Also shows the github link of the app's repository.
 *
 * @param onOpenUrl callback to be invoked when a link is clicked
 * @param onSendEmail callback to be invoked when an email is clicked
 * @param onBackButtonClicked callback to be invoked when the back button is clicked
 */
@Composable
fun AboutScreen(
    onOpenUrl: (Uri) -> Unit,
    onSendEmail: (String) -> Unit,
    onBackButtonClicked: () -> Unit
) {
    PharmacistScreen {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            ScreenTitle(title = stringResource(R.string.about_title))

            authors.forEach { author ->
                AuthorInfoView(
                    author = author,
                    onSendEmail = onSendEmail,
                    onOpenUrl = onOpenUrl
                )
            }

            Text(text = stringResource(R.string.about_repoGithub_text))
            Image(
                painter = painterResource(
                    if (isSystemInDarkTheme())
                        R.drawable.ic_github_light
                    else
                        R.drawable.ic_github_dark
                ),
                contentDescription = stringResource(R.string.about_githubLogo_contentDescription),
                modifier = Modifier
                    .clickable { onOpenUrl(githubRepoUrl) }
                    .padding(IMAGE_PADDING.dp)
            )

            GoBackButton(onClick = onBackButtonClicked)
        }
    }
}

private val authors = listOf(
    AuthorInfo(
        number = "110817",
        name = "André Páscoa",
        githubLink = Uri.parse("https://github.com/devandrepascoa"),
        email = "andre.pascoa@tecnico.ulisboa.pt",
        imageId = R.drawable.author_andre_pascoa
    ),
    AuthorInfo(
        number = "110860",
        name = "André Jesus",
        githubLink = Uri.parse("https://github.com/andre-j3sus"),
        email = "andre.f.jesus@tecnico.ulisboa.pt",
        imageId = R.drawable.author_andre_jesus
    ),
    AuthorInfo(
        number = "110893",
        name = "Nyckollas Brandão",
        githubLink = Uri.parse("https://github.com/Nyckoka"),
        email = "nyckollas.brandao@tecnico.ulisboa.pt",
        imageId = R.drawable.author_nyckollas_brandao
    )
)

@Preview
@Composable
private fun AboutScreenPreview() {
    AboutScreen(
        onOpenUrl = {},
        onSendEmail = {},
        onBackButtonClicked = {}
    )
}
