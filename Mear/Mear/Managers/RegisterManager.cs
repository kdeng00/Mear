using System;
using System.Collections.Generic;
using System.Text;

using Newtonsoft.Json;
using RestSharp;

using Mear.Constants.API;
using Mear.Models;
using Mear.Models.Authentication;

namespace Mear.Managers
{
	public class RegisterManager
	{
		#region Fields
		private User _user;
		#endregion


		#region Properties
		#endregion


		#region Constructors
		public RegisterManager(User user)
		{
			_user = user;
		}
		#endregion


		#region Methods
		public RegisterResult RegisterUser()
		{
			try
			{
				var client = new RestClient(API.ApiUrl);
				var apiEndpoint = $@"api/{API.APIVersion}/register";
				var request = new RestRequest(apiEndpoint, Method.POST);
				var userJson = JsonConvert.SerializeObject(_user);
				request.AddParameter("application/json; charset=utf-8", userJson, ParameterType.RequestBody);
				request.RequestFormat = DataFormat.Json;

				var response = client.Execute(request);

				var registerResult = JsonConvert.DeserializeObject<RegisterResult>(response.Content);

				return registerResult;
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
