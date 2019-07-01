using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

using Newtonsoft.Json;
using RestSharp;

using Mear.Constants.API;
using Mear.Models;
using Mear.Repositories.Database;
using Mear.Utilities;

namespace Mear.Repositories.Remote
{
	public class RemoteSongRepository
	{
		#region Fields
		#endregion


		#region Properties
		#endregion


		#region Constructors
		#endregion


		#region Methods
		public List<Song> RetrieveSongs()
		{
			try
			{
				var client = new RestClient(API.ApiUrl);
				var apiEndpoint = $@"api/{API.APIVersion}/song";
				var request = new RestRequest(apiEndpoint, Method.GET);

				DBTokenRepository tkRepo = new DBTokenRepository();
				var token = tkRepo.RetrieveToken();
				request.AddHeader("Authorization", $"Bearer {token.AccessToken}");
                request.AddHeader("Connection", "Keep-Alive");

				var response = client.Execute(request);

				var songs = JsonConvert.DeserializeObject<List<Song>>(response.Content);

				return songs;
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}

			return null;
		}
		public void DownloadSong(Song song)
		{
			try
			{
				var dirMgr = new DirectoryManager(song);
				var path = dirMgr.CreateSongPath(song);
				using (var writer = File.OpenWrite(path))
				{
					var client = new RestClient(API.ApiUrl);
					var apiEndpoint = $@"api/{API.APIVersion}/song/data/{song.Id}"; ;
					var request = new RestRequest(apiEndpoint, Method.GET);

					DBTokenRepository tkRepo = new DBTokenRepository();
					var token = tkRepo.RetrieveToken();
					request.AddHeader("Authorization", $"Bearer {token.AccessToken}");

					request.ResponseWriter = (responseStream) =>
						responseStream.CopyTo(writer);

					var response = client.DownloadData(request);
				}
				song.Downloaded = true;
				song.SongPath = path;

				var dbSongRepo = new DBSongRepository();
				dbSongRepo.SaveSong(song);
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}
		}
		#endregion
	}
}
