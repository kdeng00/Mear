using System;
using System.Collections.Generic;
using System.Text;

using SQLite;

using Mear.Constants.App;
using Mear.Models.Authentication;

namespace Mear.Repositories.Database
{
	public class DBTokenRepository : DBRepository
	{
		#region Fields
		#endregion


		#region Properties
		#endregion


		#region Constructors
		public DBTokenRepository()
		{
			Initialize();
		}
		#endregion


		#region Methods
		public Token RetrieveToken()
		{
			try
			{
				var token = _Db.Table<Token>().FirstOrDefault();

				return token;
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}

			return null;
		}
		public void SaveToken(Token token)
		{
			if (!DoesTableExist("Token"))
			{
				_Db.CreateTable<Token>();
			}

			_Db.DeleteAll<Token>();

			_Db.Insert(token);
		}
		#endregion
	}
}
