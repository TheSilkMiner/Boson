package net.thesilkminer.mc.boson.compatibility.crafttweaker.compiler.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ZenGenericFunctionMetadata(
        @get:JvmName("rv") val reflectionVisible: Boolean = true,
        @get:JvmName("r") val returnType: String = "",
        @get:JvmName("k") val kind: Int = 0
)
