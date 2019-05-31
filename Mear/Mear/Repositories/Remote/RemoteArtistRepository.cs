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
	public class RemoteArtistRepository
	{
		#region Fields
		#endregion


		#region Properties
		#endregion


		#region Constructors
		#endregion


		#region Methods
		public List<Artist> RetrieveArtists()
		{
			try
			{
				var client = new RestClient(API.ApiUrl);
				var apiEndpoint = $@"api/{API.APIVersion}/artist";
				var request = new RestRequest(apiEndpoint, Method.GET);

				var tkRepo = new DBTokenRepository();
				var token = tkRepo.RetrieveToken();
				request.AddHeader("Authorization", $"Bearer {token.AccessToken}");

				var response = client.Execute(request);

				var artists = JsonConvert.DeserializeObject<List<Artist>>(response.Content);

				return artists;
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
