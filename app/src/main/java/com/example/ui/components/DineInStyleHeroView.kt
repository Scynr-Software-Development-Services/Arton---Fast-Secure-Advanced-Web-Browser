package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R

@Composable
fun DineInStyleHeroView(
    onShopNowClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
    ) {
        // High-fidelity Mockup Web Asset matching user screenshot
        Image(
            painter = painterResource(id = R.drawable.bg_dine_in_style),
            contentDescription = "Dine in Style - Autumn '23 Collection",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Interactive hotspot over the "Shop Now" button
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 220.dp)
                .fillMaxWidth(0.72f)
                .height(52.dp)
                .clip(RoundedCornerShape(26.dp))
                .clickable { onShopNowClick() }
        )
    }
}
