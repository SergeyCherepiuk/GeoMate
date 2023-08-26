package com.example.geomate.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.geomate.R
import com.example.geomate.ui.theme.GeoMateTheme

@Composable
fun MapMarker(
    modifier: Modifier = Modifier,
    fullName: String,
    image: @Composable (Modifier) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
    ) {
        Text(
            text = fullName,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(-(3).dp), // TODO: Fix this later
        ) {
            image(
                Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 5.dp,
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
            )
            Image(
                painter = painterResource(id = R.drawable.arrow_down),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFF7F0
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF2A2A2A
)
@Composable
private fun MapMarkerPreview() {
    GeoMateTheme {
        val profilePictureId = when (isSystemInDarkTheme()) {
            true -> R.drawable.profile_picture_placeholder_dark
            false -> R.drawable.profile_picture_placeholder_light
        }
        MapMarker(fullName = "John Doe") {
            Image(
                painter = painterResource(profilePictureId),
                contentDescription = null,
                modifier = it
            )
        }
    }
}