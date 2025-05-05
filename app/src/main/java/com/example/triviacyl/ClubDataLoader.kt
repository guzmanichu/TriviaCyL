import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

object ClubDataLoader {

    fun loadClubsFromCSV(context: Context): List<Club> {
        val clubs = mutableListOf<Club>()
        val inputStream = context.assets.open("clubes.csv")
        val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))

        // Salta la cabecera
        reader.readLine()

        reader.forEachLine { line ->
            val parts = line.split(";")
            if (parts.size >= 13 && parts[1].isNotBlank() && parts[2].isNotBlank()) {
                val nombre = parts[1]
                val provincia = parts[3]
                val localidad = parts[4]
                val deportesRaw = parts[12]
                val deportes = deportesRaw.split("|").map { it.trim() }.filter { it.isNotEmpty() }

                clubs.add(
                    Club(
                        nombre = nombre,
                        provincia = provincia,
                        localidad = localidad,
                        deportes = deportes
                    )
                )
            }
        }

        return clubs
    }
}
