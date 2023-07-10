package wtf.zani.antioptifine

import java.util.ServiceLoader

object PipelineModifier {
    @Suppress("UNUSED")
    @JvmStatic
    fun modifyPipeline(clazz: Class<*>, classLoader: ClassLoader): Iterator<*> =
        ServiceLoader.load(clazz, classLoader)
            .filter { service ->
                val idField = service::class.java.superclass.declaredFields
                    .find { it.name == "id" }!!

                idField.trySetAccessible()
                idField.get(service) != "optifine"
            }
            .iterator()
}
