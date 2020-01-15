//
// Created by brahmix on 10/12/19.
//

#ifndef MEAR_APIINFO_H
#define MEAR_APIINFO_H

#include <string>

namespace model {
    class APIInfo
    {
    public:
        APIInfo() = default;
        APIInfo(const std::string& uri) :
            uri(uri) { }
        APIInfo(const std::string& uri, const int version) :
            uri(uri), version(version) { }
        APIInfo(const std::string&& uri, const int version) :
            uri(uri), version(version) { }
        APIInfo(const std::string& uri, const std::string& endpoint, const int version) :
            uri(uri), endpoint(endpoint), version(version) { }

        std::string uri;
        std::string endpoint;
        int version;
    };
}

#endif //MEAR_APIINFO_H
