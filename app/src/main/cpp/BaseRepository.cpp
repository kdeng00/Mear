//
// Created by brahmix on 10/6/19.
//

#include "BaseRepository.h"

#include <fstream>

#include <SQLiteCpp/SQLiteCpp.h>

namespace repository { namespace local {
    bool BaseRepository::databaseExist(const std::string& appPath)
    {
        try {
            const auto dbPath = pathOfDatabase(appPath);

            SQLite::Database db(dbPath, SQLite::OPEN_READONLY);
            return true;
        } catch (std::exception& ex) {
            auto msg = ex.what();
        }

        return false;
    }

    bool BaseRepository::isTableEmpty(const std::string& appPath)
    {
        try {
            const auto dbPath = pathOfDatabase(appPath);
            SQLite::Database db(dbPath, SQLite::OPEN_READONLY);
            std::string queryStr("SELECT * FROM ");
            queryStr.append(m_tableName);

            SQLite::Statement query(db, queryStr);

            return query.hasRow();
        } catch (std::exception& ex) {
            auto msg = ex.what();
        }

        return true;
    }


    void BaseRepository::initializedDatabase(const std::string& appPath)
    {
        try {
            const auto dbPath = pathOfDatabase(appPath);

            SQLite::Database db(dbPath, SQLite::OPEN_CREATE | SQLite::OPEN_READWRITE);
        } catch (std::exception& ex) {
            auto msg = ex.what();
        }
    }


    std::string BaseRepository::pathOfDatabase(const std::string& appPath)
    {
        std::string dbPath(appPath);
        auto lastChar = dbPath.at(dbPath.size()-1);
        if (lastChar != '/') {
            dbPath.append("/");
        }

        dbPath.append(m_databaseName);

        return dbPath;
    }


}}