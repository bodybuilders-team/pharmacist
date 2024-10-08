package pt.ulisboa.ist.pharmacist.ui.screens.about.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.ui.screens.about.IMAGE_PADDING

private const val DEV_INFO_PADDING = 16
private const val DEV_INFO_MAX_WIDTH_FACTOR = 0.8f
private const val DEV_INFO_CORNER_RADIUS = 8
private const val IMAGE_SIZE = 110
private const val DEV_INFO_HEIGHT = 140

/**
 * Shows the information of a specific author.
 * Since the email contacts are the ones from our college, ISEL, the email addresses follow a
 * specific format that only depends on the student number.
 *
 * @param author the author's information
 * @param onSendEmail callback to be invoked when an email is clicked
 * @param onOpenUrl callback to be invoked when a link is clicked
 */
@Composable
fun AuthorInfoView(
    author: AuthorInfo,
    onSendEmail: (String) -> Unit,
    onOpenUrl: (Uri) -> Unit
) {
    OutlinedCard(modifier = Modifier.padding(bottom = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(DEV_INFO_MAX_WIDTH_FACTOR),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(author.imageId),
                contentDescription = stringResource(R.string.about_authorImage_contentDescription),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(start = DEV_INFO_PADDING.dp)
                    .clip(CircleShape)
                    .size(IMAGE_SIZE.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(DEV_INFO_PADDING.dp)
                    .fillMaxWidth()
                    .height(DEV_INFO_HEIGHT.dp)
                    .clip(RoundedCornerShape(DEV_INFO_CORNER_RADIUS.dp))
                    .background(Color.LightGray)
            ) {
                Text(
                    text = author.name,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )

                Row {
                    Image(
                        painter = painterResource(R.drawable.ic_github_dark),
                        contentDescription = stringResource(R.string.about_githubLogo_contentDescription),
                        modifier = Modifier
                            .clickable { onOpenUrl(author.githubLink) }
                            .padding(IMAGE_PADDING.dp)
                    )

                    Image(
                        painter = painterResource(R.drawable.ic_email),
                        contentDescription = stringResource(R.string.about_emailIcon_contentDescription),
                        modifier = Modifier
                            .clickable { onSendEmail(author.email) }
                            .padding(IMAGE_PADDING.dp)
                    )
                }
            }
        }
    }
}
