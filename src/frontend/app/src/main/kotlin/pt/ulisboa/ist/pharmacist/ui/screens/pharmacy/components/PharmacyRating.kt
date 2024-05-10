package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.getPharmacyById.PharmacyWithUserDataModel
import pt.ulisboa.ist.pharmacist.ui.theme.Gold

private const val MIN_RATING = 1
private const val MAX_RATING = 5

/**
 * A star rating bar that allows the user to select a rating of a pharmacy.
 *
 * @param pharmacy The pharmacy to rate.
 * @param onRatingChanged The callback to be invoked when the rating changes.
 */
@Composable
fun PharmacyRating(
    pharmacy: PharmacyWithUserDataModel,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.rate_this_pharmacy),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        StarRatingBar(
            rating = pharmacy.userRating ?: 0,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            onRatingChanged(it)
        }

        // Global Rating
        Row {
            Text(// only with one decimal
                text = "${pharmacy.pharmacy.globalRating?.let { String.format("%.1f", it) } ?: 0}",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                fontSize = 40.sp,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .align(Alignment.CenterVertically)
            )
            // Number of ratings for each star
            Column {
                for (i in MAX_RATING downTo MIN_RATING) {
                    Row(
                        modifier = Modifier.height(16.dp)
                    ) {
                        Text(
                            text = "${pharmacy.pharmacy.numberOfRatings[i - 1]}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        StarRatingBar(rating = i, densityFactor = 8f, selectable = false)
                    }
                }
            }
        }
    }
}

@Composable
fun StarRatingBar(
    rating: Int,
    modifier: Modifier = Modifier,
    densityFactor: Float = 12f,
    selectable: Boolean = true,
    onRatingChanged: (Int) -> Unit = {}
) {
    val density = LocalDensity.current.density
    val starSize = (densityFactor * density).dp

    Row(
        modifier = Modifier
            .selectableGroup()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in MIN_RATING..MAX_RATING) {
            val isSelected = i <= rating
            val icon = Icons.Filled.Star
            val iconTintColor = if (isSelected) Gold else Color.LightGray
            Icon(
                imageVector = icon,
                contentDescription = stringResource(R.string.star),
                tint = iconTintColor,
                modifier = Modifier
                    .selectable(
                        enabled = selectable,
                        selected = isSelected,
                        onClick = {
                            onRatingChanged(i)
                        }
                    )
                    .width(starSize)
                    .height(starSize)
            )
        }
    }
}