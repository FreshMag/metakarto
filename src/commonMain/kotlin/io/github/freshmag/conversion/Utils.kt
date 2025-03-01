package io.github.freshmag.conversion

/**
 * Generate variations of the strings provided - e.g. `variationsOf(listOf("my_Name")) =
 * ["my_Name", "my_name", "MY_NAME", "My_Name", variationsOf("myName") ...]`
 */
fun variationsOf(names: List<String>): Set<String> =
    names
        .flatMap { name ->
            val variations =
                setOf(
                    name,
                    name.lowercase(),
                    name.uppercase(),
                    name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                )
            val cleanedName = name.replace(Regex("[^a-zA-Z0-9]"), "")
            if (cleanedName == name || cleanedName.isEmpty()) {
                variations
            } else {
                variations + variationsOf(cleanedName)
            }
        }.filterNot { it.isEmpty() }
        .toSet()

/**
 * Generate variations of the strings provided - e.g. `variationsOf("my_Name") =
 * ["my_Name", "my_name", "MY_NAME", "My_Name", variationsOf("myName") ...]`
 */
fun variationsOf(vararg names: String): Set<String> = variationsOf(names.toList())
