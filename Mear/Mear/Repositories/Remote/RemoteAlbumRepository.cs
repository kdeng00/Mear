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
	public class RemoteAlbumRepository
	{
		#region Fields
		#endregion


		#region Properties
		#endregion


		#region Constructors
		#endregion


		#region Methods
		public List<Album> RetrieveAlbums()
		{
			try
			{
				var client = new RestClient(API.ApiUrl);
				var apiEndpoint = $@"api/{API.APIVersion}/album";
				var request = new RestRequest(apiEndpoint, Method.GET);

				var tkRepo = new DBTokenRepository();
				var token = tkRepo.RetrieveToken();
				request.AddHeader("Authorization", $"Bearer {token.AccessToken}");
                request.AddHeader("Connection", "Keep-Alive");

				var reponse = client.Execute(request);

				var albums = JsonConvert.DeserializeObject<List<Album>>(reponse.Content);

				return albums;
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
