//
// Created by brahmix on 10/12/19.
//

#ifndef MEAR_DEMO_H
#define MEAR_DEMO_H

#include <iostream>

#include <jni.h>

#include "model/APIInfo.h"
#include "model/Song.h"
#include "model/Token.h"
#include "model/User.h"


std::vector<model::Song> retrieveSongs(const model::Token&, const std::string&);

jobject songToObj(JNIEnv *env, const model::Song&);

model::APIInfo retrieveAPIInfo(const std::string&);

model::Song retrieveSong(const std::string&, const std::string&, const int);
model::Song retrieveSong(const model::Token&, const model::Song&, const std::string&);

model::Token fettchToken(const model::User&, const std::string&);
model::Token retrieveSavedToken(const std::string&);
void saveToken(const model::Token&, const std::string&);

model::User retrieveCredentials(const std::string&);

bool doesDatabaseExist(const std::string&);
bool apiInformationExist(const std::string&);
bool doesTokenExist(const std::string&);
bool userCredentialExist(const std::string&);

void saveApiInfo(const model::APIInfo&, const std::string&);
void saveCredentials(const model::User&, const std::string&);


#endif //MEAR_DEMO_H
