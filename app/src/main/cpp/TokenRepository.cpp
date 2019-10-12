//
// Created by brahmix on 10/6/19.
//

#include "TokenRepository.h"

#include <SQLiteCpp/Database.h>

namespace repository { namespace local {
    TokenRepository::TokenRepository()
    {
        m_tableName = tokenTable();
    }


    model::Token TokenRepository::retrieveToken(const std::string& path)
    {
        model::Token token;

        try {
            auto db = getDbConn(path, ConnType::ReadOnly);
            std::string queryString("SELECT * FROM ");
            queryString.append(m_tableName);
            queryString.append(" LIMIT 1");

            SQLite::Statement query(db, queryString);

            auto result = query.executeStep();
            model::Token token(query.getColumn(1).getString());

            return token;
        } catch (std::exception& ex) {
            auto msg = ex.what();
        }

        return token;
    }


    void TokenRepository::createTokenTable(const std::string& path)
    {
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

    void TokenRepository::saveToken(const model::Token& token, const std::string& path)
    {
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


    std::string TokenRepository::tokenTable() noexcept { return "Token"; }
}}
