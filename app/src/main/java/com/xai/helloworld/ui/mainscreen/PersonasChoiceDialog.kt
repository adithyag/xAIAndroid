package com.xai.helloworld.ui.mainscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.xai.helloworld.repository.Persona
import com.xai.helloworld.repository.Personas
import com.xai.helloworld.ui.PreviewS22Ultra
import com.xai.helloworld.ui.theme.Dimensions
import com.xai.helloworld.ui.theme.Dimensions.AppMargin
import com.xai.helloworld.ui.theme.XAIHelloWorldTheme

@Composable
fun PersonaChoiceDialog(
    personas: List<Persona> = Personas.personas,
    selectedPersona: Persona = Personas.DEFAULT,
    onDismiss: () -> Unit,
    onPersonaSelected: (Persona) -> Unit
) {
    var selectedOption by remember { mutableStateOf<Persona>(selectedPersona) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select a Persona for xAI") },
        text = {
            Column {
                personas.forEach { persona ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedOption == persona,
                            onClick = { selectedOption = persona },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.tertiary
                            ),
                        )
                        Image(
                            painter = painterResource(persona.vectorDrawableId),
                            contentDescription = persona.name,
                            modifier = Modifier
                                .padding(horizontal = AppMargin)
                                .size(Dimensions.SmallIcon), // Apply rotation to the image
                        )
                        Text(persona.name)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedOption?.let { onPersonaSelected(it) }
                    onDismiss()
                },
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@PreviewS22Ultra
@Composable
fun PersonaChoiceDialogPreview() {
    XAIHelloWorldTheme {
        PersonaChoiceDialog(
            onDismiss = {},
            onPersonaSelected = {}
        )
    }
}