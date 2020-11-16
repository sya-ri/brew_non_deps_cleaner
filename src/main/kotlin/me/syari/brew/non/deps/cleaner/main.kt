package me.syari.brew.non.deps.cleaner

fun main() {
    val brewDepsTempFile = createTempFile().apply {
        deleteOnExit()
    }
    ProcessBuilder().apply {
        redirectOutput(brewDepsTempFile)
        command("brew", "deps", "--installed")
    }.start().waitFor()
    val deps = brewDepsTempFile.readLines().associate { line ->
        val splitLine = line.split(":")
        val key = splitLine[0]
        val value = splitLine[1].split("\\s+".toRegex()).filter(String::isNotEmpty)
        key to value
    }
    println(deps)
}