//
// Created by brahmix on 11/8/19.
//

#include "CoverArtRepository.h"

#include <curl/curl.h>

namespace repository {

    /**
    template<typename C>
    std::vector<C> CoverArtRepository<C>::retrieveCoverArtRecords(const model::Token& token,
            const std::string& uri) {
        std::vector<std::string> vals;

        return vals;
    }

    template<typename C>
    std::vector<data> CoverArtRepository<C>::retrieveCoverArtData(const model::Token& token,
            const C& cover,
            const std::string& uri) {
        std::string fullUri(uri);
        if (fullUri.at(fullUri.size() - 1) != '/') {
            fullUri.append("/");
        }
        fullUri.append(downloadEndpoint());
        fullUri.append(std::to_string(cover.id));

        CURL *curl;
        CURLcode res;
        struct curl_slist *chunk = nullptr;
        curl = curl_easy_init();

        std::vector<data> vals;

        if (!curl) {
            return vals;
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

        std::copy(data.begin(), data.end(), vals);

        return vals;
    }

    template<typename C>
    C CoverArtRepository<C>::retrieveCoverArtRecord(const model::Token& token,
            const C& cover,
            const std::string& uri) {
        model::CoverArt cov;

        return cov;
    }


    template<typename C>
    constexpr auto CoverArtRepository<C>::downloadEndpoint() noexcept {
        return "api/v1/coverart/download";
    }
    template<typename C>
    constexpr auto CoverArtRepository<C>::recordsEndpoint() noexcept {
        return "api/v1/coverart/";
    }

    template<typename C>
    size_t CoverArtRepository<C>::respBodyRetriever(void *ptr, size_t size, size_t nmemb, char *e) {
        ((std::string*)e)->append((char*)ptr, size * nmemb);

        return size * nmemb;
    }
     */
}
