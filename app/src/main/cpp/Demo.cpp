//
// Created by brahmix on 9/26/19.
//

#include <iostream>
#include <cstring>
#include <jni.h>
#include <string>
#include <iomanip>
#include <sstream>
#include <fcntl.h>

#include <android/asset_manager_jni.h>
#include <android/log.h>
#include <android/sharedmem.h>
#include <sys/mman.h>

#include "model/Song.h"

int demoCall()
{
    model::Song song;
    song.title = "hello";
    song.artist = "some artist";
    std::cout << "testing" << std::endl;
    return 0;
}

extern "C"
JNIEXPORT void
        JNICALL
Java_com_example_mear_activities_MainActivity_test(
        JNIEnv *env,
        jobject /* this */) {

    demoCall();
}