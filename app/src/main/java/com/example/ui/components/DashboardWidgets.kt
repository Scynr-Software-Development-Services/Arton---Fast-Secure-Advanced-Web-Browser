package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WidgetPersonalization
import com.example.ui.theme.UberBlue
import com.example.ui.theme.UberGreen
import com.example.ui.theme.UberPureBlack
import com.example.ui.theme.UberPureWhite

@Composable
fun DashboardWidgetDeck(
    config: WidgetPersonalization,
    onOpenArticle: (String) -> Unit,
    onOpenMatch: (String) -> Unit,
    onPersonalizeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Header with Personalize button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Live Deck",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onPersonalizeClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Personalize",
                    tint = UberBlue,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Personalize",
                    color = UberBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // 1. Weather Widget
        AnimatedVisibility(
            visible = config.showWeather,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            WeatherAnimatedWidget(city = config.selectedCity)
        }

        // 2. Recent News Widget
        AnimatedVisibility(
            visible = config.showNews,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            NewsAnimatedWidget(onArticleClick = onOpenArticle)
        }

        // 3. Game Matches Widget
        AnimatedVisibility(
            visible = config.showMatches,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            GameMatchesWidget(onMatchClick = onOpenMatch)
        }
    }
}

@Composable
fun WeatherAnimatedWidget(city: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "weather_sun")
    val sunRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sun_spin"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "Sun",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier
                                .size(34.dp)
                                .rotate(sunRotation)
                        )
                        Icon(
                            imageVector = Icons.Default.Cloud,
                            contentDescription = "Cloud",
                            tint = Color(0xFF64B5F6),
                            modifier = Modifier
                                .size(22.dp)
                                .padding(top = 8.dp, start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = city,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Partly Cloudy • Clean Air 28",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }

                Text(
                    text = "22°C",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mini Hourly Forecast Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ForecastPill(time = "Now", temp = "22°", isHighlight = true)
                ForecastPill(time = "+1h", temp = "23°")
                ForecastPill(time = "+2h", temp = "21°")
                ForecastPill(time = "+3h", temp = "20°")
                ForecastPill(time = "+4h", temp = "19°")
            }
        }
    }
}

@Composable
private fun ForecastPill(time: String, temp: String, isHighlight: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isHighlight) UberBlue.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = time,
            color = if (isHighlight) UberBlue else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = temp,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NewsAnimatedWidget(onArticleClick: (String) -> Unit) {
    val newsItems = listOf(
        Triple("TECH", "DeepMind unrolls next-gen multimodal engine with 1M tokens", "https://techcrunch.com"),
        Triple("CYBER", "Zero-day vulnerability patched in open web networking core", "https://thehackernews.com"),
        Triple("SCIENCE", "James Webb telescope identifies earliest cosmic water signatures", "https://nasa.gov")
    )

    var currentIdx by remember { mutableIntStateOf(0) }
    val item = newsItems[currentIdx % newsItems.size]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .clickable { onArticleClick(item.third) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when (item.first) {
                            "TECH" -> UberBlue
                            "CYBER" -> Color(0xFFFF2A6D)
                            else -> Color(0xFF00C853)
                        }
                    )
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Text(
                    text = item.first,
                    color = UberPureWhite,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.second,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Arton Radar • 12m ago",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Read",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

@Composable
fun GameMatchesWidget(onMatchClick: (String) -> Unit) {
    val matches = listOf(
        MatchItem("UEFA", "Real Madrid", "2", "1", "Bayern", "78' LIVE", true),
        MatchItem("VALORANT", "Sentinels", "13", "9", "Fnatic", "Final", false),
        MatchItem("NBA", "Celtics", "104", "99", "Warriors", "Q4 2:40", true)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "Matches",
                        tint = Color(0xFF7C4DFF),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Match Tracker",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FiberManualRecord,
                        contentDescription = "Live",
                        tint = UberGreen,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "LIVE SCORES",
                        color = UberGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            matches.take(2).forEach { match ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onMatchClick("https://www.google.com/search?q=${match.team1}+vs+${match.team2}") }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${match.team1} vs ${match.team2}",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${match.score1} - ${match.score2}",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (match.isLive) UberGreen.copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = match.time,
                                color = if (match.isLive) UberGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class MatchItem(
    val league: String,
    val team1: String,
    val score1: String,
    val score2: String,
    val team2: String,
    val time: String,
    val isLive: Boolean
)
