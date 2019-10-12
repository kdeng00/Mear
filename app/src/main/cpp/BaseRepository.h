//
// Created by brahmix on 10/6/19.
//

#ifndef MEAR_BASEREPOSITORY_H
#define MEAR_BASEREPOSITORY_H

#include <string>
#include <exception>

#include <SQLiteCpp/Database.h>

namespace repository { namespace local {
    class BaseRepository {
    public:
        bool databaseExist(const std::string&);
        bool doesTableExist(const std::string&);
        bool isTableEmpty(const std::string&);

        void initializedDatabase(const std::string&);
        void deleteRecord(const std::string&);
    protected:
        enum class ConnType;
        std::string pathOfDatabase(const std::string&);

        SQLite::Database getDbConn(const std::string&, ConnType);

        const std::string m_databaseName = "mear.db3";
        std::string m_tableName;

        enum class ConnType {
            ReadOnly = 0,
            ReadWrite,
            Create
        };
    private:
    };
} }


#endif //MEAR_BASEREPOSITORY_H
