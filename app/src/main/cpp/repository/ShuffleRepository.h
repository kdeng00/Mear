//
// Created by brahmix on 10/21/19.
//

#ifndef MEAR_SHUFFLEREPOSITORY_H
#define MEAR_SHUFFLEREPOSITORY_H

#include <string>

#include "BaseRepository.h"
#include "../types/ShuffleTypes.h"

namespace repository { namespace local {
    class ShuffleRepository : public BaseRepository {
    public:
        ShuffleRepository() {
            m_tableName = shuffleTable();
        }


        ShuffleTypes retrieveShuffleMode(const std::string& path) {
            try {
                auto db = getDbConn(path, ConnType::ReadOnly);
                std::string queryString("SELECT * FROM ");
                queryString.append(m_tableName);
                queryString.append(" LIMIT 1");

                SQLite::Statement query(db, queryString);

                const auto result = query.executeStep();
                auto shuffleType = query.getColumn(1).getInt();
                auto val = static_cast<ShuffleTypes>(shuffleType);

                return val;

            } catch (std::exception &ex) {
                auto msg = ex.what();
            }

            return ShuffleTypes::ShuffleOff;
        }


        void createShuffleTable(const std::string& path) {
            try {
                auto db = getDbConn(path, ConnType::ReadWrite);
                std::string queryString("CREATE TABLE ");
                queryString.append(m_tableName);
                queryString.append(" (Id INTEGER PRIMARY KEY, ShuffleMode INT)");

                db.exec(queryString);

                queryString = "INSERT INTO ";
                queryString.append(m_tableName);
                queryString.append(" (ShuffleMode) VALUES(?)");
                SQLite::Statement query(db, queryString);
                query.bind(1, static_cast<int>(ShuffleTypes::ShuffleOff));
                query.exec();
            } catch (std::exception &ex) {
                auto msg = ex.what();
            }
        }

        void updateShuffle(const std::string& path) {
            try {
                auto db = getDbConn(path, ConnType::ReadWrite);

                if (!doesTableExist(path)) {
                    createShuffleTable(path);
                }

                if (isTableEmpty(path)) {
                    constexpr auto queryString = "INSERT INTO ? (ShuffleMode) VALUES(?)";
                    SQLite::Statement query(db, queryString);
                    query.bind(1, m_tableName);
                    query.bind(2, static_cast<int>(ShuffleTypes::ShuffleOff));
                    query.exec();
                } else {
                    std::string queryString("UPDATE ");
                    queryString.append(m_tableName);
                    queryString.append(" SET ShuffleMode = ?");

                    auto shuffleMode = retrieveShuffleMode(path);
                    SQLite::Statement query(db, queryString);

                    switch (shuffleMode) {
                        case ShuffleTypes::ShuffleOff:
                            query.bind(1, static_cast<int>(ShuffleTypes::ShuffleOn));
                            break;
                        case ShuffleTypes::ShuffleOn:
                            query.bind(1, static_cast<int>(ShuffleTypes::ShuffleOff));
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
        std::string shuffleTable() noexcept { return "Shuffle"; }
    };
}}


#endif //MEAR_SHUFFLEREPOSITORY_H
