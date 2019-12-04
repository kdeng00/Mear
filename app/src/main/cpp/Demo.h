//
// Created by brahmix on 10/12/19.
//

#ifndef MEAR_DEMO_H
#define MEAR_DEMO_H

#include <iostream>

#include <jni.h>

#include "model/APIInfo.h"
#include "model/CoverArt.h"
#include "model/Song.h"
#include "model/Token.h"
#include "model/User.h"


std::vector<model::Song> retrieveSongs(const model::Token&, const std::string&);

jobject songToObj(JNIEnv *env, const model::Song&);

std::string jstringToString(JNIEnv*, jstring&);

model::APIInfo retrieveAPIInfo(const std::string&);

template<typename SongObj, typename JE, class Song = model::Song>
Song ObjToSong(JE *env, SongObj obj) {
    jclass songClass = env->GetObjectClass(obj);
    //jmethodID jconstructor = env->GetMethodID( songClass,  "<init>", "()V" );
    //jobject songObj = env->NewObject( songClass, jconstructor );

    auto songId = env->GetFieldID( songClass,  "id", "I" );
    auto songTitle = env->GetFieldID( songClass,  "title", "Ljava/lang/String;" );
    auto songAlbum = env->GetFieldID( songClass,  "album", "Ljava/lang/String;" );
    auto songAlbumArtist = env->GetFieldID(songClass, "albumArtist", "Ljava/lang/String;");
    auto songArtist = env->GetFieldID( songClass,  "artist", "Ljava/lang/String;" );
    auto songGenre = env->GetFieldID( songClass,  "genre", "Ljava/lang/String;" );
    auto songDuration = env->GetFieldID( songClass,  "duration", "I" );
    auto songYear = env->GetFieldID( songClass,  "year", "I" );
    auto songCoverArtId = env->GetFieldID(songClass, "coverArtId", "I");
    auto songPathId = env->GetFieldID(songClass, "path", "Ljava/lang/String;");
    auto songFilenameId = env->GetFieldID(songClass, "filename", "Ljava/lang/String;");
    auto songDownloadedId = env->GetFieldID(songClass, "downloaded", "B");

    auto titleVal = (jstring)env->GetObjectField(obj, songTitle);

    Song song;

    return song;
}

model::Song retrieveSong(const model::Token&, const model::Song&, const std::string&);

model::Token fetchToken(const model::User&, const std::string&);
model::Token retrieveSavedToken(const std::string&);

model::User retrieveCredentials(const std::string&);

int retrieveRepeatMode(const std::string&);
int retrieveShuffleMode(const std::string&);

bool doesDatabaseExist(const std::string&);
bool apiInformationExist(const std::string&);
bool doesTokenExist(const std::string&);
bool userCredentialExist(const std::string&);

void saveAPIInfo(const model::APIInfo&, const std::string&);
void saveCredentials(const model::User&, const std::string&);
void saveToken(const model::Token&, const std::string&);
void updateRepeatMode(const std::string&);
void updateShuffleMode(const std::string&);
template<class Song = model::Song>
void downloadSong(Song& song) {

}



#endif //MEAR_DEMO_H
