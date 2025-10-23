package com.example.pomodoro

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import com.example.pomodoro.ui.countdown.DropdownSessionFun
import com.example.pomodoro.ui.countdown.SettingRow
import com.example.pomodoro.ui.pickmonster.LocalSpacing
import okhttp3.internal.checkOffsetAndCount

@Composable
fun RowTest() {
    SettingRow(
        label = "Tags",
        content = {
            DropdownTags(
                onItemSelected = {}
            )
        }
    )
}
@Composable
fun DropdownTags(
    itemLists: List<String> = listOf(
        "Study",
        "Research",
        "Experiment",
        "Test",
        "Debug",
        "Prototype",
        "Review",
        "Practice",
        "Notes",
        "Summary",
        "Draft",
        "Pending",
        "Final",
        "Completed",
        "Flagged",
        "Priority",
        "Reference",
        "Idea",
        "Concept",
        "Design"
    ),
    onItemSelected: (String) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var tag by rememberSaveable { mutableStateOf(itemLists[0]) }

    // Use a key to measure width of parent box to match dropdown
    var parentWidth by remember { mutableIntStateOf(0) }
    val spacing = LocalSpacing.current

    Box(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp)) // 1️⃣ Clip the entire composable first
            .background(Color.White, RoundedCornerShape(12.dp)) // 2️⃣ Then apply background *inside the clip*
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                RoundedCornerShape(12.dp)
            )
            .onGloballyPositioned { coordinates ->
                parentWidth = coordinates.size.width
            }
            .widthIn(min = 150.dp, max = 170.dp),
    ) {
        Row(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .width(150.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon (
                painter = painterResource(id = R.drawable.tag),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(spacing.medium)
            )
            Text(
                text = tag,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 1
            )

            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { parentWidth.toDp() }) // Match width
                .heightIn(max = 200.dp)
                .background(Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            itemLists.forEach { tagItem ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = tagItem,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        tag = tagItem
                        expanded = false
                        onItemSelected(tagItem)
                    }
                )
            }
        }
    }
}



@Preview(
    showBackground = true
)
@Composable
fun RowTestPreview() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        RowTest()
    }
}