package com.example.triviacyl

import Club
import PreguntaGenerator
import PreguntaTrivial
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf("login") }

            when (currentScreen) {
                "login" -> LoginScreen(
                    onLoginSuccess = { currentScreen = "game" },
                    onRegisterClick = { currentScreen = "register" }
                )
                "register" -> RegisterScreen(onBackToLogin = { currentScreen = "login" })
                "game" -> GameScreen(
                    onCerrarSesion = { currentScreen = "login" },
                    onVerEstadisticas = { currentScreen = "estadisticas" }
                )
                "estadisticas" -> EstadisticasScreen(volverAlMenu = { currentScreen = "game" })
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onRegisterClick: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Usuario") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (username == "admin" && password == "admin") {
                onLoginSuccess()
            } else {
                Toast.makeText(context, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Acceder")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "¿No tienes cuenta? Regístrate",
            modifier = Modifier.clickable { onRegisterClick() },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun RegisterScreen(onBackToLogin: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registro", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Usuario") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirmar Contraseña") })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { /* Simulación, no hace nada */ }) {
            Text("Registrar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onBackToLogin) {
            Text("Volver al Login")
        }
    }
}

@Composable
fun GameScreen(
    onCerrarSesion: () -> Unit,
    onVerEstadisticas: () -> Unit
) {
    val context = LocalContext.current
    var selectedRounds by remember { mutableStateOf("5") }
    var tipoPreguntaDeportes by remember { mutableIntStateOf(1) }
    var isDropdownOpen by remember { mutableStateOf(false) }
    var partidaIniciada by remember { mutableStateOf(false) }
    val clubs = remember { mutableStateListOf<Club>() }
    var preguntaActual by remember { mutableStateOf<PreguntaTrivial?>(null) }

    var puntos by remember { mutableIntStateOf(0) }
    var respuestasCorrectas by remember { mutableIntStateOf(0) }
    var rondaActual by remember { mutableIntStateOf(1) }
    var racha by remember { mutableIntStateOf(0) }
    var mostrarResumen by remember { mutableStateOf(false) }
    val categorias = listOf("Deportes","Monuentos","Política","Tradiciones","Municipios")
    var categoriaSeleccionada by remember { mutableStateOf<String?>(null) }
    var mostrarPregunta by remember { mutableStateOf(false) }
    var categoriaAnimada by remember { mutableStateOf<String?>(null) }
    var ruletaEnProgreso by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val cargados = ClubDataLoader.loadClubsFromCSV(context)
        clubs.addAll(cargados)
    }

    LaunchedEffect(!mostrarPregunta) {
        if (!mostrarPregunta) {
            ruletaEnProgreso = true
            repeat(10) {
                categoriaAnimada = categorias.random()
                delay(100)
            }
            categoriaSeleccionada = categoriaAnimada
            ruletaEnProgreso = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("TriviaCyL", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))

        when {
            mostrarResumen -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎉 ¡Partida terminada!", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Puntos totales: $puntos")
                    Text("Respuestas correctas: $respuestasCorrectas de $selectedRounds")
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = {
                        puntos = 0
                        respuestasCorrectas = 0
                        racha = 0
                        rondaActual = 1
                        mostrarResumen = false
                        partidaIniciada = false
                        preguntaActual = null
                        mostrarPregunta = false
                    }) {
                        Text("Volver al inicio")
                    }
                }
            }

            !partidaIniciada -> {
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
                    puntos = 0
                    rondaActual = 1
                    preguntaActual = PreguntaGenerator.generarPreguntaProvincia(clubs)
                    partidaIniciada = true
                    mostrarResumen = false
                    mostrarPregunta = false
                }) {
                    Text("Iniciar Partida")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = onVerEstadisticas) {
                    Text("Estadísticas")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = onCerrarSesion) {
                    Text("Cerrar Sesión")
                }
            }

            else -> {
                Text("Ronda $rondaActual de $selectedRounds", style = MaterialTheme.typography.bodyLarge)
                Text("Puntos: $puntos", style = MaterialTheme.typography.bodyLarge)
                Text("Racha actual: $racha", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(16.dp))

                if (!mostrarPregunta) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎯 Ruleta de categoría", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = categoriaAnimada ?: "...",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        if (!ruletaEnProgreso) {
                            Button(onClick = {
                                if (categoriaSeleccionada == "Deportes") {
                                    preguntaActual = if (tipoPreguntaDeportes == 1) {
                                        tipoPreguntaDeportes = 2
                                        PreguntaGenerator.generarPreguntaProvincia(clubs)
                                    } else {
                                        tipoPreguntaDeportes = 1
                                        PreguntaGenerator.generarPreguntaDeporte(clubs)
                                    }
                                }
                                mostrarPregunta = true
                            }) {
                                Text("Continuar")
                            }
                        }
                    }
                } else {
                    preguntaActual?.let { pregunta ->
                        Text(pregunta.texto, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(16.dp))

                        pregunta.opciones.forEach { opcion ->
                            Button(
                                onClick = {
                                    if (opcion == pregunta.respuestaCorrecta) {
                                        puntos++
                                        racha++
                                        respuestasCorrectas++
                                        if (racha == 5) {
                                            puntos += 5
                                            Toast.makeText(context, "¡Racha de 5! +5 puntos extra", Toast.LENGTH_SHORT).show()
                                            racha = 0
                                        } else {
                                            Toast.makeText(context, "¡Correcto!", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Incorrecto", Toast.LENGTH_SHORT).show()
                                        racha = 0
                                    }

                                    rondaActual++
                                    if (rondaActual > selectedRounds.toInt()) {
                                        mostrarResumen = true
                                        partidaIniciada = false
                                    } else {
                                        mostrarPregunta = false
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(opcion)
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun EstadisticasScreen(volverAlMenu: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📊 Estadísticas", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Mejor Puntuación: 12")
        Text("Mejor Categoría: Política")
        Text("Preguntas Acertadas: 23")
        Text("Preguntas Falladas: 17")
        Text("Rachas Conseguidas: 5")

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = volverAlMenu) {
            Text("Volver al Menú")
        }
    }
}
