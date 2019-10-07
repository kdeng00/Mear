//
// Created by brahmix on 9/26/19.
//

#include <iostream>
#include <fstream>
#include <sstream>
#include <filesystem>
#include <cstring>
#include <jni.h>
#include <string>
#include <iomanip>
#include <sstream>
#include <vector>
#include <fcntl.h>

#include <android/asset_manager_jni.h>
#include <android/log.h>
#include <android/sharedmem.h>
#include <sys/mman.h>

#include <nlohmann/json.hpp>
#include <curl/curl.h>
#include <sqlite3.h>

#include "model/Song.h"
#include "model/User.h"
#include "SongRepository.h"
#include "Tok.h"
#include "UserRepository.h"


model::Song retrieveSong(const std::string&, const std::string&);

std::string fetchToken(const std::string&, const std::string&, const std::string&);

void saveCredentials(const model::User&, const std::string&);


model::Song retrieveSong(const std::string& token, const std::string& baseUri)
{
    repository::SongRepository songRepo;
    model::Song song;
    song.id = 1;
    song.title = "Smooth";
    song.album = "Amaze";
    song.artist = "The Herb";
    song.genre = "Blazing";
    song.duration = 420;
    song.year = 420;

    song = songRepo.retrieveSong(token, baseUri, song);

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


void iterateDirectory(const std::string& path)
{
    /**
    auto somePath = std::filesystem::path(path);
    for (auto dir: std::filesystem::directory_iterator(somePath)) {
        auto foundPath = dir;
        if (foundPath.is_directory()) {
            auto dirPath = foundPath.path();
        } else {
            auto filePath = foundPath.path();
        }
    }
    */
}

void saveCredentials(const model::User& user, const std::string& appDirectory)
{
    std::string filePath(appDirectory);
    filePath.append("/");
    repository::local::UserRepository userRepo;

    if (!userRepo.databaseExist(filePath)) {
        userRepo.initializedDatabase(filePath);
    }
    if (!userRepo.doesUserTableExist(filePath)) {
        userRepo.createUserTable(filePath);
    }

    if (userRepo.isTableEmpty(filePath)) {
        userRepo.deleteUserTable(filePath);
    }

    userRepo.saveUserCred(user, filePath);
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
        jstring token,
        jstring apiUri
        )
{
    const std::string tok = env->GetStringUTFChars(token, NULL);
    const std::string baseUri = env->GetStringUTFChars(apiUri, NULL);
    auto song = retrieveSong(tok, baseUri);

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


extern "C"
JNIEXPORT void
JNICALL
Java_com_example_mear_activities_DemoStreamActivity_pathIteratorDemo(
        JNIEnv *env,
        jobject thisObj,
        jstring path
        ) {

    const std::string pathStr = env->GetStringUTFChars(path, NULL);
    iterateDirectory(pathStr);
}


extern "C"
JNIEXPORT void
JNICALL
Java_com_example_mear_activities_DemoStreamActivity_saveUserCredentials(
        JNIEnv *env,
        jobject thisObj,
        jstring username,
        jstring password,
        jstring appDirectory
) {

    const std::string usernameStr = env->GetStringUTFChars(username, NULL);
    const std::string passwordStr = env->GetStringUTFChars(password, NULL);
    const std::string appDirectoryStr = env->GetStringUTFChars(appDirectory, NULL);

    model::User user;
    user.username = usernameStr;
    user.password = passwordStr;

    saveCredentials(user, appDirectoryStr);
}
