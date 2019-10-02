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
#include <curl/curl.h>

#include "model/Song.h"
#include "model/User.h"


std::string fetchToken();

model::Song initSong();
model::User initUser();

size_t funcTest(void*, size_t, size_t, char*);
//size_t funcTest(void*, size_t, size_t, std::string&);

int demoCall();

void printSong(const model::Song&);


std::string fetchToken()
{
    auto user = initUser();
    nlohmann::json usr;
    usr["username"] = user.username;
    usr["password"] = user.password;

    CURL *curl;
    CURLcode res;

    curl = curl_easy_init();

    std::string tok;
    if (curl) {
        const auto url = "";

        char resp[2048];
        curl_easy_setopt(curl, CURLOPT_URL, url);
        const std::string a = usr.dump();
        //std::string r;
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, a.c_str());
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, funcTest);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, resp);
        //curl_easy_setopt(curl, CURLOPT_WRITEDATA, r);
        curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0L);

        res = curl_easy_perform(curl);
        curl_easy_cleanup(curl);

        const std::string i = resp;
        auto s = nlohmann::json::parse(i);
        tok = s["token"].get<std::string>();
        return tok;
   }

   return "";
}


model::Song initSong()
{
    model::Song song;
    song.title = "hello";
    song.artist = "some artist";

    return song;
}


model::User initUser()
{
    model::User user;
    user.username = "";
    user.password = "";

    return user;
}


size_t funcTest(void* ptr, size_t size, size_t nmemb, char *e)
{
    std::memcpy(e, ptr, nmemb);
    e[nmemb] = '\0';

    return nmemb;
}

size_t funcTest(void* ptr, size_t size, size_t nmemb, std::string& e)
{
    e = (char*) ptr;

    return nmemb;
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

    auto tok = fetchToken();
}
