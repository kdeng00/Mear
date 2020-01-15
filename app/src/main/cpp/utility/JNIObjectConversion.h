//
// Created by brahmix on 1/13/20.
//

#ifndef MEAR_JNIOBJECTCONVERSION_H
#define MEAR_JNIOBJECTCONVERSION_H

#include <string>
#include <algorithm>
#include <memory>

#include "../model/Token.h"
#include "../model/User.h"

namespace utility {
    template<typename SomeClass, typename Obj = jobject>
    class JNIObjectConversion {
    public:
        // jobject to classes
        template<typename JE = JNIEnv>
        static SomeClass ObjToToken(JE *env, Obj tokenObj) {
            auto tokenClass = env->GetObjectClass(tokenObj);

            auto accessTokenId = env->GetFieldID(tokenClass, "accessToken", "Ljava/lang/String;");

            auto accessTokenVal = static_cast<jstring>(
                    env->GetObjectField(tokenObj, accessTokenId));

            SomeClass token(std::move(env->GetStringUTFChars(accessTokenVal, nullptr)));

            return token;
        }

        template<typename JE = JNIEnv>
        static SomeClass ObjToUser(JE *env, Obj userObj) {
            auto userClass = env->GetObjectClass(userObj);

            auto usernameId = env->GetFieldID(userClass, "username", "Ljava/lang/String;");
            auto passwordId = env->GetFieldID(userClass, "password", "Ljava/lang/String;");

            auto usernameVal = static_cast<jstring>(env->GetObjectField(userObj, usernameId));
            auto passwordVal = static_cast<jstring>(env->GetObjectField(userObj, passwordId));

            SomeClass user(std::move(env->GetStringUTFChars(usernameVal, nullptr)),
                    std::move(env->GetStringUTFChars(passwordVal, nullptr)));

            return user;
        }

        template<typename JE = JNIEnv>
        static SomeClass ObjToAPIInfo(JE *env, Obj apiObj) {
            auto apiClass = env->GetObjectClass(apiObj);

            auto uriId = env->GetFieldID(apiClass, "uri", "Ljava/lang/String;");
            auto versionId = env->GetFieldID(apiClass, "version", "I");

            auto uriVal = static_cast<jstring>(env->GetObjectField(apiObj, uriId));
            auto versionVal = env->GetIntField(apiObj, versionId);

            SomeClass apiInfo(std::move(env->GetStringUTFChars(uriVal, nullptr)), versionVal);

            return apiInfo;
        }

        template<typename JE = JNIEnv>
        static SomeClass ObjToCoverArt(JE *env, Obj covObj) {
            auto coverClass = env->GetObjectClass(covObj);

            auto idField = env->GetFieldID(coverClass, "id", "I");
            auto titleField = env->GetFieldID(coverClass, "title", "Ljava/lang/String;");

            auto id = env->GetIntField(covObj, idField);
            auto title = static_cast<jstring>(env->GetObjectField(covObj, titleField));

            SomeClass cover(id, std::move(env->GetStringUTFChars(title, nullptr)));

            return cover;
        }


        // classes to jobject
        template<typename JE = JNIEnv>
        static Obj TokenToObj(JE *env, const SomeClass& token) {
            auto tokenClass = env->FindClass(classBasePath<std::string>("models/Token").c_str());
            auto tokenConstructor = env->GetMethodID(tokenClass, "<init>", "()V");
            auto tokenObj = env->NewObject(tokenClass, tokenConstructor);

            auto accessTokenId = env->GetFieldID(tokenClass, "accessToken", "Ljava/lang/String;");

            env->SetObjectField(tokenObj,
                    accessTokenId, env->NewStringUTF(token.accessToken.c_str()));

            return tokenObj;
        }

        template<typename JE = JNIEnv>
        static Obj UserToObj(JE *env, const SomeClass& user) {
            auto userClass = env->FindClass(classBasePath<std::string>("models/User").c_str());
            auto classConstructor = env->GetMethodID(userClass, "<init>", "()V");
            auto userObj = env->NewObject(userClass, classConstructor);

            auto usernameId = env->GetFieldID(userClass, "username", "Ljava/lang/String;");
            auto passwordId = env->GetFieldID(userClass, "password", "Ljava/lang/String;");

            env->SetObjectField(userObj, usernameId, env->NewStringUTF(user.username.c_str()));
            env->SetObjectField(userObj, passwordId, env->NewStringUTF(user.password.c_str()));

            return userObj;
        }

        template<typename JE = JNIEnv>
        static Obj APIInfoToObj(JE *env, const SomeClass& apiInfo) {
            auto apiClass = env->FindClass(classBasePath<std::string>("models/APIInfo").c_str());
            auto apiConstructor = env->GetMethodID(apiClass, "<init>", "()V");
            auto apiObj = env->NewObject(apiClass, apiConstructor);

            auto uriId = env->GetFieldID(apiClass, "uri", "Ljava/lang/String;");
            auto versionId = env->GetFieldID(apiClass, "version", "I");

            env->SetObjectField(apiObj, uriId, env->NewStringUTF(apiInfo.uri.c_str()));
            env->SetIntField(apiObj, versionId, apiInfo.version);

            return apiObj;
        }


        template<typename Str = std::string>
        static Str classBasePath(const Str& classPath = std::string()) noexcept {
            Str basePath("com/example/mear/");
            if (!classPath.empty()) {
                basePath.append(classPath);
            }

            return basePath;
        }
    };
}

#endif //MEAR_JNIOBJECTCONVERSION_H
