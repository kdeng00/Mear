using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

using Mear.Constants.App;

using SQLite;

namespace Mear.Repositories.Database
{
	public class DBRepository
	{
        #region Fields
        protected static SQLiteConnection _DbConn = null;
		protected SQLiteConnection _Db;
        protected static string _table;
		protected string _dbPath;
        protected string _tableName;
		#endregion


		#region Properties
		public string DBPath
		{
			get => _dbPath;
		}
		#endregion


		#region Constructors
		#endregion


		#region Methods
        protected static bool TableExists()
        {
            try
            {
                var result = _DbConn.GetTableInfo(_table).Count;

                if (result > 0)
                {
                    return true;
                }
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }

            return false;
        }
		protected bool DoesTableExist(string tablename)
		{
			var result = 0;

			try
			{
				result = _Db.GetTableInfo(tablename).Count;

				if (result > 0)
				{
					return true;
				}
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}
			return false;
		}

        protected static void CloseDbConnection()
        {
            _DbConn.Close();
        }
		protected void CloseDb()
		{
			_Db.Close();
		}
		protected void Initialize()
		{
			var appName = Info.AppName;
			_dbPath = Path.Combine(Environment.GetFolderPath(
				Environment.SpecialFolder.Personal), appName);

			_Db = new SQLiteConnection(_dbPath);
		}
        protected static void InitializeDatabase(string tablename)
        {
            if (_DbConn != null)
            {
                return;
            }

            _table = tablename;
            var appName = Info.AppName;
            _DbConn = new SQLiteConnection(Path.Combine(Environment.GetFolderPath(
                Environment.SpecialFolder.Personal), appName));
        }
		#endregion
	}
}
