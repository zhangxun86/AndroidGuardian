package com.zzsr.guardian.point

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build

object VirtualDeviceDetector {

    private val VIRTUAL_DEVICE_INDICATORS = listOf(
        // Build properties
        "generic",
        "sdk",
        "google_sdk",
        "emulator",
        "android sdk built for x86",
        // Hardware
        "goldfish",
        "ranchu",
        // Manufacturer
        "genymotion"
    )

    fun isVirtualDevice(context: Context): Boolean {
        return (checkBuildProperties() ||
                checkCpuAbi() ||
                checkSensors(context) ||
                checkKernelQemu())
    }

    private fun checkBuildProperties(): Boolean {
        val fingerprint = Build.FINGERPRINT.lowercase()
        val model = Build.MODEL.lowercase()
        val manufacturer = Build.MANUFACTURER.lowercase()
        val brand = Build.BRAND.lowercase()
        val device = Build.DEVICE.lowercase()
        val hardware = Build.HARDWARE.lowercase()

        return VIRTUAL_DEVICE_INDICATORS.any {
            fingerprint.contains(it) ||
                    model.contains(it) ||
                    manufacturer.contains(it) ||
                    brand.contains(it) ||
                    device.contains(it) ||
                    hardware.contains(it)
        }
    }

    private fun checkCpuAbi(): Boolean {
        val abi = Build.CPU_ABI.lowercase()
        return abi.contains("x86") || abi.contains("amd64")
    }

    /**
     * Checks if the device has a limited number of sensors, typical for an emulator.
     * A real device usually has more than 5-7 sensors. We use a low threshold to be safe.
     */
    private fun checkSensors(context: Context): Boolean {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)
        // If there are very few sensors, it's likely an emulator.
        // A simple emulator might have 0 or 1. Let's set a low threshold.
        return sensorList.size < 5
    }

    /**
     * A very strong indicator, checking for the QEMU (Quick EMUlator) kernel property.
     */
    private fun checkKernelQemu(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("getprop ro.kernel.qemu")
            val prop = process.inputStream.bufferedReader().readLine()
            prop == "1"
        } catch (e: Exception) {
            false
        }
    }
}