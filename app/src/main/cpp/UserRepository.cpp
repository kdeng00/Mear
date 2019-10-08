//
// Created by brahmix on 10/6/19.
//

#include "UserRepository.h"

#include <SQLiteCpp/SQLiteCpp.h>

namespace repository { namespace local {
   UserRepository::UserRepository()
   {
       m_tableName = userTable();
   }


   model::User UserRepository::retrieveUserCredentials(const std::string &appPath)
   {
       model::User user;
       try {
           const auto dbPath = pathOfDatabase(appPath);
           SQLite::Database db(dbPath, SQLite::OPEN_READONLY);

           std::string queryString("SELECT * FROM ");
           queryString.append(m_tableName);
           queryString.append(" LIMIT 1");

           SQLite::Statement query(db, queryString);

           auto result = query.executeStep();

           auto valZero = query.getColumn(1);
           auto valOne = query.getColumn(2);

           user.username = valZero.getString();
           user.password = valOne.getString();

           return user;

       } catch (std::exception& ex) {
           auto msg = ex.what();
       }

       return user;
   }


   bool UserRepository::doesUserTableExist(const std::string &appPath)
   {
       try {
           const auto dbPath = pathOfDatabase(appPath);
           SQLite::Database db(dbPath, SQLite::OPEN_READONLY);

           return db.tableExists(m_tableName);
       } catch (std::exception& ex) {
           auto msg = ex.what();
       }

       return false;
   }


   void UserRepository::createUserTable(const std::string &appPath)
   {
       try {
           const auto dbPath = pathOfDatabase(appPath);
           SQLite::Database db(dbPath, SQLite::OPEN_READWRITE);

           std::string queryString("CREATE TABLE ");
           queryString.append(m_tableName);
           queryString.append(" (Id INTEGER PRIMARY KEY, Username TEXT, Password TEXT)");
           db.exec(queryString);
       } catch (std::exception& ex) {
           auto msg = ex.what();
       }
   }

   void UserRepository::deleteUserTable(const std::string& appPath)
   {
       try {
           const auto dbPath = pathOfDatabase(appPath);
           SQLite::Database db(dbPath, SQLite::OPEN_READWRITE);

           std::string queryStr("DELETE FROM ");
           queryStr.append(m_tableName);

           db.exec(queryStr);
       } catch (std::exception& ex) {
           auto msg = ex.what();
       }
   }

   void UserRepository::saveUserCred(const model::User & user, const std::string& appPath)
   {
       try {
           const auto dbPath = pathOfDatabase(appPath);
           SQLite::Database db(dbPath, SQLite::OPEN_READWRITE);
           std::string queryString("INSERT INTO ");
           queryString.append(m_tableName);
           queryString.append(" (Username, Password) VALUES (?, ?)");

           SQLite::Statement query(db, queryString);
           query.bind(1, user.username);
           query.bind(2, user.password);

           query.exec();
       } catch (std::exception& ex) {
           auto msg = ex.what();
       }
   }


   std::string UserRepository::userTable()
   {
       constexpr auto table = "User";

       return table;
   }
}}
