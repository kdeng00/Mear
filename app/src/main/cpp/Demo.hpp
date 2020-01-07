//
// Created by brahmix on 10/12/19.
//

#ifndef MEAR_DEMO_H
#define MEAR_DEMO_H

#include <iostream>
#include <algorithm>
#include <fstream>
#include <string>
#include <cstring>

#include <android/asset_manager_jni.h>
#include <android/log.h>
#include <android/sharedmem.h>
#include <fcntl.h>
#include <jni.h>
#include <nlohmann/json.hpp>
#include <sys/mman.h>

#include "manager/Tok.h"
#include "manager/DirectoryManager.h"
#include "model/APIInfo.h"
#include "model/CoverArt.h"
#include "model/Song.h"
#include "model/Token.h"
#include "model/User.h"
#include "repository/APIRepository.h"
#include "repository/CoverArtRepository.h"
#include "repository/RepeatRepository.h"
#include "repository/ShuffleRepository.h"
#include "repository/SongRepository.h"
#include "repository/TokenRepository.h"
#include "repository/UserRepository.h"


template<class Song = model::Song>
std::vector<Song> retrieveSongs(const model::Token& token, const std::string& baseUri) {
    std::vector<model::Song> songs;
    repository::SongRepository songRepo;

    return songRepo.fetchSongs(token, baseUri);
}

template<typename Str = std::string>
Str jstringToString(JNIEnv *env, jstring& val) {
    auto myVal = env->GetStringUTFChars(val, nullptr);
    return myVal;
}

template<class APIInfo = model::APIInfo>
APIInfo retrieveAPIInfo(const std::string& path) {
    repository::local::APIRepository apiRepo;

    return apiRepo.retrieveAPIInfo(path);
}

template<typename SongObj, typename JE, class Song = model::Song>
Song ObjToSong(JE *env, SongObj obj) {
    jclass songClass = env->GetObjectClass(obj);

    auto songId = env->GetFieldID( songClass,  "id", "I" );
    auto songTitle = env->GetFieldID( songClass,  "title", "Ljava/lang/String;" );
    auto songAlbum = env->GetFieldID( songClass,  "album", "Ljava/lang/String;" );
    auto songAlbumArtist = env->GetFieldID(songClass, "albumArtist", "Ljava/lang/String;");
    auto songArtist = env->GetFieldID( songClass,  "artist", "Ljava/lang/String;" );
    auto songGenre = env->GetFieldID( songClass,  "genre", "Ljava/lang/String;" );
    auto songDuration = env->GetFieldID( songClass,  "duration", "I" );
    auto songTrack = env->GetFieldID(songClass, "track", "I");
    auto songDisc = env->GetFieldID(songClass, "disc", "I");
    auto songYear = env->GetFieldID( songClass,  "year", "I" );
    auto songCoverArtId = env->GetFieldID(songClass, "coverArtId", "I");
    auto songPathId = env->GetFieldID(songClass, "path", "Ljava/lang/String;");
    auto songFilenameId = env->GetFieldID(songClass, "filename", "Ljava/lang/String;");
    auto songDownloadedId = env->GetFieldID(songClass, "downloaded", "Z");

    auto songIdVal = env->GetIntField(obj, songId);
    auto titleVal = (jstring)env->GetObjectField(obj, songTitle);
    auto albumVal = (jstring)env->GetObjectField(obj, songAlbum);
    auto albumArtistVal = (jstring)env->GetObjectField(obj, songAlbumArtist);
    auto songArtistVal = (jstring)env->GetObjectField(obj, songArtist);
    auto songGenreVal = (jstring)env->GetObjectField(obj, songGenre);
    auto songDurationVal = env->GetIntField(obj, songDuration);
    auto songTrackVal = env->GetIntField(obj, songTrack);
    auto songDiscVal = env->GetIntField(obj, songDisc);
    auto songYearVal = env->GetIntField(obj, songYear);
    auto songCoverArtIdVal = env->GetIntField(obj, songCoverArtId);
    auto songPathVal = (jstring)env->GetObjectField(obj, songPathId);
    auto songFilenameVal = (jstring)env->GetObjectField(obj, songFilenameId);


    Song song;
    song.id = songIdVal;
    song.title = env->GetStringUTFChars(titleVal, nullptr);
    song.artist = env->GetStringUTFChars(songArtistVal, nullptr);
    song.album = env->GetStringUTFChars(albumVal, nullptr);
    song.albumArtist = env->GetStringUTFChars(albumArtistVal, nullptr);
    song.genre = env->GetStringUTFChars(songGenreVal, nullptr);
    song.duration = songDurationVal;
    song.track = songTrackVal;
    song.disc = songDiscVal;
    song.year = songYearVal;
    song.coverArtId = songCoverArtIdVal;
    song.path = (songPathVal == nullptr) ? "" : env->GetStringUTFChars(songPathVal, nullptr);
    song.filename = (songFilenameVal == nullptr) ? "" : env->GetStringUTFChars(songFilenameVal, nullptr);

    return song;
}

