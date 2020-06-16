package com.pigielwpam.bluetooth2

import android.content.Context
import java.io.*
import java.io.File.separator
import kotlin.math.abs

class BluetoothData {

    //private var readSchema = listOf<MutableList<Float>>()
    private var readSchema : Array<Int> = arrayOf()
    private var numAngles = mutableListOf<Array<Float>>()
    private var numAnglesX = mutableListOf<Float>()
    private var numAnglesY = mutableListOf<Float>()
    private var numAnglesZ = mutableListOf<Float>()
    private var repPass = arrayOf(0,0,0)
    private var repStarts = mutableListOf<Float>()
    private var repEnds = mutableListOf<Float>()
    private var meanAngles : MutableList<Array<Float>> = mutableListOf<Array<Float>>()

    private var repStarted : Boolean = false
    public fun getNumAngles(angles: String, maxTime: Int): Boolean {

        val splitted = angles.split(";")
        if (splitted.size < 3 || splitted[0].isEmpty() || splitted[1].isEmpty() || splitted[2].isEmpty()) return false
        val x = splitted.get(0).toFloat()
        val y = splitted.get(1).toFloat()
        val z = splitted.get(2).toFloat()
        numAnglesX.add(numAnglesX.lastIndex + 1, x)
        numAnglesY.add(numAnglesY.lastIndex + 1, y)
        numAnglesZ.add(numAnglesZ.lastIndex + 1, z)
        val array = arrayOf(x, y, z)
        numAngles.add(numAngles.lastIndex + 1, array)
        if (numAngles.lastIndex == maxTime * 100 && readSchema.isEmpty()) {
            return true
        }
        return false
    }

    private fun isStable(list: List<Float>, epsilon: Double): Boolean {

        return (list.max()!! - list.min()!!) < 2 * abs(epsilon)
    }

    private fun getRepStart(range: Int, i: Int): Int {

        var stableAxes: Int = 0
        var epsilon = numAnglesX.subList(i - range, i + range).average() * 0.1
        if (isStable(numAnglesX.subList(i - range, i), epsilon) &&
            !isStable(numAnglesX.subList(i, i + range), epsilon)) {
            stableAxes += 1
        }
        epsilon = numAnglesY.subList(i - range, i + range).average() * 0.1
        if (isStable(numAnglesY.subList(i - range, i), epsilon) &&
            !isStable(numAnglesY.subList(i, i + range), epsilon)) {
            stableAxes += 1
        }
        epsilon = numAnglesZ.subList(i - range, i + range).average() * 0.1
        if (isStable(numAnglesZ.subList(i - range, i), epsilon) &&
            !isStable(numAnglesZ.subList(i, i + range), epsilon)) {
            stableAxes += 1
        }
        return if (stableAxes >= 2) {
            i
        } else 0
    }

    private fun getRepEnd(range: Int, i: Int): Int {

        var stableAxes: Int = 0
        var epsilon = numAnglesX.subList(i - range, i + range).average() * 0.1
        if (!isStable(numAnglesX.subList(i - range, i), epsilon) &&
            isStable(numAnglesX.subList(i, i + range), epsilon)
        ) {
            stableAxes += 1
        }
        epsilon = numAnglesY.subList(i - range, i + range).average() * 0.1
        if (!isStable(numAnglesY.subList(i - range, i), epsilon) &&
            isStable(numAnglesY.subList(i, i + range), epsilon)
        ) {
            stableAxes += 1
        }
        epsilon = numAnglesZ.subList(i - range, i + range).average() * 0.1
        if (!isStable(numAnglesZ.subList(i - range, i), epsilon) &&
            isStable(numAnglesZ.subList(i, i + range), epsilon)
        ) {
            stableAxes += 1
        }
        return if (stableAxes >= 2) {
            i
        } else 0
    }

    public fun getReps(): List<MutableList<Int>> {

        val range = 8
        var repStarted: Boolean = false
        var repStarts = mutableListOf<Int>()
        var repEnds = mutableListOf<Int>()
        var repCount = 0
        var start: Int = 0
        var end: Int = 0
        for (i in range..numAngles.lastIndex - range) {
            if (!repStarted) {
                start = getRepStart(range, i)
                if (start != 0) {
                    repStarts.add(repCount, start)
                    repStarted = true
                    start = 0
                }
            } else {
                end = getRepEnd(range, i)
                if (end != 0) {
                    repEnds.add(repCount, end)
                    repCount += 1
                    repStarted = false
                    end = 0
                } else if (i == numAngles.lastIndex - range) {
                    repEnds.add(repCount, numAngles.lastIndex)
                }
            }
        }
        return listOf(repStarts, repEnds)
    }

