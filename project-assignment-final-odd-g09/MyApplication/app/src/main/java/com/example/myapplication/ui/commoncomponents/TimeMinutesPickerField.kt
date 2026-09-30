package com.example.myapplication.ui.commoncomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TimeMinutesPickerField(
    minutes: Int,
    minutesLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    textColor: Color = if (minutes > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline

) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = minutesLabel, style = MaterialTheme.typography.titleMedium, color = textColor)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeMinutesInputDialog(
    initialMinutes: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
    title: String = "Set time",
    confirmLabel: String = "Apply",
    dismissLabel: String = "Cancel"
) {

    val initialHour = initialMinutes / 60
    val normalizedInitialHour = when {
        initialHour < 0 -> 0
        initialHour > 23 -> 23
        else -> initialHour
    }
    val initialMinute = initialMinutes % 60
    val normalizedInitialMinute = when {
        initialMinute < 0 -> 0
        initialMinute > 59 -> 59
        else -> initialMinute
    }

    val timePickerState = rememberTimePickerState(
        initialHour = normalizedInitialHour,
        initialMinute = normalizedInitialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            TimeInput(state = timePickerState)
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm((timePickerState.hour * 60 + timePickerState.minute).coerceIn(0, MAX_TIME_MINUTES))
                }
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(dismissLabel)
            }
        }
    )
}

private const val MAX_TIME_MINUTES = 23 * 60 + 59

