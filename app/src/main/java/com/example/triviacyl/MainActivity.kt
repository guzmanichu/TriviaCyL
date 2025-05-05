package com.example.triviacyl

import Club
import PreguntaTrivial
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // Llamada a la pantalla principal del juego
            GameScreen()

        }
    }
}

@Composable
fun GameScreen() {
    val context = LocalContext.current
    var selectedRounds by remember { mutableStateOf("5") }
    var isDropdownOpen by remember { mutableStateOf(false) }
    var partidaIniciada by remember { mutableStateOf(false) }
    val clubs = remember { mutableStateListOf<Club>() }
    var preguntaActual by remember { mutableStateOf<PreguntaTrivial?>(null) }

    LaunchedEffect(Unit) {
        val cargados = ClubDataLoader.loadClubsFromCSV(context)
        clubs.addAll(cargados)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Trivial Castilla y León", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(24.dp))

        if (!partidaIniciada) {
            Box {
                Text(
                    text = "Rondas: $selectedRounds",
                    modifier = Modifier
                        .clickable { isDropdownOpen = !isDropdownOpen }
                        .padding(8.dp)
                )

                DropdownMenu(
                    expanded = isDropdownOpen,
                    onDismissRequest = { isDropdownOpen = false }
                ) {
                    listOf("5", "10", "15").forEach { round ->
                        DropdownMenuItem(
                            text = { Text(round) },
                            onClick = {
                                selectedRounds = round
                                isDropdownOpen = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                preguntaActual = PreguntaGenerator.generarPreguntaProvincia(clubs)
                partidaIniciada = true
            }) {
                Text("Iniciar Partida")
            }

        } else {
            // Mostrar pregunta
            preguntaActual?.let { pregunta ->
                Text(pregunta.texto, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                pregunta.opciones.forEach { opcion ->
                    Button(
                        onClick = {
                            if (opcion == pregunta.respuestaCorrecta) {
                                Toast.makeText(context, "¡Correcto!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Incorrecto", Toast.LENGTH_SHORT).show()
                            }
                            // Generar siguiente pregunta (aquí podrías contar rondas)
                            preguntaActual = PreguntaGenerator.generarPreguntaProvincia(clubs)
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text(opcion)
                    }
                }
            }
        }
    }
}

