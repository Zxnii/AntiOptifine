package wtf.zani.antioptifine

import java.lang.instrument.Instrumentation

@Suppress("UNUSED_PARAMETER")
fun premain(opt: String?, instrumentation: Instrumentation) {
    instrumentation.addTransformer(GenesisTransformer(instrumentation))
}
