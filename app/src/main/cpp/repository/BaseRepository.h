//
// Created by brahmix on 10/6/19.
//

#ifndef MEAR_BASEREPOSITORY_H
#define MEAR_BASEREPOSITORY_H

#include <string>
#include <exception>

#include <SQLiteCpp/Database.h>

#include "../types/ConnType.h"

namespace repository { namespace local {
    using type::ConnType;

    class BaseRepository {
    public:
        bool databaseExist(const std::string& appPath) {
            try {
                const auto dbPath = pathOfDatabase(appPath);

                SQLite::Database db(dbPath, SQLite::OPEN_READONLY);
                return true;
            } catch (std::exception& ex) {
                auto msg = ex.what();
            }

            return false;
        }

        bool doesTableExist(const std::string& appPath) {
            try {
                const auto dbPath = pathOfDatabase(appPath);
                SQLite::Database db(dbPath, SQLite::OPEN_READONLY);

                auto result = db.tableExists(m_tableName);
                return result;
            } catch (std::exception& ex) {
                auto msg = ex.what();
            }

            return false;
        }

        bool isTableEmpty(const std::string& appPath) {
            try {
                const auto dbPath = pathOfDatabase(appPath);
                SQLite::Database db(dbPath, SQLite::OPEN_READONLY);
                std::string queryStr("SELECT * FROM ");
                queryStr.append(m_tableName);

                SQLite::Statement query(db, queryStr);

                const auto result = query.hasRow();

                return result;
            } catch (std::exception& ex) {
                auto msg = ex.what();
            }

            return true;
        }


        void initializedDatabase(const std::string& appPath) {
            try {
                const auto dbPath = pathOfDatabase(appPath);

                SQLite::Database db(dbPath, SQLite::OPEN_CREATE | SQLite::OPEN_READWRITE);
            } catch (std::exception& ex) {
                auto msg = ex.what();
            }
        }

        void deleteRecord(const std::string& appPath) {
            try {
                const auto dbPath = pathOfDatabase(appPath);

                SQLite::Database db(dbPath, SQLite::OPEN_CREATE | SQLite::OPEN_READWRITE);
            } catch (std::exception& ex) {
                auto msg = ex.what();
            }
        }
    protected:
        std::string pathOfDatabase(const std::string& appPath) {
            std::string dbPath(appPath);
            auto lastChar = dbPath.at(dbPath.size()-1);
            if (lastChar != '/') {
                dbPath.append("/");
            }

            dbPath.append(databaseName());

            return dbPath;
        }

        template<typename Str = std::string>
        const Str databaseName() noexcept { return "mear.db3"; }


        SQLite::Database getDbConn(const std::string& path, ConnType dbType) {
            auto dbPath = pathOfDatabase(path);
            switch (dbType) {
                case ConnType::ReadOnly:
                    return SQLite::Database(dbPath, SQLite::OPEN_READONLY);
                case ConnType::ReadWrite:
                    return SQLite::Database(dbPath, SQLite::OPEN_READWRITE);
                case ConnType::Create:
                    return SQLite::Database(dbPath, SQLite::OPEN_CREATE);
                default:
                    break;
            }

            return SQLite::Database(dbPath);
        }

        std::string m_tableName;
    private:
    };
} }


#endif //MEAR_BASEREPOSITORY_H
