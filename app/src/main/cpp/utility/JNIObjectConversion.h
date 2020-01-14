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
    template<typename Obj>
    class JNIObjectConversion {
    public:
        // jobject to classes
        template<typename Token, typename JE>
        static Token ObjToToken(JE *env, Obj tokenObj) {
            auto tokenClass = env->GetObjectClass(tokenObj);

            auto accessTokenId = env->GetFieldId(tokenClass, "accessToken", "Ljava/lang/String;");

            auto accessTokenVal = env->GetObjectField(tokenObj, accessTokenId);

            Token token(std::move(env->GetStringUTFChars(accessTokenVal, nullptr)));

            return token;
        }


        // classes to jobject
        template<typename Token, typename JE>
        static Obj TokenToObj(JE *env, const Token& token) {
            auto tokenClass = env->FindClass("com/example/mear/models/Token");
            auto tokenConstructor = env->GetMethodID(tokenClass, "<init>", "()V");
            auto tokenObj = env->NewObject(tokenClass, tokenConstructor);

            auto accessTokenId = env->GetFieldID(tokenClass, "accessToken", "Ljava/lang/String;");

            // TODO: left off here
        }
    private:
    };
}

#endif //MEAR_JNIOBJECTCONVERSION_H
