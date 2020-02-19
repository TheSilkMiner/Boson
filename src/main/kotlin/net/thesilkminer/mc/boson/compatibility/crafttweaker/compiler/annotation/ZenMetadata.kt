package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ZenMetadata(
        @get:JvmName("rv") val reflectionVisible: Boolean = true,
        @get:JvmName("ci") val compilerInfo: IntArray = [],
        @get:JvmName("cs") val compilerSpecificData: Array<String> = [],
        @get:JvmName("k") val kind: Int = 0
)
