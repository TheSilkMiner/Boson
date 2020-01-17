package net.thesilkminer.mc.boson.api.tag

interface TagTypeRegistry {
    fun <T : Any> registerTagType(tagType: TagType<T>)
}
