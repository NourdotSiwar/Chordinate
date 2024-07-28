package com.example.chordinate

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen() {
    Image(painter = painterResource(id = R.drawable.about_us_page), contentDescription =null, Modifier.absoluteOffset(0.dp, 5.dp))
}