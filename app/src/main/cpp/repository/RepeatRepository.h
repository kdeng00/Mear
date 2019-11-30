//
// Created by brahmix on 10/21/19.
//

#ifndef MEAR_REPEATREPOSITORY_H
#define MEAR_REPEATREPOSITORY_H

#include <string>

#include "BaseRepository.h"
#include "../types/RepeatTypes.h"

namespace repository { namespace local {
    class RepeatRepository : public BaseRepository {
    public:
        RepeatRepository() {
            m_tableName = repeatTable();
        }


        RepeatTypes retrieveRepeatMode(const std::string& path) {
            try {
                auto db = getDbConn(path, ConnType::ReadOnly);
                std::string queryString("SELECT * FROM ");
                queryString.append(m_tableName);
                queryString.append(" LIMIT 1");

                SQLite::Statement query(db, queryString);

                const auto result = query.executeStep();

                auto repeatType = query.getColumn(1).getInt();
                auto val = static_cast<RepeatTypes>(repeatType);
                return val;
            } catch (std::exception& ex) {
                auto msg = ex.what();
            }

            return RepeatTypes::RepeatOff;
        }


        void createRepeatTable(const std::string& path) {
            try {
                auto db = getDbConn(path, ConnType::ReadWrite);
                std::string queryString("CREATE TABLE ");
                queryString.append(m_tableName);
                queryString.append(" (Id INTEGER PRIMARY KEY, RepeatMode INT)");

                db.exec(queryString);

                queryString = "INSERT INTO " + m_tableName + " (RepeatMode) VALUES(?)";
                SQLite::Statement query(db, queryString);
                query.bind(1, static_cast<int>(RepeatTypes::RepeatOff));
                query.exec();

            } catch (std::exception &ex) {
                auto msg = ex.what();
            }
        }

        void updateRepeat(const std::string& path) {
            try {
                auto db = getDbConn(path, ConnType::ReadWrite);

                if (isTableEmpty(path)) {
                    std::string queryString("INSERT INTO ");
                    queryString.append(m_tableName);
                    queryString.append(" (RepeatMode) VALUES (?)");

                    SQLite::Statement query(db, queryString);
                    query.bind(1, static_cast<int>(RepeatTypes::RepeatOff));
                    query.exec();
                } else {
                    std::string queryString("UPDATE ");
                    queryString.append(m_tableName);
                    queryString.append(" SET RepeatMode = ?");

                    auto repeatMode = retrieveRepeatMode(path);
                    SQLite::Statement query(db, queryString);

                    switch (repeatMode) {
                        case RepeatTypes::RepeatOff:
                            query.bind(1, static_cast<int>(RepeatTypes::RepeatSong));
                            break;
                        case RepeatTypes::RepeatSong:
                            query.bind(1, static_cast<int>(RepeatTypes::RepeatOff));
                            break;
                        default:
                            break;
                    }

                    query.exec();
                }
            } catch (std::exception &ex) {
                auto msg = ex.what();
            }
        }
    private:
        std::string repeatTable() noexcept { return "Repeat"; }
    };
}}


#endif //MEAR_REPEATREPOSITORY_H
