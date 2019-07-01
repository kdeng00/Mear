using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Threading.Tasks;

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
                //using (var writer = File.OpenWrite(song.SongPath))
                using (var writer = new FileStream(song.SongPath, FileMode.Create, FileAccess.Write,
                    FileShare.ReadWrite, bufferSize: 4096, useAsync: true))
                {
                    var client = new RestClient(API.ApiUrl);
                    var apiEndpoint = $@"api/{API.APIVersion}/song/stream/{song.Id}";

                    var request = new RestRequest(apiEndpoint, Method.GET);
                    var tokRepo = new DBTokenRepository();
                    var token = tokRepo.RetrieveToken();

                    request.AddHeader("Authorization", $"Bearer {token.AccessToken}");
                    request.AddHeader("Content-type", "application/octet-stream");
                    request.AddHeader("Connection", "Keep-Alive");
                    request.ResponseWriter = (responseStream) =>
                        responseStream.CopyTo(writer);
                    /**
                    */
                    //responseStream.CopyToAsync(writer);

                    client.DownloadData(request);
                    //var response = client.Execute(request);

                }

                return true;
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
