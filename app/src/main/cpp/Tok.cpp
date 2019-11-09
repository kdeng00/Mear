//
// Created by brahmix on 10/1/19.
//

#include "Tok.h"

#include <algorithm>
#include <memory>
#include <cstring>

#include <curl/curl.h>
#include <nlohmann/json.hpp>

namespace manager {
    model::Token Tok::fetchTokenTrans(const model::User &user, const std::string& uri) {
        CURL *curl;
        struct curl_slist *chunk = nullptr;
        CURLcode res;

        curl = curl_easy_init();

        if (!curl) {
            return model::Token("none");
        }

        std::string resp;
        const auto loginUri = fetchLoginUri(uri);
        const auto obj = userJsonString(user);

        constexpr auto contentType = "Content-type: Keep-alive";
        chunk = curl_slist_append(chunk , contentType);

        curl_easy_setopt(curl, CURLOPT_URL, loginUri.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, obj.c_str());
        curl_easy_setopt(curl, CURLOPT_HTTPHEADER, chunk);
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, respBodyRetriever);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, &resp);
        curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0L);

        res = curl_easy_perform(curl);
        curl_easy_cleanup(curl);

        if (res == CURLE_OK) {
            auto s = nlohmann::json::parse(resp);
            const auto tokenStr = std::move(s["token"].get<std::string>());

            return model::Token(std::move(tokenStr));
        }

        return model::Token("failure");
    }


    constexpr auto Tok::loginEndpoint() noexcept
    {
        return "api/v1/login";
    }

    std::string Tok::fetchLoginUri(const std::string& uriBase) noexcept
    {
        std::string uri(uriBase);
        if (uri.at(uri.size() - 1) != '/') {
            uri.append("/");
        }
        uri.append(loginEndpoint());

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
        ((std::string*)e)->append((char*)ptr, size * nmemb);

        return size * nmemb;
    }
}
