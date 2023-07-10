package wtf.zani.antioptifine

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import java.lang.instrument.Instrumentation
import java.security.ProtectionDomain

class GenesisLoaderTransformer(private val target: String, instrumentation: Instrumentation) : TransformerStage(instrumentation) {
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

            reader.accept(node, 0)

            val ichorPipeline = node.fields
                .map { Type.getType(it.desc).internalName }
                .find { it.startsWith("com/moonsworth/lunar/") }!!

            println(ichorPipeline)

            instrumentation.removeTransformer(this)
            instrumentation.addTransformer(PipelineTransformer(ichorPipeline, instrumentation))
        }

        return null
    }
}
