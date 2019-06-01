using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Threading.Tasks;

using MediaManager;
using Newtonsoft.Json;
using RestSharp;

using Mear.Constants.API;
using Mear.Models;
using Mear.Models.Authentication;
using Mear.Repositories.Database;

namespace Mear.Playback
{
	public class MearPlayer
	{
		#region Fields
		#endregion


		#region Properties
		#endregion


		#region Constructors
		#endregion


		#region Methods
		public static async Task<Song> StreamSongDemoAsync(Song song)
		{
			string tmpFile = Path.GetTempPath() + "track.mp3";

			if (File.Exists(tmpFile))
			{
				File.Delete(tmpFile);
			}

			try
			{
				using (var writer = File.OpenWrite(tmpFile))
				{
					var client = new RestClient(API.ApiUrl);
					var apiEndpoint = $@"api/{API.APIVersion}/song/stream/{song.Id}"; ;
					var request = new RestRequest(apiEndpoint, Method.GET);
					var tokRepo = new DBTokenRepository();
					var token = tokRepo.RetrieveToken();
					request.AddHeader("Authorization", $"Bearer {token.AccessToken}");
					request.ResponseWriter = (responseStream) =>
						responseStream.CopyTo(writer);

					var response = client.DownloadData(request);

					await CrossMediaManager.Current.Play(tmpFile);
					song.SongPath = tmpFile;
				}

				return song;
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}

			return null;
		}
		public static async Task PlaySong(Song song)
		{
			try
			{
				var songPath = song.SongPath;

				await CrossMediaManager.Current.Play(songPath);
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}
		}
		#endregion
	}
}
