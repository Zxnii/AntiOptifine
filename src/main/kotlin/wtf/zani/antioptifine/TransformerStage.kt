package wtf.zani.antioptifine

import java.lang.instrument.ClassFileTransformer
import java.lang.instrument.Instrumentation

abstract class TransformerStage(protected val instrumentation: Instrumentation) : ClassFileTransformer
