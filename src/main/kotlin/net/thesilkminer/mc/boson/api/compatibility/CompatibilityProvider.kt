package net.thesilkminer.mc.boson.api.compatibility

interface CompatibilityProvider {
    fun canLoad(): Boolean

    fun onPostInitialization() {}
    fun onPreAvailable() {}

    // TODO("Communication API")
    //fun enqueueMessages()
    //fun receiveMessages()
}
