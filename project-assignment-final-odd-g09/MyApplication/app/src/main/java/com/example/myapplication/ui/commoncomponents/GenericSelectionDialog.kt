package com.example.myapplication.ui.commoncomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenericSelectionDialog(
    suggestions: List<String>,
    alreadySelectedItems: List<String>,
    onItemSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    dialogTitle: String = "Select option",

    ) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, style = MaterialTheme.typography.titleLarge) },
        text = {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                suggestions.forEach { option ->
                    val alreadyPresent = alreadySelectedItems.contains(option)
                    AssistChip(
                        onClick = { if (!alreadyPresent) onItemSelected(option) },
                        shape = RoundedCornerShape(24.dp),
                        label = { Text(option) },
                        enabled = !alreadyPresent
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}