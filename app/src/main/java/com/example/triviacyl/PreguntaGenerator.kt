object PreguntaGenerator {

    fun generarPreguntaProvincia(clubs: List<Club>): PreguntaTrivial? {
        if (clubs.size < 4) return null

        val clubCorrecto = clubs.random()
        val provincias = clubs.map { it.provincia }.distinct().shuffled().take(3).toMutableList()

        // Asegurarnos de que la provincia correcta esté en las opciones
        if (!provincias.contains(clubCorrecto.provincia)) {
            provincias[0] = clubCorrecto.provincia
        }

        return PreguntaTrivial(
            texto = "¿En qué provincia se encuentra el club \"${clubCorrecto.nombre}\"?",
            opciones = provincias.shuffled(),
            respuestaCorrecta = clubCorrecto.provincia
        )
    }

    fun generarPreguntaDeporte(clubs: List<Club>): PreguntaTrivial? {
        val club = clubs.shuffled().find { it.deportes.size >= 1 } ?: return null
        val deporteCorrecto = club.deportes.random().split("-").last().trim()

        val deportesAlternativos = clubs
            .flatMap { it.deportes }
            .map { it.split("-").last().trim() }
            .distinct()
            .filter { it != deporteCorrecto }
            .shuffled()
            .take(3)
            .toMutableList()

        // Asegurarnos de que el deporte correcto esté entre las opciones
        deportesAlternativos.add(deporteCorrecto)

        return PreguntaTrivial(
            texto = "¿Cuál de estos deportes practica el club \"${club.nombre}\"?",
            opciones = deportesAlternativos.shuffled(),
            respuestaCorrecta = deporteCorrecto
        )
    }
}
