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
#include "SongRepository.h"
#include "Tok.h"


model::Song retrieveSong(const std::string&);

std::string fetchToken(const std::string&, const std::string&, const std::string&);


model::Song retrieveSong(const std::string& token)
{
    repository::SongRepository songRepo;
    model::Song song;
    song.id = 2;
    song.title = "Smooth";
    song.album = "Amaze";
    song.artist = "The Herb";
    song.genre = "Blazing";
    song.duration = 420;
    song.year = 420;

    song = songRepo.retrieveSong(token, song.id);

    return song;
}


std::string fetchToken(const std::string& username, const std::string& password,
    const std::string& apiUri)
{
    model::User user(username, password);
    manager::Tok tokMgr;

    auto token = tokMgr.fetchToken(user, apiUri);

    return token;

}


extern "C"
JNIEXPORT jstring
JNICALL
Java_com_example_mear_activities_DemoStreamActivity_logUser(
        JNIEnv *env,
        jobject thisObj,
        jstring username,
        jstring password,
        jstring apiUri ) {

    const std::string usr = env->GetStringUTFChars(username, NULL);
    const std::string pass = env->GetStringUTFChars(password, NULL);
    const std::string api = env->GetStringUTFChars(apiUri, NULL);

    auto token = fetchToken(usr, pass, api);

    return env->NewStringUTF(token.c_str());
}


extern "C"
JNIEXPORT jobject
JNICALL
Java_com_example_mear_activities_DemoStreamActivity_retrieveSong(
        JNIEnv *env,
        jobject thisObj,
        jstring token
        )
{
    const std::string tok = env->GetStringUTFChars(token, NULL);

    auto song = retrieveSong(tok);
    std::string bank = "SBI Bank";

    jclass songClass = env->FindClass( "com/example/mear/models/Song");
    jmethodID jconstructor = env->GetMethodID( songClass,  "<init>", "()V" );
    jobject songObj = env->NewObject( songClass, jconstructor );

    jmethodID songId = env->GetMethodID( songClass,  "setId", "(I)V" );
    jmethodID songTitle = env->GetMethodID( songClass,  "setTitle", "(Ljava/lang/String;)V" );
    jmethodID songAlbum = env->GetMethodID( songClass,  "setAlbum", "(Ljava/lang/String;)V" );
    jmethodID songArtist = env->GetMethodID( songClass,  "setArtist", "(Ljava/lang/String;)V" );
    jmethodID songGenre = env->GetMethodID( songClass,  "setGenre", "(Ljava/lang/String;)V" );
    jmethodID songDuration = env->GetMethodID( songClass,  "setDuration", "(I)V" );
    jmethodID songYear = env->GetMethodID( songClass,  "setYear", "(I)V" );

    jint id = song.id;
    jstring title = env->NewStringUTF(song.title.c_str());
    jstring album = env->NewStringUTF(song.album.c_str());
    jstring artist = env->NewStringUTF(song.artist.c_str());
    jstring genre = env->NewStringUTF(song.genre.c_str());
    jint duration = song.duration;
    jint year = song.year;

    env->CallVoidMethod( songObj, songId, id );
    env->CallVoidMethod(songObj, songTitle, title);
    env->CallVoidMethod(songObj, songAlbum, album);
    env->CallVoidMethod(songObj, songArtist, artist);
    env->CallVoidMethod(songObj, songGenre, genre);
    env->CallVoidMethod(songObj, songDuration, duration);
    env->CallVoidMethod(songObj, songYear, year);

    return songObj;
}
