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
#include "model/Token.h"
#include "model/User.h"
#include "SongRepository.h"
#include "Tok.h"
#include "UserRepository.h"

std::vector<model::Song> retrieveSongs(const model::Token&, const std::string&);

jobject songToObj(JNIEnv *env, const model::Song&);

model::Song retrieveSong(const std::string&, const std::string&, const int);

model::User retrieveCredentials(const std::string&);

std::string fetchToken(const std::string&, const std::string&, const std::string&);

bool doesDatabaseExist(const std::string&);
bool userCredentialExist(const std::string&);

void saveCredentials(const model::User&, const std::string&);


std::vector<model::Song> retrieveSongs(const model::Token& token, const std::string& baseUri)
{
    std::vector<model::Song> songs;
    repository::SongRepository songRepo;
    songs = songRepo.fetchSongs(token, baseUri);

    return songs;
}


jobject songToObj(JNIEnv *env, const model::Song& song)
{
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


model::Song retrieveSong(const std::string& token, const std::string& baseUri, const int songId)
{
    repository::SongRepository songRepo;
    model::Song song(songId, "Smooth", "Amaze", "The Herb", "Blazing",
            420, 420);

    song = songRepo.retrieveSong(token, baseUri, song);
    auto songs = songRepo.fetchSongs(model::Token(token), baseUri);

    return song;
}


model::User retrieveCredentials(const std::string& dataPath)
{
    repository::local::UserRepository userRepo;
    auto user = userRepo.retrieveUserCredentials(dataPath);

    return user;
}


std::string fetchToken(const std::string& username, const std::string& password,
    const std::string& apiUri)
{
    model::User user(username, password);
    manager::Tok tokMgr;

    auto token = tokMgr.fetchToken(user, apiUri);

    return token;

}


bool doesDatabaseExist(const std::string& dataPath)
{
    repository::local::UserRepository userRepo;
    const auto result = userRepo.databaseExist(dataPath);

    return result;
}

bool userCredentialExist(const std::string& dataPath)
{
    repository::local::UserRepository userRepo;

    return userRepo.isTableEmpty(dataPath);
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
    repository::local::UserRepository userRepo;

    if (!userRepo.databaseExist(appDirectory)) {
        userRepo.initializedDatabase(appDirectory);
    }
    if (!userRepo.doesUserTableExist(appDirectory)) {
        userRepo.createUserTable(appDirectory);
    }

    if (userRepo.isTableEmpty(appDirectory)) {
        userRepo.deleteUserTable(appDirectory);
    }

    userRepo.saveUserCred(user, appDirectory);
}


extern "C"
JNIEXPORT jobjectArray
JNICALL
Java_com_example_mear_activities_IcarusSongActivity_retrieveSongs(
        JNIEnv *env,
        jobject  thisOnj,
        jobject token,
        jstring baseUri
        ) {
    jclass songClass = env->FindClass( "com/example/mear/models/Song");
    jclass tokenClass = env->FindClass("com/example/mear/models/Token");
    jobjectArray songs = env->NewObjectArray(2, songClass, nullptr);
    auto tok = env->CallObjectMethod(token, env->GetMethodID(tokenClass, "setAccessToken", "(Ljava/lang/String;)V"));
    std::string tokStr = env->GetStringUTFChars((jstring)tok, nullptr);
    model::Token tk(tokStr);
    const std::string uri = env->GetStringUTFChars(baseUri, nullptr);

    auto allSongs = retrieveSongs(tk, uri);
    for (auto i = 0; i != allSongs.size(); ++i) {
        auto song = songToObj(env, allSongs[i]);
        env->SetObjectArrayElement(songs, i, song);
    }

    return songs;
}


extern "C"
JNIEXPORT jobject
JNICALL
Java_com_example_mear_activities_LoginActivity_retrieveSong(
        JNIEnv *env,
        jobject thisObj,
        jstring token,
        jstring apiUri,
        jint idOfSong
)
{
    const std::string tok = env->GetStringUTFChars(token, NULL);
    const std::string baseUri = env->GetStringUTFChars(apiUri, NULL);
    auto song = retrieveSong(tok, baseUri, idOfSong);

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
JNIEXPORT jobject
JNICALL
Java_com_example_mear_repositories_UserRepository_retrieveUserCredentials(
        JNIEnv *env,
        jobject thisObj,
        jstring dataPath
) {
    jclass userClass = env->FindClass("com/example/mear/models/User");
    jmethodID userContructor = env->GetMethodID(userClass, "<init>", "()V");
    jobject userObj = env->NewObject(userClass, userContructor);

    const std::string dataPathStr = env->GetStringUTFChars(dataPath, nullptr);
    auto user = retrieveCredentials(dataPathStr);

    jmethodID usernameId = env->GetMethodID(userClass, "setUsername", "(Ljava/lang/String;)V");
    jmethodID passwordId = env->GetMethodID(userClass, "setPassword", "(Ljava/lang/String;)V");

    jstring username = env->NewStringUTF(user.username.c_str());
    jstring password = env->NewStringUTF(user.password.c_str());

    env->CallVoidMethod(userObj, usernameId, username);
    env->CallVoidMethod(userObj, passwordId, password);

    return userObj;
}

extern "C"
JNIEXPORT jobject
JNICALL
Java_com_example_mear_repositories_UserRepository_logUser(
        JNIEnv *env,
        jobject thisObj,
        jobject user,
        jstring apiUri ) {
    jclass userClass = env->GetObjectClass(user);
    //auto tok = env->CallObjectMethod(token, env->GetMethodID(tokenClass, "setAccessToken", "(Ljava/lang/String;)V"));
    auto passwordField = env->GetFieldID(userClass, "getPassword", "(Ljava/lang/String;)V");
    //auto passwordM = env->CallObjectMethod(user, env->GetMethodID(userClass, "password", "(Ljava/lang/String;)V"));
    //auto passwordMOne = env->CallObjectMethod(user, env->GetMethodID(userClass, "getPassword", "(Ljava/lang/String;)V"));
    auto password = env->GetObjectField(user, passwordField);
    //auto username = env->CallObjectMethod(user, env->GetMethodID(userClass, "setUsername", "(Ljava/lang/String;)V"));
    auto usernameField = env->GetFieldID(userClass, "setUsername", "(Ljava/lang/String;)V");
    auto username = env->GetObjectField(user, usernameField);

    const std::string usr = env->GetStringUTFChars((jstring)username, nullptr);
    const std::string pass = env->GetStringUTFChars((jstring)password, nullptr);
    const std::string api = env->GetStringUTFChars(apiUri, nullptr);

    model::Token token(fetchToken(usr, pass, api));

    jclass tokenClass = env->FindClass( "com/example/mear/models/Token");
    jmethodID jconstructor = env->GetMethodID( tokenClass,  "<init>", "()V" );
    jobject tokenObj = env->NewObject( tokenClass, jconstructor );

    jmethodID tokenAccess = env->GetMethodID( tokenClass,  "setAccessToken", "(Ljava/lang/String;)V" );

    jstring title = env->NewStringUTF(token.accessToken.c_str());

    env->CallVoidMethod(tokenObj, tokenAccess, title);

    return tokenObj;
}


extern "C"
JNIEXPORT jstring
JNICALL
Java_com_example_mear_activities_LoginActivity_logUser(
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
JNIEXPORT jboolean
JNICALL
Java_com_example_mear_repositories_UserRepository_isUserTableEmpty(
        JNIEnv *env,
        jobject thisObj,
        jstring dataPath
        ) {
    const std::string dataPathStr = env->GetStringUTFChars(dataPath, NULL);
    jboolean result = userCredentialExist(dataPathStr);

    return result;
}

extern "C"
JNIEXPORT jboolean
JNICALL
Java_com_example_mear_repositories_BaseRepository_doesDatabaseExist(
        JNIEnv *env,
        jobject thidObj,
        jstring dataPath
        ) {
    const std::string dataPathStr = env->GetStringUTFChars(dataPath, NULL);
    jboolean result = doesDatabaseExist(dataPathStr);

    return result;
}


extern "C"
JNIEXPORT void
JNICALL
Java_com_example_mear_activities_LoginActivity_pathIteratorDemo(
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
Java_com_example_mear_activities_LoginActivity_saveUserCredentials(
        JNIEnv *env,
        jobject thisObj,
        jstring username,
        jstring password,
        jstring appDirectory
) {
    const std::string usernameStr = env->GetStringUTFChars(username, NULL);
    const std::string passwordStr = env->GetStringUTFChars(password, NULL);
    const std::string appDirectoryStr = env->GetStringUTFChars(appDirectory, NULL);

    model::User user(usernameStr, passwordStr);

    saveCredentials(user, appDirectoryStr);
}
