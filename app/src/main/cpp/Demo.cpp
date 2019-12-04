//
// Created by brahmix on 9/26/19.
//
#include "Demo.h"

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
#include <curl/curl.h>
#include <nlohmann/json.hpp>
#include <sqlite3.h>
#include <sys/mman.h>

#include "repository/APIRepository.h"
#include "repository/CoverArtRepository.h"
#include "repository/RepeatRepository.h"
#include "repository/ShuffleRepository.h"
#include "repository/SongRepository.h"
#include "manager/Tok.h"
#include "repository/TokenRepository.h"
#include "repository/UserRepository.h"



std::vector<model::Song> retrieveSongs(const model::Token& token, const std::string& baseUri)
{
    std::vector<model::Song> songs;
    repository::SongRepository songRepo;

    return songRepo.fetchSongs(token, baseUri);
}


jobject songToObj(JNIEnv *env, const model::Song& song)
{
    jclass songClass = env->FindClass( "com/example/mear/models/Song");
    jmethodID jconstructor = env->GetMethodID( songClass,  "<init>", "()V" );
    jobject songObj = env->NewObject( songClass, jconstructor );

    jmethodID songId = env->GetMethodID( songClass,  "setId", "(I)V" );
    jmethodID songTitle = env->GetMethodID( songClass,  "setTitle", "(Ljava/lang/String;)V" );
    jmethodID songAlbum = env->GetMethodID( songClass,  "setAlbum", "(Ljava/lang/String;)V" );
    jmethodID songAlbumArtist = env->GetMethodID(songClass, "setAlbumArtist", "(Ljava/lang/String;)V");
    jmethodID songArtist = env->GetMethodID( songClass,  "setArtist", "(Ljava/lang/String;)V" );
    jmethodID songGenre = env->GetMethodID( songClass,  "setGenre", "(Ljava/lang/String;)V" );
    jmethodID songDuration = env->GetMethodID( songClass,  "setDuration", "(I)V" );
    jmethodID songYear = env->GetMethodID( songClass,  "setYear", "(I)V" );
    jmethodID songCoverArtId = env->GetMethodID(songClass, "setCoverArtId", "(I)V");
    jmethodID songPathId = env->GetMethodID(songClass, "setPath", "(Ljava/lang/String;)V");
    jmethodID songFilenameId = env->GetMethodID(songClass, "setFilename", "(Ljava/lang/String;)V");
    jmethodID songDownloadedId = env->GetMethodID(songClass, "setDownloaded", "(B)V");

    jint id = song.id;
    jstring title = env->NewStringUTF(song.title.c_str());
    jstring album = env->NewStringUTF(song.album.c_str());
    jstring albumArtist = env->NewStringUTF(song.albumArtist.c_str());
    jstring artist = env->NewStringUTF(song.artist.c_str());
    jstring genre = env->NewStringUTF(song.genre.c_str());
    jint duration = song.duration;
    jint year = song.year;
    jint coverArtId = song.coverArtId;
    jstring songPath = env->NewStringUTF(song.path.c_str());
    jstring songFilename = env->NewStringUTF(song.filename.c_str());
    jboolean songDownloaded = song.downloaded;


    env->CallVoidMethod( songObj, songId, id );
    env->CallVoidMethod(songObj, songTitle, title);
    env->CallVoidMethod(songObj, songAlbum, album);
    env->CallVoidMethod(songObj, songAlbumArtist, albumArtist);
    env->CallVoidMethod(songObj, songArtist, artist);
    env->CallVoidMethod(songObj, songGenre, genre);
    env->CallVoidMethod(songObj, songDuration, duration);
    env->CallVoidMethod(songObj, songYear, year);
    env->CallVoidMethod(songObj, songCoverArtId, coverArtId);
    env->CallVoidMethod(songObj, songPathId, songPath);
    env->CallVoidMethod(songObj, songFilenameId, songFilename);
    env->CallVoidMethod(songObj, songDownloadedId, songDownloaded);

    env->DeleteLocalRef(title);
    env->DeleteLocalRef(album);
    env->DeleteLocalRef(albumArtist);
    env->DeleteLocalRef(artist);
    env->DeleteLocalRef(genre);
    env->DeleteLocalRef(songPath);
    env->DeleteLocalRef(songFilename);

    return songObj;
}


std::string jstringToString(JNIEnv *env, jstring& val)
{
    return env->GetStringUTFChars(val, nullptr);
}


model::APIInfo retrieveAPIInfo(const std::string& path)
{
    repository::local::APIRepository apiRepo;

    return apiRepo.retrieveAPIInfo(path);
}


