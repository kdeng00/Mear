//
// Created by brahmix on 10/1/19.
//

#ifndef MEAR_TOK_H
#define MEAR_TOK_H

#include <algorithm>
#include <memory>
#include <string>
#include <cstring>

#include <curl/curl.h>
#include <nlohmann/json.hpp>

#include "../model/Token.h"
#include "../model/User.h"

namespace manager {
    template<class Token = model::Token>
    class Tok {
    public:
        template<class User = model::User>
        Token fetchTokenTrans(const User& user, const std::string& uri) {
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
            chunk = curl_slist_append(chunk, contentType);

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

                return Token(std::move(tokenStr));
            }

            return Token("failure");
        }
    private:
        std::string fetchLoginUri(const std::string& uriBase) noexcept {
            std::string uri(uriBase);
            if (uri.at(uri.size() - 1) != '/') {
                uri.append("/");
            }
            uri.append(loginEndpoint());

            return uri;
        }

        std::string userJsonString(const model::User& user) {
            nlohmann::json usr;
            usr["username"] = user.username;
            usr["password"] = user.password;

            return usr.dump();
        }


        constexpr auto loginEndpoint() noexcept {
            return "api/v1/login";
        }


        static size_t respBodyRetriever(void* ptr, size_t size, size_t nmemb, char* e) {
            ((std::string*)e)->append((char*)ptr, size * nmemb);

            return size * nmemb;
        }
    };
}


#endif //MEAR_TOK_H
