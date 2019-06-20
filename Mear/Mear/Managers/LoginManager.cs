using System;
using System.Collections.Generic;
using System.Text;

using Newtonsoft.Json;
using RestSharp;

using Mear.Constants.API;
using Mear.Models.Authentication;
using Mear.Repositories.Database;

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
        public bool Authenticate()
        {
            var loginRes = Login();

            if (loginRes.Expiration > 0 && loginRes != null)
            {
                DBUserRepository.DeleteUser();

                SaveToDatabase(loginRes);

                return true;
            }

            return false;
        }

		private LoginResult Login()
		{
			try
			{
				var client = new RestClient(API.ApiUrl);
				var apiEndpoint = $@"api/{API.APIVersion}/login";
				var request = new RestRequest(apiEndpoint, Method.POST);
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

        private async void SaveToDatabase(LoginResult loginRes)
        {
            DBTokenRepository tokRepo = new DBTokenRepository();
            tokRepo.SaveToken(new Token
            {
                AccessToken = loginRes.Token,
                UserId = loginRes.UserId
            });

            DBUserRepository.SaveUser(_user);
            var usr = DBUserRepository.RetrieveUser();
        }
		#endregion
	}
}
