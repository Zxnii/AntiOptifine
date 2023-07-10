package wtf.zani.antioptifine

import net.weavemc.loader.api.util.asm
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import java.lang.instrument.Instrumentation
import java.security.ProtectionDomain

class PipelineTransformer(private val target: String, instrumentation: Instrumentation) : TransformerStage(instrumentation) {
    override fun transform(
        loader: ClassLoader,
        className: String,
        classBeingRedefined: Class<*>?,
        protectionDomain: ProtectionDomain?,
        classfileBuffer: ByteArray
    ): ByteArray? {
        if (className == target) {
            val node = ClassNode()
            val reader = ClassReader(classfileBuffer)
            val writer = ClassWriter(0)

            reader.accept(node, 0)

            val initMethod = node.methods
                .find {
                    val type = Type.getType(it.desc)

                    it.name == "<init>" &&
                            type.argumentTypes.isNotEmpty() &&
                            type.argumentTypes[0].internalName == "java/util/List"
                }!!

            val instructions = asm {
                invokestatic(
                    PipelineModifier::class.java.name.replace(".", "/"),
                    "modifyPipeline",
                    "(Ljava/lang/Class;Ljava/lang/ClassLoader;)Ljava/util/Iterator;"
                )
            }

            val serviceLoaderLoad = initMethod.instructions
                .find {
                    it is MethodInsnNode &&
                            it.name == "load" &&
                            it.owner == "java/util/ServiceLoader"
                }!!

            initMethod.instructions.remove(serviceLoaderLoad.next)
            initMethod.instructions.insert(serviceLoaderLoad, instructions)
            initMethod.instructions.remove(serviceLoaderLoad)

            node.accept(writer)

            return writer.toByteArray()
        }

        return null
    }
}
