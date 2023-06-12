package mpdev.springboot.aoc2019.solutions.day06

data class ObjectInOrbit(val name: String) {

    val objectsInOrbit: MutableSet<ObjectInOrbit> = mutableSetOf()

    fun findByName(name: String): ObjectInOrbit? = walkTheMap({}, { it.name == name })

    fun walkTheMap(cmdOnEachObj: (ObjectInOrbit) -> Unit,
                   cmdFilter: (ObjectInOrbit) -> Boolean): ObjectInOrbit? {
        cmdOnEachObj(this)
        if (cmdFilter(this))
            return this
        objectsInOrbit.forEach {
            obj ->
                val res = obj.walkTheMap(cmdOnEachObj, cmdFilter)
                if (res != null)
                    return res
        }
        return null
    }
}