model::Song retrieveSong(const model::Token& token, const model::Song& song,
        const std::string& baseUri)
{
    repository::SongRepository songRepo;

    return songRepo.retrieveSong(token, song, baseUri);
}


model::Token fetchToken(const model::User& user, const std::string& apiUri)
{
    manager::Tok tokMgr;
    auto token = tokMgr.fetchTokenTrans(user, apiUri);

    return token;
}

model::Token retrieveSavedToken(const std::string& path)
{
    repository::local::TokenRepository tokenRepo;
    if (!tokenRepo.databaseExist(path)) {
        tokenRepo.initializedDatabase(path);
    }
    auto token = tokenRepo.retrieveToken(path);

    return token;
}


model::User retrieveCredentials(const std::string& dataPath)
{
    repository::local::UserRepository userRepo;
    if (!userRepo.databaseExist(dataPath)) {
        userRepo.initializedDatabase(dataPath);
    }
    auto user = userRepo.retrieveUserCredentials(dataPath);

    return user;
}


int retrieveRepeatMode(const std::string& path)
{
    repository::local::RepeatRepository repeatRepo;
    if (!repeatRepo.databaseExist(path)) {
        repeatRepo.initializedDatabase(path);
    }
    if (!repeatRepo.doesTableExist(path)) {
        repeatRepo.createRepeatTable(path);
    }

    auto repeatMode = repeatRepo.retrieveRepeatMode(path);

    return static_cast<int>(repeatMode);
}

int retrieveShuffleMode(const std::string& path)
{
    repository::local::ShuffleRepository shuffleRepo;
    if (!shuffleRepo.databaseExist(path)) {
        shuffleRepo.initializedDatabase(path);
    }
    if (!shuffleRepo.doesTableExist(path)) {
        shuffleRepo.createShuffleTable(path);
    }

    auto shuffleMode = shuffleRepo.retrieveShuffleMode(path);

    return static_cast<int>(shuffleMode);
}


bool doesDatabaseExist(const std::string& dataPath)
{
    repository::local::UserRepository userRepo;
    const auto result = userRepo.databaseExist(dataPath);

    return result;
}

bool apiInformationExist(const std::string& dataPath)
{
    repository::local::APIRepository apiRepo;

    return apiRepo.isTableEmpty(dataPath);
}

bool doesTokenExist(const std::string& dataPath)
{
    repository::local::TokenRepository tokenRepo;

    return tokenRepo.isTableEmpty(dataPath);
}

bool userCredentialExist(const std::string& dataPath)
{
    repository::local::UserRepository userRepo;

    return userRepo.isTableEmpty(dataPath);
}


void saveAPIInfo(const model::APIInfo& apiInfo, const std::string& path)
{
    repository::local::APIRepository apiRepo;
    if (!apiRepo.databaseExist(path)) {
        apiRepo.initializedDatabase(path);
    }
    if (!apiRepo.doesTableExist(path)) {
        apiRepo.createAPiInfoTable(path);
    }
    if (!apiRepo.isTableEmpty(path)) {
        apiRepo.deleteAPIInfo(apiInfo, path);
    }

    apiRepo.saveAPIInfo(apiInfo, path);
}

void saveCredentials(const model::User& user, const std::string& appDirectory)
{
    repository::local::UserRepository userRepo;

    if (!userRepo.databaseExist(appDirectory)) {
        userRepo.initializedDatabase(appDirectory);
    }
    if (!userRepo.doesTableExist(appDirectory)) {
        userRepo.createUserTable(appDirectory);
    }

    if (!userRepo.isTableEmpty(appDirectory)) {
        userRepo.deleteUserTable(appDirectory);
    }

    userRepo.saveUserCred(user, appDirectory);
}

void saveToken(const model::Token& token, const std::string& path)
{
    repository::local::TokenRepository tokenRepo;
    if (!tokenRepo.databaseExist(path)) {
        tokenRepo.initializedDatabase(path);
    }
    if (!tokenRepo.doesTableExist(path)) {
        tokenRepo.createTokenTable(path);
    }
    if (!tokenRepo.isTableEmpty(path)) {
        tokenRepo.deleteRecord(path);
    }

    tokenRepo.saveToken(token, path);
}

void updateRepeatMode(const std::string& path)
{
    repository::local::RepeatRepository repeatRepo;
    if (!repeatRepo.databaseExist(path)) {
        repeatRepo.initializedDatabase(path);
    }
    repeatRepo.updateRepeat(path);
}

