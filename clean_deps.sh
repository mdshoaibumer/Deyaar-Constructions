#!/bin/bash
sed -i 's/implementation(libs.firebase.ai)/\/\/ implementation(libs.firebase.ai)/g' app/build.gradle.kts
sed -i 's/implementation(libs.firebase.appcheck.recaptcha)/\/\/ implementation(libs.firebase.appcheck.recaptcha)/g' app/build.gradle.kts
sed -i 's/implementation(libs.retrofit)/\/\/ implementation(libs.retrofit)/g' app/build.gradle.kts
sed -i 's/implementation(libs.okhttp)/\/\/ implementation(libs.okhttp)/g' app/build.gradle.kts
sed -i 's/implementation(libs.moshi.kotlin)/\/\/ implementation(libs.moshi.kotlin)/g' app/build.gradle.kts
sed -i 's/implementation(libs.converter.moshi)/\/\/ implementation(libs.converter.moshi)/g' app/build.gradle.kts
sed -i 's/implementation(libs.logging.interceptor)/\/\/ implementation(libs.logging.interceptor)/g' app/build.gradle.kts
sed -i 's/implementation(libs.timber)/\/\/ implementation(libs.timber)/g' app/build.gradle.kts
