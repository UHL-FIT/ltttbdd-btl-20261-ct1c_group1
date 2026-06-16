package com.example.calpro.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calpro.domain.engine.formatExpressionForDisplay

@Composable
fun ExpressionDisplay(
    expression: String,
    preview: String,
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false
) {
    val exprScrollState = rememberScrollState()
    val prevScrollState = rememberScrollState()

    // Auto scroll to the end when text changes so we always see the latest input
    LaunchedEffect(expression) {
        exprScrollState.animateScrollTo(exprScrollState.maxValue)
    }
    LaunchedEffect(preview) {
        prevScrollState.animateScrollTo(prevScrollState.maxValue)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(if (isLandscape) 8.dp else 16.dp),
        horizontalAlignment = Alignment.End
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(exprScrollState),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = formatExpressionForDisplay(expression),
                fontSize = if (isLandscape) 24.sp else 28.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                textAlign = TextAlign.End
            )
        }
        Spacer(modifier = Modifier.height(if (isLandscape) 4.dp else 8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(prevScrollState),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = preview,
                fontSize = if (isLandscape) 36.sp else 48.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                textAlign = TextAlign.End
            )
        }
    }
}