void updateShuffleMode(const std::string& path)
{
    repository::local::ShuffleRepository shuffleRepo;
    if (!shuffleRepo.databaseExist(path)) {
        shuffleRepo.initializedDatabase(path);
    }
    shuffleRepo.updateShuffle(path);
}


extern "C"
JNIEXPORT jobjectArray
JNICALL
Java_com_example_mear_repositories_TrackRepository_retrieveSongs(
        JNIEnv *env,
        jobject thisOnj,
        jobject token,
        jstring baseUri
        ) {
    jclass tokenClass = env->FindClass("com/example/mear/models/Token");
    jclass songClass = env->FindClass( "com/example/mear/models/Song");

    auto tokField = env->GetFieldID(tokenClass, "accessToken", "Ljava/lang/String;");
    auto tokObj = env->GetObjectField(token, tokField);
    std::string tokStr = env->GetStringUTFChars((jstring)tokObj, nullptr);
    model::Token tk(tokStr);

    const std::string uri = env->GetStringUTFChars(baseUri, nullptr);

    auto allSongs = retrieveSongs(tk, uri);
    jobjectArray songs = env->NewObjectArray(allSongs.size(), songClass, nullptr);
    for (auto i = 0; i != allSongs.size(); ++i) {
        try {
            auto song = songToObj(env, allSongs[i]);
            env->SetObjectArrayElement(songs, i, song);
        } catch (std::exception& ex) {
            auto msg = ex.what();
        }
    }

    auto curPath = "/data/data/com.example.mear/";
    std::string filename;
    std::string currPathStr(curPath);
    std::fstream tmp;
    if (currPathStr.at(currPathStr.size() - 1) == '/') {
        filename.assign(currPathStr);
    } else {
        filename.assign(currPathStr);
        filename.append("/");
    }
    filename.append("test.txt");
    tmp.open(filename.c_str(), std::ios::out);
    auto content = "hello fs";
    tmp.write(content, std::strlen(content));
    tmp.close();

    return songs;
}


extern "C"
JNIEXPORT jobject
JNICALL
Java_com_example_mear_repositories_APIRepository_retrieveAPIInfoRecord(
        JNIEnv *env,
        jobject thisObj,
        jstring pathStr ) {
    const auto path = env->GetStringUTFChars(pathStr, nullptr);

    auto apiInfo = retrieveAPIInfo(path);

    jclass apiInfoClass = env->FindClass( "com/example/mear/models/APIInfo");
    jmethodID jconstructor = env->GetMethodID( apiInfoClass,  "<init>", "()V" );
    jobject apiInfoObj = env->NewObject( apiInfoClass, jconstructor );

    jmethodID uriId = env->GetMethodID( apiInfoClass,  "setUri", "(Ljava/lang/String;)V" );
    jmethodID versionId = env->GetMethodID( apiInfoClass,  "setVersion", "(I)V" );

    jint versionVal = apiInfo.version;
    jstring uriVal = env->NewStringUTF(apiInfo.uri.c_str());

    env->CallVoidMethod(apiInfoObj, uriId, uriVal);
    env->CallVoidMethod( apiInfoObj, versionId, versionVal);

    return apiInfoObj;
}

extern "C"
JNIEXPORT jobject
JNICALL
Java_com_example_mear_repositories_TokenRepository_retrieveTokenRecord(
        JNIEnv *env,
        jobject thisObj,
        jstring pathStr
        ) {
    const auto path = env->GetStringUTFChars(pathStr, nullptr);
    auto token = retrieveSavedToken(path);

    auto tokenClass = env->FindClass("com/example/mear/models/Token");
    auto jConstructor = env->GetMethodID(tokenClass, "<init>", "()V");
    auto tokenObj = env->NewObject(tokenClass, jConstructor);

    auto accessTokenId = env->GetMethodID(tokenClass, "setAccessToken", "(Ljava/lang/String;)V");

    auto accessTokenStr = env->NewStringUTF(token.accessToken.c_str());

    env->CallVoidMethod(tokenObj, accessTokenId, accessTokenStr);

    return tokenObj;
}

