#!/bin/bash
sed -i 's/\/\/ implementation(libs.androidx.camera.camera2)/implementation(libs.androidx.camera.camera2)/' app/build.gradle.kts
sed -i 's/\/\/ implementation(libs.androidx.camera.core)/implementation(libs.androidx.camera.core)/' app/build.gradle.kts
sed -i 's/\/\/ implementation(libs.androidx.camera.lifecycle)/implementation(libs.androidx.camera.lifecycle)/' app/build.gradle.kts
sed -i 's/\/\/ implementation(libs.androidx.camera.view)/implementation(libs.androidx.camera.view)/' app/build.gradle.kts
sed -i 's/\/\/ implementation(libs.coil.compose)/implementation(libs.coil.compose)/' app/build.gradle.kts
