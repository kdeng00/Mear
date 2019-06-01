using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

using Mear.Models;

namespace Mear.Utilities
{
	public class DirectoryManager
	{
		#region Fields
		private Song _song;
		private string _path;
		#endregion


		#region Properties
		#endregion


		#region Constructors
		public DirectoryManager(Song song)
		{
			_song = song;
		}
		#endregion


		#region Methods
		public string CreateSongPath(Song song)
		{
			try
			{
				var artist = song.Artist;
				var album = song.Album;
				var title = song.Title;

				if (!ArtistDirectoryExist(song))
				{
					Directory.CreateDirectory(ArtistPath(song));
				}
				if (!AlbumDirectoryExist(song))
				{
					Directory.CreateDirectory(AlbumPath(song));
				}
				if (SongPathExist(song))
				{
					// TODO: Song exists
					return SongPath(song);
				}
				else
				{
					return SongPath(song);
				}
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}

			return string.Empty;
		}

		private string RootPath(Song song)
		{
			var path = $@"{Environment.GetFolderPath(Environment.SpecialFolder.Personal)}/";

			return path;
		}
		private string ArtistPath(Song song)
		{
			var path = RootPath(song);
			path += $@"{song.Artist}/";

			return path;
		}
		private string AlbumPath(Song song)
		{
			var path = ArtistPath(song);
			path += $@"{song.Album}/";

			return path;
		}
		private string SongPath(Song song)
		{
			var path = AlbumPath(song);
			path += $@"{song.Filename}";

			return path;
		}

		private bool ArtistDirectoryExist(Song song)
		{
			var path = RootPath(song);
			path += song.Artist;

			return Directory.Exists(path);
		}
		private bool AlbumDirectoryExist(Song song)
		{
			var path = AlbumPath(song);

			return Directory.Exists(path);
		}
		private bool SongPathExist(Song song)
		{
			var path = SongPath(song);

			return File.Exists(path);
		}

		private void Initialize()
		{
			_path = Environment.GetFolderPath(Environment.SpecialFolder.Personal) + @"/";
		}
		#endregion
	}
}
