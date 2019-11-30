//
// Created by brahmix on 10/6/19.
//

#ifndef MEAR_TOKENREPOSITORY_H
#define MEAR_TOKENREPOSITORY_H

#include <string>

#include "../model/Token.h"
#include "BaseRepository.h"

namespace repository { namespace local {
    template<class Token = model::Token>
    class TokenRepository: public BaseRepository {
    public:
        TokenRepository() {
            m_tableName = tokenTable();
        }


        Token retrieveToken(const std::string& path) {
            Token token;

            try {
                auto db = getDbConn(path, ConnType::ReadOnly);
                std::string queryString("SELECT * FROM ");
                queryString.append(m_tableName);
                queryString.append(" LIMIT 1");

                SQLite::Statement query(db, queryString);

                auto result = query.executeStep();
                token.accessToken = std::move(query.getColumn(1).getString());

                return token;
            } catch (std::exception& ex) {
                auto msg = ex.what();
            }

            return token;
        }


        void createTokenTable(const std::string& path) {
            try {
                auto db = getDbConn(path, ConnType::ReadWrite);

                std::string queryString("CREATE TABLE ");
                queryString.append(m_tableName);
                queryString.append(" (Id INTEGER PRIMARY KEY, AccessToken TEXT)");

                db.exec(queryString);
            } catch (std::exception& ex) {
                auto msg = ex.what();
            }
        }

        void saveToken(const Token& token, const std::string& path) {
            try {
                auto db = getDbConn(path, ConnType::ReadWrite);

                std::string queryString("INSERT INTO ");
                queryString.append(m_tableName);
                queryString.append(" (AccessToken) VALUES (?)");

                SQLite::Statement query(db, queryString);
                query.bind(1, token.accessToken);

                query.exec();
            } catch (std::exception& ex) {
                auto msg = ex.what();
            }
        }
    private:
        std::string tokenTable() noexcept { return "Token"; }
    };
}}


#endif //MEAR_TOKENREPOSITORY_H
