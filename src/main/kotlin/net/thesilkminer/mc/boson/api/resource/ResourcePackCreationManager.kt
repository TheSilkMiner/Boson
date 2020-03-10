package net.thesilkminer.mc.boson.api.resource

import java.nio.file.Path

interface ResourcePackCreationManager {
    fun request(owner: String, name: String, description: String, root: Path)
}
