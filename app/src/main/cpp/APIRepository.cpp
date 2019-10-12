//
// Created by brahmix on 10/12/19.
//

#include "APIRepository.h"

#include <SQLiteCpp/Database.h>

namespace repository { namespace local {
    APIRepository::APIRepository()
    {
        m_tableName = apiInfoTable();
    }


    model::APIInfo APIRepository::retrieveAPIInfo(const std::string& path)
    {
        model::APIInfo apiInfo;
        try {
            const auto dbPath = pathOfDatabase(path);
            SQLite::Database db(dbPath, SQLite::OPEN_READONLY);

            std::string queryString("SELECT * FROM ");
            queryString.append(m_tableName);
            queryString.append(" LIMIT 1");

            SQLite::Statement query(db, queryString);

            auto result = query.executeStep();
            apiInfo.uri = query.getColumn(1).getString();
            apiInfo.endpoint = query.getColumn(2).getString();
            apiInfo.version = query.getColumn(3).getInt();

            return apiInfo;
        } catch (std::exception ex) {
            auto msg = ex.what();
        }

        return apiInfo;
    }


    void APIRepository::createAPiInfoTable(const std::string& path)
    {
        try {
            const auto dbPath = pathOfDatabase(path);
            SQLite::Database db(dbPath, SQLite::OPEN_READWRITE);

            std::string queryString("CREATE TABLE ");
            queryString.append(m_tableName);
            queryString.append(" (Id INTEGER PRIMARY KEY, Uri TEXT, Endpoint TEXT, Version INT");
            db.exec(queryString);
        } catch (std::exception& ex) {
            auto msg = ex.what();
        }
    }

    void APIRepository::deleteAPIInfo(const model::APIInfo& apiInfo, const std::string& path)
    {
        try {
            const auto dbPath = pathOfDatabase(path);
            SQLite::Database db(dbPath, SQLite::OPEN_READWRITE);

            std::string queryString("DELETE FROM ");
            queryString.append(m_tableName);

            db.exec(queryString);
        } catch (std::exception& ex) {
            auto msg = ex.what();
        }
    }

    void APIRepository::saveAPIInfo(const model::APIInfo& apiInfo, const std::string& path)
    {
        try {
            const auto dbPath = pathOfDatabase(path);
            SQLite::Database db(dbPath, SQLite::OPEN_READWRITE);
            std::string queryString("INSERT INTO ");
            queryString.append(m_tableName);
            queryString.append(" (Uri, Endpoint, Version) VALUES (?, ?, ?)");

            SQLite::Statement query(db, queryString);
            query.bind(1, apiInfo.uri);
            query.bind(2, apiInfo.endpoint);
            query.bind(3, apiInfo.version);

            query.exec();
        } catch (std::exception& ex) {
            auto msg = ex.what();
        }
    }


    std::string APIRepository::apiInfoTable() noexcept { return "APIInfo"; }
}}
