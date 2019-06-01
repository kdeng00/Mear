using System;
using System.Collections.Generic;
using System.Text;

using Mear.Models;
using Mear.Repositories.Database;

namespace Mear.Repositories.Local
{
	public class LocalSongRepository
	{
		#region Fields
		#endregion


		#region Properties
		#endregion


		#region Contructors
		#endregion


		#region Methods
		public List<Song> RetrieveSongs()
		{
			try
			{
				var dbSongRepo = new DBSongRepository();

				var songs = dbSongRepo.RetrieveSongs();

				if (songs == null)
				{
					throw new Exception("Unable to retrieve song metadata from the local database");
				}

				return songs;
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}
			return null;
		}
		#endregion
	}
}
