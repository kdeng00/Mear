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
        std::string uri;
        std::string endpoint;
        int version;
    };
}

#endif //MEAR_APIINFO_H
