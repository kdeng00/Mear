//
// Created by brahmix on 10/1/19.
//

#include "Tok.h"

#include <cstring>

#include <curl/curl.h>
#include <nlohmann/json.hpp>

namespace manager {
    std::string Tok::fetchToken(const model::User &user, const std::string& uri) {
        CURL *curl;
        CURLcode res;

        curl = curl_easy_init();

        if (!curl) {
            return "none";
        }

        char resp[2048];

        const auto loginUri = fetchLoginUri(uri);
        const auto obj = userJsonString(user);

        curl_easy_setopt(curl, CURLOPT_URL, loginUri.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, obj.c_str());
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, respBodyRetriever);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, resp);
        curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0L);

        res = curl_easy_perform(curl);
        curl_easy_cleanup(curl);

        if (res == CURLE_OK) {
            auto s = nlohmann::json::parse(resp);
            return s["token"].get<std::string>();
        }

        return "failure";
    }


    std::string Tok::fetchLoginUri(const std::string& baseUri)
    {
        std::string uri(baseUri);
        uri.append("/api/v1/login");

        return uri;
    }

    std::string Tok::userJsonString(const model::User& user)
    {
        nlohmann::json usr;
        usr["username"] = user.username;
        usr["password"] = user.password;

        return usr.dump();
    }


    size_t Tok::respBodyRetriever(void* ptr, size_t size, size_t nmemb, char *e)
    {
        std::memcpy(e, ptr, nmemb);
        e[nmemb] = '\0';

        return nmemb;
    }
}
