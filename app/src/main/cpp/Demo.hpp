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
#include "utility/JNIObjectConversion.h"


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


template<class Song = model::Song, typename Str = std::string, typename B = bool>
B deleteSong(Song& song, const Str& path) {
    manager::DirectoryManager dirMgr;
    if (!dirMgr.doesSongExist(song, path)) {
        return false;
    }

    auto result = dirMgr.deleteSong(song, path);
    if (!result) {
        return result;
    }

    repository::local::SongRepository songRepo(path);
    songRepo.deleteSongFromTable(song, path);

    return result;
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
    manager::DirectoryManager dirMgr;
    if (dirMgr.doesSongExist(song, path)) {
        std::cout << "song already exists\n";
        return;
    }

    auto downloadedSong = songRepo.downloadSong(token, song, apiInfo);

    dirMgr.createSongDirectory(song, path);
    downloadedSong.path = dirMgr.fullSongPath(song, path);
    std::fstream saveSong(downloadedSong.path, std::ios::out | std::ios::binary);
    saveSong.write((char*)&downloadedSong.data[0], downloadedSong.data.size());
    saveSong.close();

    song.path = downloadedSong.path;
    song.downloaded = true;
    repository::local::SongRepository localSongRepo(path);
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
    jclass songClass = env->FindClass( "com/example/mear/models/Song");

    auto tk = utility::JNIObjectConversion<model::Token>::ObjToToken(env, token);

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
    jclass songClass = env->FindClass( "com/example/mear/models/Song");

    std::string appPath = env->GetStringUTFChars((jstring)appPathStr, nullptr);
    auto tk = utility::JNIObjectConversion<model::Token>::ObjToToken(env, token);

    const std::string uri = env->GetStringUTFChars(baseUri, nullptr);
    env->DeleteLocalRef(baseUri);

    auto allSongs = retrieveSongs(tk, uri);
    jobjectArray songs = env->NewObjectArray(allSongs.size(), songClass, nullptr);
    auto i = 0;

    repository::local::SongRepository localSongRepo(appPath);
    auto localSongs = (!localSongRepo.isTableEmpty(appPath)) ?
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
    auto token = utility::JNIObjectConversion<model::Token>::ObjToToken(env, tokenObj);

    auto cover = utility::JNIObjectConversion<model::CoverArt>::
            ObjToCoverArt(env, coverArt);

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

    auto apiInfoObj = utility::JNIObjectConversion<model::APIInfo>::
            APIInfoToObj(env, apiInfo);

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

    auto tokenObj = utility::JNIObjectConversion<model::Token>::TokenToObj(env, token);

    return tokenObj;
}

extern "C"
JNIEXPORT jobject
JNICALL
Java_com_example_mear_repositories_TrackRepository_downloadSong(
        JNIEnv *env,
        jobject thisObj,
        jobject tokenObj,
        jobject songObj,
        jstring pathStr
) {
    auto token = utility::JNIObjectConversion<model::Token>::ObjToToken(env, tokenObj);

    auto song = ObjToSong<jobject, JNIEnv>(env, songObj);
    auto path = env->GetStringUTFChars(pathStr, nullptr);
    downloadSong(song, token, path);

    return songToObj(env, song);
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
    auto songClass = env->GetObjectClass(songObj);

    auto idField = env->GetFieldID(songClass, "id", "I");

    auto idInt = env->GetIntField(songObj, idField);

    auto uri = env->GetStringUTFChars(uriStr, nullptr);
    auto token = utility::JNIObjectConversion<model::Token>::ObjToToken(env, tokenObj);

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
    const std::string dataPathStr = env->GetStringUTFChars(dataPath, nullptr);
    auto user = retrieveCredentials(dataPathStr);

    auto userObj = utility::JNIObjectConversion<model::User>::UserToObj<JNIEnv>(env, user);

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
    const std::string api = env->GetStringUTFChars(apiUri, nullptr);

    auto us = utility::JNIObjectConversion<model::User>::ObjToUser(env, user);

    auto token = fetchToken(us, api);

    auto tokenObj = utility::JNIObjectConversion<model::Token>::TokenToObj(env, token);

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
Java_com_example_mear_repositories_TrackRepository_deleteSong(
        JNIEnv *env,
        jobject thisObj,
        jobject songObj,
        jstring pathStr
) {
    auto songClass = env->GetObjectClass(songObj);
    auto path = env->GetStringUTFChars(pathStr, nullptr);
    auto song = ObjToSong(env, songObj);

    const auto result = deleteSong(song, path);

    return result;
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
    auto apiInfo = utility::JNIObjectConversion<model::APIInfo>::
            ObjToAPIInfo(env, apiInfoObj);
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
    auto token = utility::JNIObjectConversion<model::Token>::ObjToToken(env, tokenObj);
    auto path = env->GetStringUTFChars(pathStr, nullptr);

    saveToken(token, path);
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
    const std::string appDirectoryStr = env->GetStringUTFChars(appDirectory, nullptr);

    auto usr = utility::JNIObjectConversion<model::User>::ObjToUser<JNIEnv>(env, user);

    saveCredentials(usr, appDirectoryStr);
}


#endif //MEAR_DEMO_H