extern "C"
JNIEXPORT jobject
JNICALL
Java_com_example_mear_repositories_TrackRepository_retrieveSong(
        JNIEnv *env,
        jobject thisObj,
        jobject tokenObj,
        jobject songObj,
        jstring uriStr
        ) {
    auto tokenClass = env->GetObjectClass(tokenObj);
    auto songClass = env->GetObjectClass(songObj);

    auto accessTokenField = env->GetFieldID(tokenClass, "accessToken", "Ljava/lang/String;");
    auto idField = env->GetFieldID(songClass, "id", "I");

    auto accessTokenStr = (jstring)env->GetObjectField(tokenObj, accessTokenField);
    auto idInt = env->GetIntField(songObj, idField);

    auto uri = env->GetStringUTFChars(uriStr, nullptr);
    model::Token token(env->GetStringUTFChars(accessTokenStr, nullptr));
    model::Song song(idInt);

    song = retrieveSong(token, song, uri);
    auto fetchedSongObj = songToObj(env, song);

    return fetchedSongObj;
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
    auto passwordField = env->GetFieldID(userClass, "password", "Ljava/lang/String;");
    auto usernameField = env->GetFieldID(userClass, "username", "Ljava/lang/String;");

    auto password = (jstring)env->GetObjectField(user, passwordField);
    auto username = (jstring)env->GetObjectField(user, usernameField);

    const std::string api = env->GetStringUTFChars(apiUri, nullptr);
    model::User us(env->GetStringUTFChars(username, nullptr),
            env->GetStringUTFChars(password, nullptr));

    auto token = fetchToken(us, api);

    jclass tokenClass = env->FindClass( "com/example/mear/models/Token");
    jmethodID jconstructor = env->GetMethodID( tokenClass,  "<init>", "()V" );
    jobject tokenObj = env->NewObject( tokenClass, jconstructor );

    jmethodID tokenAccess = env->GetMethodID( tokenClass,  "setAccessToken", "(Ljava/lang/String;)V" );

    jstring title = env->NewStringUTF(token.accessToken.c_str());

    env->CallVoidMethod(tokenObj, tokenAccess, title);

    return tokenObj;
}


extern "C"
JNIEXPORT jbyteArray
JNICALL
Java_com_example_mear_repositories_CoverArtRepository_retrieveCoverArtImage(
        JNIEnv *env,
        jobject thisObj,
        jobject tokenObj,
        jobject coverArt,
        jstring apiUri
        ) {
    jclass coverArtClass = env->GetObjectClass(coverArt);
    auto idField = env->GetFieldID(coverArtClass, "id", "I");
    auto titleField = env->GetFieldID(coverArtClass, "title", "Ljava/lang/String;");
    auto id = env->GetIntField(coverArt, idField);
    auto title = (jstring)env->GetObjectField(coverArt, titleField);

    jclass tokenClass = env->GetObjectClass(tokenObj);
    auto accessTokenId = env->GetFieldID(tokenClass, "accessToken", "Ljava/lang/String;");
    auto accessTokenVal = (jstring) env->GetObjectField(tokenObj, accessTokenId);

    model::Token token;
    token.accessToken = env->GetStringUTFChars(accessTokenVal, nullptr);

    model::CoverArt cover(id, env->GetStringUTFChars(title, nullptr));
    repository::CoverArtRepository<model::CoverArt> coverArtRepo;
    auto data = coverArtRepo.retrieveCoverArtData(token, cover, env->GetStringUTFChars(apiUri, nullptr));

    jbyteArray image = env->NewByteArray(data.size());
    env->SetByteArrayRegion(image, 0, data.size(), (jbyte*) data.data());

    return image;
}


extern "C"
JNIEXPORT jint
JNICALL
Java_com_example_mear_repositories_RepeatRepository_retrieveRepeatMode(
        JNIEnv *env,
        jobject thisObj,
        jstring pathStr
        ) {
    const auto dataPath = jstringToString(env, pathStr);
    auto repeatMode = retrieveRepeatMode(dataPath);

    return repeatMode;
}

extern "C"
JNIEXPORT jint
JNICALL
Java_com_example_mear_repositories_ShuffleRepository_retrieveShuffleMode(
        JNIEnv *env,
        jobject thisObj,
        jstring pathStr
        ) {
    const auto dataPath = jstringToString(env, pathStr);
    auto shuffleMode = retrieveShuffleMode(dataPath);

    return shuffleMode;
}


extern "C"
JNIEXPORT jboolean
JNICALL
Java_com_example_mear_repositories_APIRepository_isAPIInfoTableEmpty(
        JNIEnv *env,
        jobject thisObj,
        jstring pathStr
        ) {
    const auto dataPath = env->GetStringUTFChars(pathStr, nullptr);

    return apiInformationExist(dataPath);
}

extern "C"
JNIEXPORT jboolean
JNICALL
Java_com_example_mear_repositories_TokenRepository_isTokenTableEmpty(
        JNIEnv *env,
        jobject thisObj,
        jstring pathStr
        ) {
    const auto dataPath = env->GetStringUTFChars(pathStr, nullptr);

    return doesTokenExist(dataPath);
}

