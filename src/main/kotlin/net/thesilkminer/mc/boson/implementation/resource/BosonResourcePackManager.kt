/*
 * Copyright (C) 2020  TheSilkMiner
 *
 * This file is part of Boson.
 *
 * Boson is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Boson is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Boson.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact information:
 * E-mail: thesilkminer <at> outlook <dot> com
 */

package net.thesilkminer.mc.boson.implementation.resource

import net.minecraft.client.Minecraft
import net.thesilkminer.kotlin.commons.lang.uncheckedCast
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.log.L
import net.thesilkminer.mc.boson.api.resource.ResourcePackCreationManager
import net.thesilkminer.mc.boson.api.resource.ResourcePackCreationRequester
import java.nio.file.Files
import java.nio.file.Path
import java.util.ServiceLoader

internal object BosonResourcePackManager : ResourcePackCreationManager {
    private data class ResourcePackRequest(val owner: String, val name: String, val description: String, val root: Path)

    private val l = L(MOD_NAME, "Resource Pack Manager")

    private val enqueuedRequests = mutableListOf<ResourcePackRequest>()

    override fun request(owner: String, name: String, description: String, root: Path) = this.request(ResourcePackRequest(owner, name, description, root))

    private fun request(requestData: ResourcePackRequest) {
        this.l.info("Received request $requestData: enqueueing")
        this.enqueuedRequests += requestData
    }

    internal fun gatherAndInjectResourcePacks() {
        this.l.info("Running on 'CLIENT' distribution: attempting to gather and inject all resource packs")
        this.l.info("Using ServiceLoader implementation")
        ServiceLoader.load(ResourcePackCreationRequester::class.java).forEach { it.apply(this) }

        val requests = this.enqueuedRequests.toList().asSequence()
        this.enqueuedRequests.clear()
        this.l.info("Received ${requests.count()} for resource pack creation requests: satisfying them")

        val alreadyConsideredPacks = mutableListOf<Path>()
        val consideredRequests = mutableMapOf<Path, ResourcePackRequest>()

        requests.forEach {
            val requestPath = it.root.normalize().toAbsolutePath().uncheckedCast<Path>()
            if (requestPath in alreadyConsideredPacks) {
                this.l.bigWarn("""
                        A double registration for the pack root '${requestPath}' has been identified! The second request will be ignored.
                        Ignored request details: $it
                        Kept request details: ${consideredRequests[requestPath] ?: "kotlin.KotlinNullPointerException REPORT TO US ON GITHUB"}
                    """.trimIndent())
                return@forEach
            }

            val isFile = Files.isRegularFile(requestPath) && !Files.isDirectory(requestPath)
            val targetResourcePack = createResourcePackFrom(it.owner, it.name, it.description, requestPath, isFile)

            if (targetResourcePack == null) {
                this.l.bigWarn("""
                        Unable to satisfy request $it: created resource pack returned null, which means that an error occurred. Please check your request.
                        isFile: $isFile
                        requestPath: $requestPath
                    """.trimIndent(), L.DumpStackBehavior.DO_NOT_DUMP)
                return@forEach
            }

            alreadyConsideredPacks.add(requestPath) // Why doesn't plusAssign work here?
            consideredRequests[requestPath] = it

            Minecraft.getMinecraft().defaultResourcePacks += targetResourcePack
            this.l.info("Successfully injected resource pack represented by request '${it}' to resource pack list ($targetResourcePack)")
        }

        this.l.info("Satisfied ${consideredRequests.count()} resource pack creation requests out of ${requests.count()} totals")

        if (consideredRequests.count() != requests.count()) this.l.info("Skipped a total of ${alreadyConsideredPacks.count()} requests due to duplication")
    }
}
