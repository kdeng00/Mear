using System;
using System.Collections.Generic;
using System.Text;

using Newtonsoft.Json;
using RestSharp;

using Mear.Constants.API;
using Mear.Models.Authentication;

namespace Mear.Managers
{
	public class LoginManager
	{
		#region Fields
		private User _user;
		#endregion


		#region Properties
		#endregion


		#region Constructors
		public LoginManager(User user)
		{
			_user = user;
		}
		#endregion


		#region Methods
		public LoginResult Login()
		{
			try
			{
				var client = new RestClient(API.ApiUrl);
				var request = new RestRequest(@"api/login", Method.POST);
				var userJson = JsonConvert.SerializeObject(_user);

				request.AddParameter("application/json; charset=utf-8", userJson, ParameterType.RequestBody);
				request.RequestFormat = DataFormat.Json;

				var response = client.Execute(request);

				var loginResult = JsonConvert.DeserializeObject<LoginResult>(response.Content);

				return loginResult;
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
