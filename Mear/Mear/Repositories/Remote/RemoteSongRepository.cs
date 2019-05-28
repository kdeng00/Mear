using System;
using System.Collections.Generic;
using System.Text;

using Newtonsoft.Json;
using RestSharp;

using Mear.Constants.API;
using Mear.Models;
using Mear.Repositories.Database;

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
				var request = new RestRequest(@"api/song", Method.GET);

				DBTokenRepository tkRepo = new DBTokenRepository();
				var token = tkRepo.RetrieveToken();
				request.AddHeader("Authorization", $"Bearer {token.AccessToken}");

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
		#endregion
	}
}
