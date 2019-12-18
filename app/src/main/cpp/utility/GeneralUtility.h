//
// Created by brahmix on 11/25/19.
//

#ifndef MEAR_GENERALUTILITY_H
#define MEAR_GENERALUTILITY_H

#include <string>

#include "../model/Token.h"

namespace utility {
    class GeneralUtility {
    public:
        template<typename S = std::string>
        static S appendForwardSlashToUri(const S& uri) {
            std::string fullUri(uri);
            if (fullUri.at(fullUri.size() - 1) != '/') {
                fullUri.append("/");
            }

            return fullUri;
        }
        template<typename S = std::string, class Token = model::Token>
        static S authHeader(const Token& token) {
            std::string header("Authorization: Bearer ");
            header.append(token.accessToken);

            return header;
        }
    private:
    };
}

#endif //MEAR_GENERALUTILITY_H
