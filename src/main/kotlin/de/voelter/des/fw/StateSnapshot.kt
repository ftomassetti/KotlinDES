package de.voelter.des.fw

import kotlin.reflect.KClass

/**
 * A snapshot of the state history for a given point in time.
 * It's basically a Map from instanceID -> StateVariable
 */
class StateSnapshot(val time: Time) {

    /**
     * The Map to store the data
     */
    private val variables = HashMap<String, StateVariable>()

    /**
     * registers a StateVariable by ID. This function is called
     * by State in order of occurence fo the StateUpdates, then
     * this ends up with the latest update (per instanceID) last.
     */
    internal fun register(state: StateVariable) {
        variables.put(state.instanceID(), state)
    }

    /**
     * returns the state value for a given instanceID
     */
    fun get(instanceID: String) = variables.get(instanceID)

    /**
     * Convenience method to grab integer state's values directly
     */
    fun getInt(instanceID: String) : Int {
        val s = variables.get(instanceID)
        if (s is IntState) {
            return s.value()
        }
        throw RuntimeException(instanceID + " is not an IntState")
    }

    /**
     * directly returns the intger value
     */
    fun <T> getInt(cls: KClass<T>) : Int where T: IntState, T: SingleInstanceStateVariable {
        val s = variables.get(cls.qualifiedName)
        return (s as IntState).value()
    }


    /**
     * Convenience method to grab boolean state's values directly
     */
    fun getBool(instanceID: String) : Boolean {
        val s = variables.get(instanceID)
        if (s is BooleanState) {
            return s.value()
        }
        throw RuntimeException(instanceID + " is not an BooleanState")
    }

    /**
     * directly returns the intger value
     */
    fun <T> getBool(cls: KClass<T>) : Boolean where T: BooleanState, T: SingleInstanceStateVariable {
        val s = variables.get(cls.qualifiedName)
        return (s as BooleanState).value()
    }

    /**
     * For single instance state variables where the class is the instanceID,
     * we can pass in the class for the lookup (instead of its ID aka qualified name)
     * This way we can use the class to cast the result, making value access simpler
     * for the client
     */
    fun <T : SingleInstanceStateVariable> get(cls: KClass<T>) = variables.get(cls.qualifiedName) as T

    /**
     * Debug support
     */
    fun print() {
        System.err.println("Snapshot for " + time.clock)
        for (k in variables.keys) {
            System.err.println("  " + k + " -> " + variables.get(k))
        }
        System.err.println("")
    }

}