    public fun getSchema(bounds: List<MutableList<Int>>): Array<Int> {

        val starts = bounds[0]
        val ends = bounds[1]
        if (starts.isEmpty() || ends.isEmpty()) return arrayOf()
        //var repetitions : MutableList<MutableList<Array<Float>>> = mutableListOf()
        var meanRepX: MutableList<Float> = mutableListOf() ; var sumX = 0f
        var meanRepY: MutableList<Float> = mutableListOf() ; var sumY = 0f
        var meanRepZ: MutableList<Float> = mutableListOf() ; var sumZ = 0f
        var meanLength: Int = 0
        var lengthSum: Int = 0
        val zeroPasses: Array<Int> = arrayOf(0,0,0)

        for (i in 0..starts.lastIndex) {
            // repetitions.add(numAngles.subList(starts[i],ends[i]))
            lengthSum += ends[i] - starts[i]
        }
        meanLength = lengthSum / (starts.lastIndex + 1)

        meanRepX.add(0f)
        meanRepY.add(0f)
        meanRepZ.add(0f)

        for (i in 1..meanLength) {
            for (rep in 0..starts.lastIndex) {
                sumX += numAnglesX[starts[rep] + i]
                sumY += numAnglesY[starts[rep] + i]
                sumZ += numAnglesZ[starts[rep] + i]
            }
            meanRepX.add(sumX / (starts.lastIndex + 1) - meanRepX[0])
            meanRepY.add(sumY / (starts.lastIndex + 1) - meanRepY[0])
            meanRepZ.add(sumZ / (starts.lastIndex + 1) - meanRepZ[0])
            meanAngles.add(arrayOf(meanRepX[meanRepX.lastIndex],meanRepY[meanRepY.lastIndex],meanRepZ[meanRepZ.lastIndex]))
            sumX = 0f
            sumY = 0f
            sumZ = 0f
            if (i >= 4) {
                if (pointPassed(0f, meanRepX, i-2)) zeroPasses[0] += 1
                if (pointPassed(0f, meanRepY, i-2)) zeroPasses[1] += 1
                if (pointPassed(0f, meanRepZ, i-2)) zeroPasses[2] += 1
            }
        }
        readSchema = zeroPasses
        numAnglesX = mutableListOf()
        numAnglesY = mutableListOf()
        numAnglesZ = mutableListOf()
        numAngles = mutableListOf()
        return zeroPasses
    }

    private fun pointPassed(value: Float, list : MutableList<Float>, i: Int): Boolean {
        return (list[i - 1] < value && list[i + 1] > value && list[i - 2] < value && list[i + 2] > value) ||
                list[i - 1] > value && list[i + 1] < value && list[i - 2] > value && list[i + 2] < value
    }

    public fun repFinished() : Boolean {
        var repCount = 0

        //if (readSchema.isEmpty() || numAngles.isEmpty()) return false
        if (numAngles.isEmpty()) return false
        val range : Int = 6
        var start : Int = 0
        var end : Int = 0
        if (numAnglesX.lastIndex < 2 * range) {
            return false
        } else {
            if (!repStarted ) {
                start = getRepStart(range,numAngles.lastIndex-range)
                if (start != 0) repStarted = true
            } else {
/*                if (pointPassed(numAnglesX[start],numAnglesX, numAnglesX.lastIndex-2)) repPass[0] += 1
                if (pointPassed(numAnglesY[start],numAnglesY, numAnglesY.lastIndex-2)) repPass[1] += 1
                if (pointPassed(numAnglesZ[start],numAnglesZ, numAnglesZ.lastIndex-2)) repPass[2] += 1
                if (repPass[0] == readSchema[0] && repPass[1] == readSchema[1] && repPass[2] == readSchema[2]) {
                    repPass = arrayOf(0,0,0)
                    return true
                }*/
                end = getRepEnd(range,numAngles.lastIndex-range)
                if (end != 0 && end > start) {
                    repStarted = false
                    return true
                }
            }
            return false
            }
    }


    fun saveSchemaToFile(exerciseName : String, context: Context){

        val file = File(context.filesDir, exerciseName+".txt")
        val writer = FileWriter(file, true)
        var stringAngle : String = ""
        for (i in 0..meanAngles.lastIndex){
            stringAngle = meanAngles[i].joinToString(";","","\n")
            writer.write(stringAngle);
        }
        writer.close()
    }

    fun readFromFile(exerciseName : String, context: Context){
        val file = File(context.filesDir, exerciseName+".txt")
        val fileReader : FileReader = FileReader(file)
        var buffer : CharArray = charArrayOf()
        if (fileReader.ready()) {
            fileReader.read(buffer)
        }
    }

}



