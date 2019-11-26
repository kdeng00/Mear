//
// Created by brahmix on 11/8/19.
//

#ifndef MEAR_COVERARTREPOSITORY_H
#define MEAR_COVERARTREPOSITORY_H

#include <string>
#include <sstream>
#include <iterator>
#include <vector>

#include <curl/curl.h>

#include "model/CoverArt.h"
#include "model/Token.h"

using data = char;

namespace repository {
    template<typename C>
    class CoverArtRepository {
    public:
        // TODO: implement this
        std::vector<C> retrieveCoverArtRecords(const model::Token& token, const std::string& uri) {
            std::vector<std::string> vals;

            return vals;
        }

        std::vector<data> retrieveCoverArtData(const model::Token& token, const C& cover,
                const std::string& uri) {
            std::string fullUri(uri);
            if (fullUri.at(fullUri.size() - 1) != '/') {
                fullUri.append("/");
            }
            fullUri.append(downloadEndpoint());
            fullUri.append(std::to_string(cover.id));

            CURL *curl;
            struct curl_slist *chunk = nullptr;
            curl = curl_easy_init();

            if (!curl) {
                return std::vector<data>();
            }

            std::string data;
            std::string authHeader("Authorization: Bearer ");
            authHeader.append(token.accessToken);
            constexpr auto keepAliveHeader = "Content-type: Keep-alive";
            chunk = curl_slist_append(chunk, authHeader.c_str());
            chunk = curl_slist_append(chunk, keepAliveHeader);

            curl_easy_setopt(curl, CURLOPT_URL, fullUri.c_str());
            curl_easy_setopt(curl, CURLOPT_HTTPHEADER, chunk);
            curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, respBodyRetriever);
            curl_easy_setopt(curl, CURLOPT_WRITEDATA, &data);
            curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0L);

            auto res = curl_easy_perform(curl);
            curl_easy_cleanup(curl);

            std::vector<char> vals(data.begin(), data.end());

            return vals;
        }

        // TODO: implement this
        C retrieveCoverArtRecord(const model::Token& token, const C& cover,
                const std::string& uri) {
            C cov;

            return cov;
        }


    private:
        constexpr auto downloadEndpoint() noexcept {
            return "api/v1/coverart/download/";
        }

        constexpr auto recordsEndpoint() noexcept {
            return "api/v1/coverart/";
        }

        static size_t respBodyRetriever(void *ptr, size_t size,
                size_t nmemb, char *e) {
            ((std::string*)e)->append((char*)ptr, size * nmemb);

            return size * nmemb;
        }
    };
}


#endif //MEAR_COVERARTREPOSITORY_H
