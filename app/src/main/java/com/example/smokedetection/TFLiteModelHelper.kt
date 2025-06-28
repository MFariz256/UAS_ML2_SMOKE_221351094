package com.example.smokedetection

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class TFLiteModelHelper(context: Context) {

    private var interpreter: Interpreter

    init {
        val modelFile = File(context.getExternalFilesDir(null), "smoke-detection-dataset.tflite")
        interpreter = if (modelFile.exists()) {
            val fileChannel = FileInputStream(modelFile).channel
            val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size())
            fileChannel.close()
            Interpreter(modelBuffer)
        } else {
            val afd = context.assets.openFd("smoke-detection-dataset.tflite")
            val inputStream = FileInputStream(afd.fileDescriptor)
            val fileChannel = inputStream.channel
            val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, afd.startOffset, afd.declaredLength)
            afd.close()
            inputStream.close()
            fileChannel.close()
            Interpreter(modelBuffer)
        }

        Log.d("TFLite", "Output shape: ${interpreter.getOutputTensor(0).shape().contentToString()}")
    }

    private val mean = floatArrayOf(
        0.5377555f, 0.5022604f, 0.4812178f,
        0.5145307f, 0.4910425f, 0.515451f,
        0.47088328f, 0.4886423f, 0.5111028f,
        0.4978888f, 0.4876017f, 0.5079812f,
        0.4944556f
    )

    private val scale = floatArrayOf(
        0.1419243f, 0.1221933f, 0.1179466f,
        0.1261614f, 0.1310339f, 0.1313407f,
        0.1360804f, 0.1202963f, 0.1220484f,
        0.1134204f, 0.1269733f, 0.1255395f,
        0.1182104f
    )

    private fun scaleInput(input: FloatArray): FloatArray {
        return FloatArray(input.size) { i -> (input[i] - mean[i]) / scale[i] }
    }

    fun predict(input: FloatArray): Float {
        require(input.size == 13) { "Model membutuhkan 13 fitur input." }

        val scaled = scaleInput(input)

        val inputBuffer = ByteBuffer.allocateDirect(4 * 13).apply {
            order(ByteOrder.nativeOrder())
            for (v in scaled) putFloat(v)
        }

        val outputBuffer = ByteBuffer.allocateDirect(4 * 2).apply {
            order(ByteOrder.nativeOrder())
        }

        interpreter.run(inputBuffer, outputBuffer)
        outputBuffer.rewind()

        val result = FloatArray(2)
        outputBuffer.asFloatBuffer().get(result)

        return result[1]
    }
}