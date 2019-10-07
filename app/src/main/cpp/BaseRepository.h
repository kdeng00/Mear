//
// Created by brahmix on 10/6/19.
//

#ifndef MEAR_BASEREPOSITORY_H
#define MEAR_BASEREPOSITORY_H

#include <string>
#include <exception>

namespace repository { namespace local {
    class BaseRepository {
    public:
        bool databaseExist(const std::string&);
        bool isTableEmpty(const std::string&);

        void initializedDatabase(const std::string&);
    protected:
        std::string pathOfDatabase(const std::string&);

        const std::string m_databaseName = "mear.db3";
        std::string m_tableName;
    private:
    };
} }


#endif //MEAR_BASEREPOSITORY_H