extern "C"
JNIEXPORT jboolean
JNICALL
Java_com_example_mear_repositories_UserRepository_isUserTableEmpty(
        JNIEnv *env,
        jobject thisObj,
        jstring dataPath
        ) {
    const std::string dataPathStr = env->GetStringUTFChars(dataPath, nullptr);

    return userCredentialExist(dataPathStr);
}

extern "C"
JNIEXPORT jboolean
JNICALL
Java_com_example_mear_repositories_BaseRepository_doesDatabaseExist(
        JNIEnv *env,
        jobject thidObj,
        jstring dataPath
        ) {
    const std::string dataPathStr = env->GetStringUTFChars(dataPath, nullptr);
    jboolean result = doesDatabaseExist(dataPathStr);

    return result;
}


extern "C"
JNIEXPORT void
JNICALL
Java_com_example_mear_repositories_UserRepository_saveUserCredentials(
        JNIEnv *env,
        jobject thisObj,
        jobject user,
        jstring appDirectory
) {
    jclass userClass = env->GetObjectClass(user);
    auto usernameId = env->GetFieldID(userClass, "username", "Ljava/lang/String;");
    auto passwordId = env->GetFieldID(userClass, "password", "Ljava/lang/String;");

    auto usernameStr = (jstring) env->GetObjectField(user, usernameId);
    auto passwordStr = (jstring) env->GetObjectField(user, passwordId);

    const std::string username = env->GetStringUTFChars(usernameStr, nullptr);
    const std::string password = env->GetStringUTFChars(passwordStr, nullptr);
    const std::string appDirectoryStr = env->GetStringUTFChars(appDirectory, nullptr);

    model::User usr(username, password);

    saveCredentials(usr, appDirectoryStr);
}

extern "C"
JNIEXPORT void
JNICALL
Java_com_example_mear_repositories_APIRepository_saveAPIInfoRecord(
        JNIEnv *env,
        jobject thisObj,
        jobject apiInfoObj,
        jstring pathStr
        ) {
    auto apiInfoClass = env->GetObjectClass(apiInfoObj);
    auto uriId = env->GetFieldID(apiInfoClass, "uri", "Ljava/lang/String;");
    auto versionId = env->GetFieldID(apiInfoClass, "version", "I");

    auto uriStr = (jstring) env->GetObjectField(apiInfoObj, uriId);
    auto versionInt = env->GetIntField(apiInfoObj, versionId);

    model::APIInfo apiInfo(env->GetStringUTFChars(uriStr, nullptr), versionInt);
    auto path = env->GetStringUTFChars(pathStr, nullptr);

    saveAPIInfo(apiInfo, path);
}

extern "C"
JNIEXPORT void
JNICALL
Java_com_example_mear_repositories_TokenRepository_saveTokenRecord(
        JNIEnv *env,
        jobject thisObj,
        jobject tokenObj,
        jstring pathStr
        ) {
    auto tokenClass = env->GetObjectClass(tokenObj);
    auto accessTokenId = env->GetFieldID(tokenClass, "accessToken", "Ljava/lang/String;");

    auto accessTokenVal = (jstring) env->GetObjectField(tokenObj, accessTokenId);

    model::Token token(env->GetStringUTFChars(accessTokenVal, nullptr));
    auto path = env->GetStringUTFChars(pathStr, nullptr);

    saveToken(token, path);
}

extern "C"
JNIEXPORT void
JNICALL
Java_com_example_mear_repositories_TrackRepositories_downloadSong(
        JNIEnv *env,
        jobject thisObj,
        jobject tokenObj,
        jobject songObj,
        jstring uriStr
) {
    // TODO: left off here
    auto song = ObjToSong<jobject, JNIEnv>(env, songObj);

}

extern "C"
JNIEXPORT void
JNICALL
Java_com_example_mear_repositories_RepeatRepository_updateRepeatMode(
        JNIEnv *env,
        jobject thisObj,
        jstring pathStr
        ) {
    const auto dataPath = jstringToString(env, pathStr);
    updateRepeatMode(dataPath);
}

extern "C"
JNIEXPORT void
JNICALL
Java_com_example_mear_repositories_ShuffleRepository_updateShuffle(
        JNIEnv *env,
        jobject thisObj,
        jstring pathStr
        ) {
    const auto dataPath = jstringToString(env, pathStr);
    updateShuffleMode(dataPath);
}