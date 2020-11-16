package me.syari.brew.non.deps.cleaner

data class DepTree(val deps: List<String>) {
    var isDep = false
}

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
        key to DepTree(value)
    }
    var nextProcess = true
    while (nextProcess) {
        nextProcess = false
        deps.values.forEach { tree ->
            tree.deps.forEach {
                if (deps[it]?.isDep == false){
                    deps[it]?.isDep = true
                    nextProcess = true
                }
            }
        }
    }
    println(deps.entries.filter { it.value.isDep.not() }.map { it.key })
}