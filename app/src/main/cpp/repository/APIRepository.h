//
// Created by brahmix on 10/12/19.
//

#ifndef MEAR_APIREPOSITORY_H
#define MEAR_APIREPOSITORY_H

#include <string>

#include "BaseRepository.h"
#include "../model/APIInfo.h"

namespace repository { namespace local {
    class APIRepository: public BaseRepository {
    public:
        APIRepository() {
            m_tableName = apiInfoTable();
        }


        model::APIInfo retrieveAPIInfo(const std::string& path) {
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
                apiInfo.version = query.getColumn(2).getInt();

                return apiInfo;
            } catch (std::exception& ex) {
                auto msg = ex.what();
            }

            return apiInfo;
        }


        [[deprecated("use the base class function")]]
        bool doesAPIInfoTableExist(const std::string& path) {
            try {
                const auto dbPath = pathOfDatabase(path);
                SQLite::Database db(dbPath, SQLite::OPEN_READONLY);

                return db.tableExists(m_tableName);
            } catch (std::exception& ex) {
                auto msg = ex.what();
            }

            return false;
        }


        void createAPiInfoTable(const std::string& path) {
            try {
                const auto dbPath = pathOfDatabase(path);
                SQLite::Database db(dbPath, SQLite::OPEN_READWRITE);

                std::string queryString("CREATE TABLE ");
                queryString.append(m_tableName);
                queryString.append(" (Id INTEGER PRIMARY KEY, Uri TEXT, Version INT)");
                db.exec(queryString);
            } catch (std::exception& ex) {
                auto msg = ex.what();
            }
        }

        void deleteAPIInfo(const model::APIInfo& apiInfo, const std::string& path) {
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

        void saveAPIInfo(const model::APIInfo& apiInfo, const std::string& path) {
            try {
                const auto dbPath = pathOfDatabase(path);
                SQLite::Database db(dbPath, SQLite::OPEN_READWRITE);
                std::string queryString("INSERT INTO ");
                queryString.append(m_tableName);
                queryString.append(" (Uri, Version) VALUES (?, ?)");

                SQLite::Statement query(db, queryString);
                query.bind(1, apiInfo.uri);
                query.bind(2, apiInfo.version);

                query.exec();
            } catch (std::exception& ex) {
                auto msg = ex.what();
            }
        }
    private:
        std::string apiInfoTable() noexcept { return "APIInfo"; }
    };
}}


#endif //MEAR_APIREPOSITORY_H