template<class Song = model::Song>
Song retrieveSong(const model::Token& token, const Song& song,
                         const std::string& baseUri) {
    repository::SongRepository songRepo;

    return songRepo.retrieveSong(token, song, baseUri);
}

template<class Token = model::Token>
Token fetchToken(const model::User& user, const std::string& apiUri) {
    manager::Tok tokMgr;
    auto token = tokMgr.fetchTokenTrans(user, apiUri);

    return token;
}
template<class Token = model::Token>
Token retrieveSavedToken(const std::string& path) {
    repository::local::TokenRepository tokenRepo;
    if (!tokenRepo.databaseExist(path)) {
        tokenRepo.initializedDatabase(path);
    }
    auto token = tokenRepo.retrieveToken(path);

    return token;
}

template<class User = model::User>
User retrieveCredentials(const std::string& dataPath) {
    repository::local::UserRepository userRepo;
    if (!userRepo.databaseExist(dataPath)) {
        userRepo.initializedDatabase(dataPath);
    }
    auto user = userRepo.retrieveUserCredentials(dataPath);

    return user;
}


int retrieveRepeatMode(const std::string& path) {
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

int retrieveShuffleMode(const std::string& path) {
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


bool doesDatabaseExist(const std::string& dataPath) {
    repository::local::UserRepository userRepo;
    const auto result = userRepo.databaseExist(dataPath);

    return result;
}

bool apiInformationExist(const std::string& dataPath) {
    repository::local::APIRepository apiRepo;

    return apiRepo.isTableEmpty(dataPath);
}

bool doesTokenExist(const std::string& dataPath) {
    repository::local::TokenRepository tokenRepo;

    return tokenRepo.isTableEmpty(dataPath);
}

bool userCredentialExist(const std::string& dataPath) {
    repository::local::UserRepository userRepo;

    return userRepo.isTableEmpty(dataPath);
}


void saveAPIInfo(const model::APIInfo& apiInfo, const std::string& path) {
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

void saveCredentials(const model::User& user, const std::string& appDirectory) {
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

void saveToken(const model::Token& token, const std::string& path) {
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

void updateRepeatMode(const std::string& path) {
    repository::local::RepeatRepository repeatRepo;
    if (!repeatRepo.databaseExist(path)) {
        repeatRepo.initializedDatabase(path);
    }
    repeatRepo.updateRepeat(path);
}

void updateShuffleMode(const std::string& path) {
    repository::local::ShuffleRepository shuffleRepo;
    if (!shuffleRepo.databaseExist(path)) {
        shuffleRepo.initializedDatabase(path);
    }
    shuffleRepo.updateShuffle(path);
}

template<class Song = model::Song, class Token = model::Token, typename Str = std::string>
void downloadSong(Song& song, const Token& token, const Str& path) {
    song.filename = "track";
    song.path = path;
    repository::local::APIRepository apiRepo;
    auto apiInfo = apiRepo.retrieveAPIInfo(path);
    repository::SongRepository songRepo;
    auto downloadedSong = songRepo.downloadSong(token, song, apiInfo);
    manager::DirectoryManager dirMgr;
    if (dirMgr.doesSongExist(song, path)) {
        std::cout << "song already exists\n";
        return;
    }

    dirMgr.createSongDirectory(song, path);
    downloadedSong.path = dirMgr.fullSongPath(song, path);
    std::fstream saveSong(downloadedSong.path, std::ios::out | std::ios::binary);
    saveSong.write((char*)&downloadedSong.data[0], downloadedSong.data.size());
    saveSong.close();

    repository::local::SongRepository localSongRepo;
    if (!localSongRepo.databaseExist(path)) {
        localSongRepo.initializedDatabase(path);
    }

    if (!localSongRepo.doesTableExist(path)) {
        localSongRepo.createSongTable(path);
    }

    localSongRepo.saveSong(downloadedSong, path);
}


template<class Song = model::Song>
jobject songToObj(JNIEnv *env, const Song& song) {
    jclass songClass = env->FindClass( "com/example/mear/models/Song");
    jmethodID jconstructor = env->GetMethodID( songClass,  "<init>", "()V" );
    jobject songObj = env->NewObject( songClass, jconstructor );

    auto songId = env->GetFieldID( songClass,  "id", "I" );
    auto songTitle = env->GetFieldID( songClass,  "title", "Ljava/lang/String;" );
    auto songAlbum = env->GetFieldID( songClass,  "album", "Ljava/lang/String;" );
    auto songAlbumArtist = env->GetFieldID(songClass, "albumArtist", "Ljava/lang/String;");
    auto songArtist = env->GetFieldID( songClass,  "artist", "Ljava/lang/String;" );
    auto songGenre = env->GetFieldID( songClass,  "genre", "Ljava/lang/String;" );
    auto songYear = env->GetFieldID( songClass,  "year", "I" );
    auto songDuration = env->GetFieldID( songClass,  "duration", "I" );
    auto songTrack = env->GetFieldID(songClass, "track", "I");
    auto songDisc = env->GetFieldID(songClass, "disc", "I");
    auto songCoverArtId = env->GetFieldID(songClass, "coverArtId", "I");
    auto songDownloadedId = env->GetFieldID(songClass, "downloaded", "Z");
    auto songPathId = env->GetFieldID(songClass, "path", "Ljava/lang/String;");
    auto songFilenameId = env->GetFieldID(songClass, "filename", "Ljava/lang/String;");

    auto songPath = (song.path.empty()) ? nullptr : env->NewStringUTF(song.path.c_str());
    auto songFilename = (song.filename.empty()) ? nullptr : env->NewStringUTF(song.filename.c_str());

    env->SetIntField(songObj, songId, song.id);
    env->SetObjectField(songObj, songTitle, env->NewStringUTF(song.title.c_str()));
    env->SetObjectField(songObj, songAlbum, env->NewStringUTF(song.album.c_str()));
    env->SetObjectField(songObj, songAlbumArtist, env->NewStringUTF(song.albumArtist.c_str()));
    env->SetObjectField(songObj, songArtist, env->NewStringUTF(song.artist.c_str()));
    env->SetObjectField(songObj, songGenre, env->NewStringUTF(song.genre.c_str()));
    env->SetIntField(songObj, songYear, song.year);
    env->SetIntField(songObj, songDuration, song.duration);
    env->SetIntField(songObj, songTrack, song.track);
    env->SetIntField(songObj, songDisc, song.disc);
    env->SetIntField(songObj, songCoverArtId, song.coverArtId);
    env->SetBooleanField(songObj, songDownloadedId, song.downloaded);
    env->SetObjectField(songObj, songPathId, songPath);
    env->SetObjectField(songObj, songFilenameId, songFilename);

    env->DeleteLocalRef(songPath);
    env->DeleteLocalRef(songFilename);

    return songObj;
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
    env->DeleteLocalRef(baseUri);

    auto allSongs = retrieveSongs(tk, uri);
    jobjectArray songs = env->NewObjectArray(allSongs.size(), songClass, nullptr);
    auto i = 0;
    for (auto& sng: allSongs) {
        try {
            auto song = songToObj<model::Song>(env, sng);
            env->SetObjectArrayElement(songs, i++, song);
        } catch (std::exception& ex) {
            auto msg = ex.what();
        }
    }

    return songs;
}

extern "C"
JNIEXPORT jobjectArray
JNICALL
Java_com_example_mear_repositories_TrackRepository_retrieveSongsIncludingDownloaded(
        JNIEnv *env,
        jobject thisOnj,
        jobject token,
        jstring baseUri,
        jstring appPathStr
) {
    jclass tokenClass = env->FindClass("com/example/mear/models/Token");
    jclass songClass = env->FindClass( "com/example/mear/models/Song");

    auto tokField = env->GetFieldID(tokenClass, "accessToken", "Ljava/lang/String;");
    auto tokObj = env->GetObjectField(token, tokField);
    std::string tokStr = env->GetStringUTFChars((jstring)tokObj, nullptr);
    std::string appPath = env->GetStringUTFChars((jstring)appPathStr, nullptr);
    model::Token tk(tokStr);

    const std::string uri = env->GetStringUTFChars(baseUri, nullptr);
    env->DeleteLocalRef(baseUri);

    auto allSongs = retrieveSongs(tk, uri);
    jobjectArray songs = env->NewObjectArray(allSongs.size(), songClass, nullptr);
    auto i = 0;

    repository::local::SongRepository localSongRepo(appPath);
    auto localSongs = (localSongRepo.isTableEmpty(appPath)) ?
            localSongRepo.retrieveAllSongs(appPath) : std::vector<model::Song>();

    for (auto& sng: allSongs) {
        try {
            if (localSongs.size() > 0) {
                auto result = std::any_of(localSongs.begin(), localSongs.end(),
                        [&](model::Song s) {
                   auto result = s.artist.compare(sng.artist) == 0 &&
                       s.title.compare(sng.title) == 0 &&
                       s.album.compare(sng.album) == 0 &&
                       s.albumArtist.compare(sng.albumArtist) == 0;

                   if (result) {
                       auto song = songToObj<model::Song>(env, s);
                       env->SetObjectArrayElement(songs, i++, song);
                   }

                   return result;
                });

                if (result) {
                    continue;
                }
            }

            auto song = songToObj<model::Song>(env, sng);
            env->SetObjectArrayElement(songs, i++, song);
        } catch (std::exception& ex) {
            auto msg = ex.what();
        }
    }

    return songs;
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
Java_com_example_mear_repositories_TrackRepository_downloadSong(
        JNIEnv *env,
        jobject thisObj,
        jobject tokenObj,
        jobject songObj,
        jstring pathStr
) {
    auto tokenClass = env->GetObjectClass(tokenObj);
    auto accessTokenId = env->GetFieldID(tokenClass, "accessToken", "Ljava/lang/String;");
    auto accessTokenVal = (jstring) env->GetObjectField(tokenObj, accessTokenId);
    model::Token token(env->GetStringUTFChars(accessTokenVal, nullptr));

    auto song = ObjToSong<jobject, JNIEnv>(env, songObj);
    auto path = env->GetStringUTFChars(pathStr, nullptr);
    downloadSong(song, token, path);
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


#endif //MEAR_DEMO_H
