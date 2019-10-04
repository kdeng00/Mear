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
#include "Tok.h"


std::string fetchToken(const std::string&, const std::string&, const std::string&);


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
