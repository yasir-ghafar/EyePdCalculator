package com.techlads.eyepdcalculator.ui.onboarding

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.techlads.eyepdcalculator.R
import com.techlads.eyepdcalculator.ui.theme.VernierAmber
import com.techlads.eyepdcalculator.ui.theme.VernierMint
import com.techlads.eyepdcalculator.ui.theme.VernierOutline
import com.techlads.eyepdcalculator.ui.theme.VernierSurfaceVariant
import kotlin.math.absoluteValue
import kotlinx.coroutines.launch

private data class OnboardingPage(
    @StringRes val kicker: Int,
    @StringRes val title: Int,
    @StringRes val body: Int,
    val icon: ImageVector,
    val accent: Color
)

private val pages = listOf(
    OnboardingPage(
        R.string.onboarding_kicker_1,
        R.string.onboarding_title_1,
        R.string.onboarding_body_1,
        Icons.Outlined.Face,
        VernierMint
    ),
    OnboardingPage(
        R.string.onboarding_kicker_2,
        R.string.onboarding_title_2,
        R.string.onboarding_body_2,
        Icons.Outlined.CreditCard,
        VernierAmber
    ),
    OnboardingPage(
        R.string.onboarding_kicker_3,
        R.string.onboarding_title_3,
        R.string.onboarding_body_3,
        Icons.Outlined.Straighten,
        VernierMint
    ),
    OnboardingPage(
        R.string.onboarding_kicker_4,
        R.string.onboarding_title_4,
        R.string.onboarding_body_4,
        Icons.Outlined.PhotoCamera,
        VernierAmber
    )
)

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.splash_brand_name),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onFinished) {
                Text(
                    text = stringResource(R.string.onboarding_skip),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            val offset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                .absoluteValue
                .coerceIn(0f, 1f)
            OnboardingPageContent(
                page = pages[page],
                emphasis = 1f - offset * 0.12f
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            pages.indices.forEach { index ->
                val selected = pagerState.currentPage == index
                val width by animateDpAsState(
                    targetValue = if (selected) 28.dp else 8.dp,
                    label = "indicatorWidth"
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(8.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(if (selected) VernierMint else VernierOutline)
                )
            }
        }

        Button(
            onClick = {
                if (isLastPage) {
                    onFinished()
                } else {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = stringResource(
                    if (isLastPage) R.string.onboarding_start else R.string.onboarding_next
                ),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Text(
            text = stringResource(R.string.onboarding_footer),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage, emphasis: Float) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size((220.dp.value * emphasis).dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                drawCircle(color = page.accent.copy(alpha = 0.14f), radius = size.minDimension * 0.48f, center = center)
                drawCircle(color = page.accent.copy(alpha = 0.28f), radius = size.minDimension * 0.34f, center = center)
                drawCircle(color = VernierSurfaceVariant, radius = size.minDimension * 0.24f, center = center)
            }
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                tint = page.accent,
                modifier = Modifier.size(52.dp)
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = stringResource(page.kicker).uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = page.accent
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(page.title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(page.body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
