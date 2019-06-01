using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

using TagLib;

using Mear.Models;

using IOFile = System.IO.File;

namespace Mear.Utilities
{
	public class SongMetadataRetriever
	{
		#region Fields
		#endregion


		#region Properties
		#endregion


		#region Constructors
		#endregion


		#region Methods
		public Song ExtractData(string songPath)
		{
			try
			{
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}

			return null;
		}
		public Stream ExtractCovertArtStream(Song song)
		{
			var strm = new FileStream(song.SongPath, FileMode.Open);

			try
			{
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}

			return strm;
		}
		public string ExtractCoverArtData(Song song)
		{
			var imgData = string.Empty;
			try
			{
				var d = IOFile.ReadAllBytes(song.SongPath);
				/**
				*/

			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}

			return imgData;
		}
		#endregion
	}
}
