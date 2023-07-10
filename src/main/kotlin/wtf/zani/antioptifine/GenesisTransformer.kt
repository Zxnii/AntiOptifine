package wtf.zani.antioptifine

import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import java.lang.instrument.Instrumentation
import java.security.ProtectionDomain

class GenesisTransformer(instrumentation: Instrumentation) : TransformerStage(instrumentation) {
    override fun transform(
        loader: ClassLoader,
        className: String,
        classBeingRedefined: Class<*>?,
        protectionDomain: ProtectionDomain?,
        classfileBuffer: ByteArray
    ): ByteArray? {
        if (className == "com/moonsworth/lunar/genesis/Genesis") {
            val node = ClassNode()
            val reader = ClassReader(classfileBuffer)

            reader.accept(node, 0)

            val mainMethod = node.methods
                .find { it.name == "main" }!!

            var initClassLoader = mainMethod.instructions
                .find {
                    it is MethodInsnNode && it.name == "setContextClassLoader"
                }!!.previous

            // there's definitely an easier way to do this but this is relatively well guarded against updates
            while (initClassLoader !is MethodInsnNode || initClassLoader.name != "<init>")
                initClassLoader = initClassLoader.previous

            instrumentation.removeTransformer(this)
            instrumentation.addTransformer(GenesisLoaderTransformer(initClassLoader.owner, instrumentation))
        }

        return null
    }
}
