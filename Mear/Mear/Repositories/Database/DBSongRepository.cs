using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using SQLite;

using Mear.Constants.App;
using Mear.Models;

namespace Mear.Repositories.Database
{
	public class DBSongRepository : DBRepository
	{
		#region Fields
		#endregion


		#region Properties
		#endregion


		#region Constructors
		public DBSongRepository()
		{
			Initialize();
		}
		#endregion


		#region Methods
		public List<Song> RetrieveSongs()
		{
			try
			{
				var songs = _Db.Table<Song>().ToList();

				return songs;
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}

			return null;
		}
		public Song RetrieveSong(int id)
		{
			try
			{
				var song = _Db.Table<Song>().Where(s => s.Id == id).First();

				return song;
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}

			return null;
		}
		public Song RetrieveSong(string artist, string title)
		{
			try
			{
				var song = _Db.Table<Song>().Where(s => 
					(s.Artist.Equals(artist) && s.Title.Equals(title))).First();

				return song;
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}

			return null;
		}

		public void SaveSong(Song song)
		{
			if (!DoesTableExist("Song"))
			{
				_Db.CreateTable<Song>();
			}

			try
			{
				_Db.Insert(song);
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}
		}
		public void UpdateSong(Song song)
		{
			if (!DoesTableExist("Song"))
			{
				return;
			}

			// TODO: Implement functionality for udpating song
		}
		#endregion
	}
}
