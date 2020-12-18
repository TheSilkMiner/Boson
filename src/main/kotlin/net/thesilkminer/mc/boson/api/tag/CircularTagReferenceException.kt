package net.thesilkminer.mc.boson.api.tag

class CircularTagReferenceException(val tag: Tag<*>) : RuntimeException("The given tag '#${tag.name}' of type '${tag.type.name}' specifies a circular reference on itself")
