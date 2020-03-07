package net.thesilkminer.mc.boson.implementation.communication

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.event.FMLInterModComms
import net.thesilkminer.mc.boson.MOD_NAME
import net.thesilkminer.mc.boson.api.bosonApi
import net.thesilkminer.mc.boson.api.communication.Message
import net.thesilkminer.mc.boson.api.id.NameSpacedString
import net.thesilkminer.mc.boson.api.log.L
import java.util.concurrent.Executors
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

//TODO("Move to Service based dispatchers")
internal object Dispatcher {
    private val l = L(MOD_NAME, "Communication Dispatcher")
    private val pool = Executors.newFixedThreadPool(3).apply {
        this@Dispatcher.l.info("Started communication dispatcher with 3 threads")
    }

    init {
        Runtime.getRuntime().addShutdownHook(Thread(Runnable { this.terminate() }))
    }

    internal fun dispatch(receiver: String, message: Message<*>) = this.toRunnable(receiver, message)?.let { this.dispatchToPool(it) } ?: Unit

    private fun toRunnable(receiver: String, message: Message<*>): Runnable? {
        val handlerSequence = bosonApi.messageHandlerRegistry[receiver]
        if (handlerSequence.count() == 0) {
            l.warn("Unable to dispatch message to reciever '$receiver': attempting to fallback to deprecated IMC communication system")
            this.tryDispatchImc(receiver, message, message.messageType)
            return null
        }
        return Runnable {
            handlerSequence.forEach { it.handleMessage(message) }
        }
    }

    private fun tryDispatchImc(receiver: String, message: Message<*>, type: KClass<*>) = when {
        type.isSubclassOf(String::class) -> this.tryDispatchImc(receiver, message.key, message.content as String)
        type.isSubclassOf(ItemStack::class) -> this.tryDispatchImc(receiver, message.key, message.content as ItemStack)
        type.isSubclassOf(NBTTagCompound::class) -> this.tryDispatchImc(receiver, message.key, message.content as NBTTagCompound)
        type.isSubclassOf(NameSpacedString::class) -> this.tryDispatchImc(receiver, message.key, message.content as NameSpacedString)
        type.isSubclassOf(KClass::class) -> this.tryDispatchImc(receiver, message.key, message.content as KClass<*>)
        else -> l.error("Unable to fallback to IMC: message won't be delivered")
    }

    private fun tryDispatchImc(receiver: String, key: String, value: String) {
        FMLInterModComms.sendMessage(receiver, key, value)
    }

    private fun tryDispatchImc(receiver: String, key: String, value: ItemStack) {
        FMLInterModComms.sendMessage(receiver, key, value)
    }

    private fun tryDispatchImc(receiver: String, key: String, value: NBTTagCompound) {
        FMLInterModComms.sendMessage(receiver, key, value)
    }

    private fun tryDispatchImc(receiver: String, key: String, value: NameSpacedString) {
        // Not using prefab because... impl must have no knowledge of prefab
        FMLInterModComms.sendMessage(receiver, key, ResourceLocation(value.nameSpace, value.path))
    }

    private fun tryDispatchImc(receiver: String, key: String, value: KClass<*>) {
        FMLInterModComms.sendFunctionMessage(receiver, key, value.java.name)
    }

    private fun dispatchToPool(runnable: Runnable) {
        this.pool.submit(runnable)
    }

    private fun terminate() {
        this.pool.shutdownNow()
        this.l.info("Communication dispatcher shut down")
    }
}
