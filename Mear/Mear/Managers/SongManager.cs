using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
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
        public async Task StreamSong()
        {
            var url = $"https://www.soaricarus.com/api/v1/song/stream/{Playback.MearPlayer.OnSong.Id}";
            var streamReq = (HttpWebRequest)WebRequest.Create(url);
            var tokRepo = new DBTokenRepository();
            var token = tokRepo.RetrieveToken();
            streamReq.Headers.Add(HttpRequestHeader.Authorization, $"Bearer {token.AccessToken}");
            streamReq.KeepAlive = true;
            streamReq.ContentType = "application/octet-stream";
            streamReq.AddRange(0, long.MaxValue);
            streamReq.Method = WebRequestMethods.Http.Get;

            using (var songStream = ((HttpWebResponse)streamReq.GetResponse()).GetResponseStream())
            {
                using (var songTemp = new FileStream(Playback.MearPlayer.OnSong.SongPath, FileMode.CreateNew, FileAccess.ReadWrite,
                    FileShare.Read, bufferSize: 4096, useAsync: true))
                {
                    await songStream.CopyToAsync(songTemp);
                    await Task.Delay(500);
                }
            }
        }

        // Do not use
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
