package net.thesilkminer.mc.boson.api.configuration

interface Category {
    val name: String
    val comment: String
    val languageKey: String

    val categories: List<Category>
    val entries: List<Entry>

    fun getSubCategory(category: String, vararg subCategories: String): Category
    operator fun get(entry: String): Entry
}
