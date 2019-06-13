using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

using MediaManager;
using Newtonsoft.Json;
using RestSharp;

using Mear.Constants;
using Mear.Constants.API;
using Mear.Models;
using Mear.Models.Authentication;
using Mear.Models.PlayerControls;
using Mear.Repositories.Database;
using Mear.Repositories.Remote;
using Mear.Utilities;

namespace Mear.Playback
{
	public class MearPlayer
	{
        #region Fields
        private static Song _song;
        private static bool? _initialized = null;
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
        public static async Task<Song> ControlMusic(Song song, PlayControls control)
        {
            _song = song;
            switch (control)
            {
                case PlayControls.PLAYOFFLINE:
                    await PlaySong(song);
                    var plyCountRepo = new DBPlayCountRepository();
                    plyCountRepo.AffectPlayCount(song);
                    InitializeRepeatMode();
                    break;
                case PlayControls.PAUSE:
                    PauseSong();
                    break;
                case PlayControls.RESUME:
                    ResumeSong();
                    break;
                case PlayControls.STREAM:
                    song = StreamSong(song);
                    InitializeRepeatMode();
                    return song;
                case PlayControls.REPEAT:
                    ToggleRepeat();
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

        public static async Task<string> ConvertToTime()
        {
            try
            {
                var ttlSec = (int)CrossMediaManager.Current.Position.TotalSeconds;
                var cnvrt = new TimeFormat();
                var curPos = cnvrt.ConvertToSongTime(ttlSec);

                return curPos;
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }

            return string.Empty;
        }

        public static async Task<double?> ProgressValue()
        {
            try
            {
                var totalSeconds = (int)CrossMediaManager.Current.Position.TotalSeconds;
			    double progVal = ((double)totalSeconds) / ((double)_song.Duration) * 100;

                return progVal;
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }

            return null;
        }

        public static async Task DownloadSongToFS()
        {
			var songRepo = new RemoteSongRepository();
			songRepo.DownloadSong(_song);

            _song.Downloaded = true;
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
        public static async Task RemoveSongFromFS()
        {
            var dbSongRepo = new DBSongRepository();
            var plyCount = new DBPlayCountRepository();
            dbSongRepo.DeleteSong(_song);
            plyCount.DeletePlayCount(_song);

            File.Delete(_song.SongPath);

            _song.Downloaded = false;
        }
        public static async Task SeekTo(double songProress)
        {
            double newPosition = (songProress / 100) * ((double)_song.Duration);
            await CrossMediaManager.Current.SeekTo(TimeSpan.FromSeconds(newPosition));
        }

        public static string RetrieveRepeatString()
        {
            var ctrlRepo = new DBMusicControlsRepository();
            var ctrlRepeatMode = ctrlRepo.IsRepeatOn();

            switch (ctrlRepeatMode)
            {
                case Repeat.OFF:
                    return "RepOff";
                case Repeat.ONE:
                    return "RepOn";
                case Repeat.ALL:
                    return "RepAll";
            }

            return string.Empty;
        }
        public static string RetrieveShuffleString()
        {
            var controlRepo = new DBMusicControlsRepository();
            var shuffleOn = controlRepo.IsShuffleOn();
            try
            {
                return shuffleOn ? "ShfOn" : "ShfOff";
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }

            return string.Empty;
        }

        public static bool IsPlaying()
        {
            return CrossMediaManager.Current.IsPlaying();
        }
        public static bool RepeatMatchedDatabase()
        {
            var playerRepeatMode = CrossMediaManager.Current.RepeatMode;
            var controlRepo = new DBMusicControlsRepository();
            var repeatMode = controlRepo.IsRepeatOn();
            var repeatModeConverted = RepeatUtility.RetrieveRepeatMode(repeatMode);

            return (playerRepeatMode == repeatModeConverted);
        }

        public static void UpdateRepeatControls()
        {
            InitializeRepeatMode();
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

                    PlaySong(tmpFile);

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

        private static void InitializeModes()
        {
            InitializeRepeatMode();
        }
        private static void InitializeRepeatMode()
        {
            var ctrlRepo = new DBMusicControlsRepository();
            var repeatMode = ctrlRepo.IsRepeatOn();

            var mode = RepeatUtility.RetrieveRepeatMode(repeatMode);

            CrossMediaManager.Current.RepeatMode = mode;
        }
        private static async Task PauseSong()
        {
            await CrossMediaManager.Current.Pause();
        }
        private static async Task PlaySong(string songPath)
        {
            await CrossMediaManager.Current.Play(songPath);
            if (_initialized == null)
            {
                CrossMediaManager.Current.MediaItemFinished += Current_MediaItemFinished;
                //BackgroundWork();
                _initialized = true;
            }
        }
        private static async Task ResumeSong()
        {
            await CrossMediaManager.Current.PlayPause();
        }

        private static void ToggleRepeat()
        {
            var musicCtrl = new DBMusicControlsRepository();
            musicCtrl.UpdateRepeat();
            var repeatMode = (Repeat)musicCtrl.IsRepeatOn();

            CrossMediaManager.Current.RepeatMode = RepeatUtility.RetrieveRepeatMode(repeatMode);
        }

        #region Background
        private static async Task BackgroundWork()
        {
            try
            {
                new Thread(async () =>
                {

                });
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }
        }
        #endregion

        #region Events
        private static void Current_MediaItemFinished(object sender, MediaManager.Media.MediaItemEventArgs e)
        {
            var ctrlRepo = new DBMusicControlsRepository();
            var repeatMode = ctrlRepo.IsRepeatOn();
            if (CrossMediaManager.Current.IsPlaying())
            {
                //return;
            }
            switch(repeatMode)
            {
                case Repeat.ONE:
                    CrossMediaManager.Current.SeekToStart();
                    //PlaySong(_song.SongPath);
                    break;
                case Repeat.ALL:
                    // Will implment this fully later once Queues are a feature
                    PlaySong(_song.SongPath);
                    break;
            }
        }
        #endregion
        #endregion
    }
}
