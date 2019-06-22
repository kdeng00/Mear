using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

using Newtonsoft.Json;
using RestSharp;

using Mear.Constants.API;
using Mear.Models;
using Mear.Repositories.Database;

namespace Mear.Managers
{
    public class SongManager
    {
        #region Methods
        public bool DownloadStream(ref Song song)
        {
            try
            {
                using (var writer = File.OpenWrite(song.SongPath))
                {
                    var client = new RestClient(API.ApiUrl);
                    var apiEndpoint = $@"api/{API.APIVersion}/song/stream/{song.Id}";

                    var request = new RestRequest(apiEndpoint, Method.GET);
                    var tokRepo = new DBTokenRepository();
                    var token = tokRepo.RetrieveToken();

                    request.AddHeader("Authorization", $"Bearer {token.AccessToken}");
                    request.ResponseWriter = (responseStream) =>
                        responseStream.CopyTo(writer);

                    var response = client.DownloadData(request);

                    return true;
                }
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }

            return false;
        }
        #endregion
    }
}
