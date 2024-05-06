package pt.ulisboa.ist.pharmacist.ui.screens.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.ui.screens.home.LOGO_MAX_HEIGHT_FACTOR
import pt.ulisboa.ist.pharmacist.ui.screens.home.LOGO_MAX_WIDTH_FACTOR
import pt.ulisboa.ist.pharmacist.ui.screens.home.WELCOME_TEXT_PADDING
import pt.ulisboa.ist.pharmacist.ui.screens.home.WELCOME_TEXT_WIDTH_FACTOR

@Composable
fun HomeHeader(loggedIn: Boolean, username: String?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Image(
            painter = painterResource(R.drawable.pharmacy_logo),
            contentDescription = stringResource(R.string.logo_content_description),
            modifier = Modifier
                .fillMaxWidth(LOGO_MAX_WIDTH_FACTOR)
                .fillMaxHeight(LOGO_MAX_HEIGHT_FACTOR)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = if (loggedIn)
                stringResource(R.string.home_welcome_text, username!!)
            else
                stringResource(R.string.home_welcome_guest_text),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(WELCOME_TEXT_WIDTH_FACTOR)
                .padding(bottom = WELCOME_TEXT_PADDING.dp)
        )
    }
}