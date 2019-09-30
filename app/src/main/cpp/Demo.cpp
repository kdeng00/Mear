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

#include "nlohmann/json.hpp"

#include "model/Song.h"

model::Song initSong();

int demoCall();

void printSong(const model::Song&);


model::Song initSong()
{
    model::Song song;
    song.title = "hello";
    song.artist = "some artist";


    return song;
}


int demoCall()
{
    model::Song song = initSong();

    std::cout << "testing" << std::endl;
    printSong(song);

    return 0;
}


void printSong(const model::Song& song)
{
    std::cout << "\nsong information" << std::endl;
    std::cout << "title: " << song.title << std::endl;
    std::cout << "artist: " << song.artist << std::endl;
    std::cout << "album: " << song.album << std::endl;
    std::cout << "genre: " << song.genre << std::endl;
    std::cout << "year: " << song.year << std::endl;
}


extern "C"
JNIEXPORT void
        JNICALL
Java_com_example_mear_activities_MainActivity_test(
        JNIEnv *env,
        jobject /* this */) {

    demoCall();
}

extern "C"
JNIEXPORT void
JNICALL
Java_com_example_mear_activities_DemoStreamActivity_test(
        JNIEnv *env,
        jobject /* this */) {

    demoCall();
}
