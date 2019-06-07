using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Threading.Tasks;

using MediaManager;
using Newtonsoft.Json;
using RestSharp;

using Mear.Constants;
using Mear.Constants.API;
using Mear.Models;
using Mear.Models.Authentication;
using Mear.Repositories.Database;

namespace Mear.Playback
{
	public class MearPlayer
	{
        #region Fields
        private static Song _song;
		#endregion


		#region Properties
		#endregion


		#region Constructors
		#endregion


		#region Methods
		public static async Task<Song> StreamSongDemoAsync(Song song)
		{
			string tmpFile = Path.GetTempPath() + "track.mp3";

			if (File.Exists(tmpFile))
			{
				File.Delete(tmpFile);
			}

			try
			{
				using (var writer = File.OpenWrite(tmpFile))
				{
					var client = new RestClient(API.ApiUrl);
					var apiEndpoint = $@"api/{API.APIVersion}/song/stream/{song.Id}"; ;
					var request = new RestRequest(apiEndpoint, Method.GET);
					var tokRepo = new DBTokenRepository();
					var token = tokRepo.RetrieveToken();
					request.AddHeader("Authorization", $"Bearer {token.AccessToken}");
					request.ResponseWriter = (responseStream) =>
						responseStream.CopyTo(writer);

					var response = client.DownloadData(request);

					await CrossMediaManager.Current.Play(tmpFile);
					song.SongPath = tmpFile;

				}

				return song;
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}

			return null;
		}
		public static async Task PlaySong(Song song)
		{
			try
			{
				var songPath = song.SongPath;

				await CrossMediaManager.Current.Play(songPath);
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}
		}
        public static async Task<Song> ControlMusic(Song song, PlayControls control)
        {
            switch (control)
            {
                case PlayControls.PLAYOFFLINE:
                    var songPath = song.SongPath;
                    await CrossMediaManager.Current.Play(songPath);
                    break;
                case PlayControls.PAUSE:
                    await CrossMediaManager.Current.Pause();
                    break;
                case PlayControls.RESUME:
                    await CrossMediaManager.Current.Play();
                    break;
                case PlayControls.STREAM:
                    return StreamSong(song);
                    break;
                case PlayControls.REPEAT:
                    // TODO: Not fully implemented
                    CrossMediaManager.Current.ToggleRepeat();
                    var i = CrossMediaManager.Current.RepeatMode;
                    break;
                case PlayControls.SHUFFLE:
                    // TODO: Implement shuffling
                    break;
                case PlayControls.NEXT:
                    // TODO: Implement Next
                    break;
                case PlayControls.PREVIOUS:
                    break;
                    // TODO: Implement Next
            }

            return null;
        }

        private static Song StreamSong(Song song)
        {
			string tmpFile = Path.GetTempPath() + "track.mp3";

			if (File.Exists(tmpFile))
			{
				File.Delete(tmpFile);
			}

			try
			{
				using (var writer = File.OpenWrite(tmpFile))
				{
					var client = new RestClient(API.ApiUrl);
					var apiEndpoint = $@"api/{API.APIVersion}/song/stream/{song.Id}"; ;

					var request = new RestRequest(apiEndpoint, Method.GET);
					var tokRepo = new DBTokenRepository();
					var token = tokRepo.RetrieveToken();

					request.AddHeader("Authorization", $"Bearer {token.AccessToken}");
					request.ResponseWriter = (responseStream) =>
						responseStream.CopyTo(writer);

					var response = client.DownloadData(request);

					CrossMediaManager.Current.Play(tmpFile);
					song.SongPath = tmpFile;
				}

				return song;
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
