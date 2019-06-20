using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Mear.Models;
using Mear.Models.Authentication;

namespace Mear.Repositories.Database
{
    public class DBUserRepository : DBRepository
    {
        #region Fields
        #endregion


        #region Properties
        #endregion


        #region Constructors
        #endregion


        #region Methods
        public static User RetrieveUser()
        {
            InitializeDatabase("User");
            try
            {
                if (TableExists())
                {
                    var user = _DbConn.Table<User>().First();

                    if (user != null)
                    {
                        return user;
                    }
                }
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }

            return null;
        }

        public static void DeleteUser()
        {
            InitializeDatabase("User");
            try
            {
                if (TableExists())
                {
                    _DbConn.Table<User>().Delete(u => !string.IsNullOrEmpty(u.Username));
                }
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }
        }
        public static void SaveUser(User user)
        {
            InitializeDatabase("User");
            try
            {
                if (!TableExists())
                {
                    _DbConn.CreateTable<User>();
                }

                _DbConn.Insert(user);
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }
        }
        #endregion
    }
}
