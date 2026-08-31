package com.acevflow.echo.ui.player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acevflow.echo.ui.theme.Dims

@Composable
fun LyricsView(
    lyrics: String?,
    currentPosition: Long,
    modifier: Modifier = Modifier
) {
    if (lyrics == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No lyrics found",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val parsedLyrics = remember(lyrics) { parseLrc(lyrics) }
    val lazyListState = rememberLazyListState()
    
    // Find current line index
    var currentIndex by remember { mutableStateOf(-1) }
    
    LaunchedEffect(currentPosition) {
        val index = parsedLyrics.indexOfLast { it.timestamp <= currentPosition }
        if (index != currentIndex) {
            currentIndex = index
            if (index != -1) {
                lazyListState.animateScrollToItem(index)
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = lazyListState,
        contentPadding = PaddingValues(vertical = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(parsedLyrics) { index, line ->
            val isCurrent = index == currentIndex
            Text(
                text = line.content,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = if (isCurrent) 24.sp else 20.sp,
                    lineHeight = 32.sp
                ),
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

data class LyricsLine(val timestamp: Long, val content: String)

private fun parseLrc(lrc: String): List<LyricsLine> {
    val lines = mutableListOf<LyricsLine>()
    val regex = Regex("\\[(\\d+):(\\d+\\.?\\d*)\\](.*)")
    
    lrc.lines().forEach { line ->
        val match = regex.find(line)
        if (match != null) {
            val min = match.groupValues[1].toLong()
            val sec = match.groupValues[2].toDouble()
            val content = match.groupValues[3].trim()
            if (content.isNotEmpty()) {
                val timestamp = (min * 60 * 1000) + (sec * 1000).toLong()
                lines.add(LyricsLine(timestamp, content))
            }
        }
    }
    return lines.sortedBy { it.timestamp }
}
