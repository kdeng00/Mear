﻿using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

using MediaManager;
using Newtonsoft.Json;
using RestSharp;

using Mear.Constants;
using Mear.Constants.API;
using Mear.Managers;
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
        private static Queue<Song> _mearQueue = null;
        private static Song _song;
        private static bool? _initialized = null;
        private static SortedDictionary<MusicViews, bool?> _songChanged =
            new SortedDictionary<MusicViews, bool?>{
                { MusicViews.Song, false },
                { MusicViews.Album, false },
                { MusicViews.Artist, false },
                { MusicViews.Player, false }
            };
        private static int _songIndex;
		#endregion


		#region Properties
        public static Song OnSong
        {
            get => _song;
            set => _song = value;
        }

        private static int QueueIndex
        {
            get => _songIndex;
            set
            {
                if (value >= QueueCount)
                {
                    _songIndex = 0;
                }
                else if (value < 0)
                {
                    _songIndex = QueueCount;
                }
                else
                {
                    _songIndex = value;
                }
            }
        }
        private static int QueueCount
        {
            get => _mearQueue.Count;
        }
		#endregion


		#region Constructors
		#endregion


		#region Methods
        public static async Task<Song> ControlMusic(Song song, PlayControls control)
        {
            if (song != null)
            {
                _song = song;
            }
            switch (control)
            {
                case PlayControls.PLAYOFFLINE:
                    DeterminePlayType();
                    var plyCountRepo = new DBPlayCountRepository();
                    plyCountRepo.AffectPlayCount(_song);
                    InitializeRepeatMode();
                    break;
                case PlayControls.PAUSE:
                    PauseSong();
                    break;
                case PlayControls.RESUME:
                    ResumeSong();
                    break;
                case PlayControls.STREAM:
                    InitializeRepeatMode();
                    return await StreamSong();
                case PlayControls.REPEAT:
                    ToggleRepeat();
                    break;
                case PlayControls.SHUFFLE:
                    ToggleShuffle();
                    break;
                case PlayControls.NEXT:
                    var res = DetermineControlFlow();
                    if (!res)
                    {
                        QueueIndex++;
                    }
                    _song = _mearQueue.ToArray()[QueueIndex];
                    DeterminePlayType();

                    _songChanged[MusicViews.Player] = true;
                    break;
                case PlayControls.PREVIOUS:
                    var prevRes = DetermineControlFlow();
                    if (!prevRes)
                    {
                        QueueIndex--;
                    }
                    _song = _mearQueue.ToArray()[QueueIndex];
                    DeterminePlayType();

                    _songChanged[MusicViews.Player] = true;
                    break;
            }

            return null;
        }

        public static async Task<string> ConvertToTime()
        {
            try
            {
                var ttlSec = (int)CrossMediaManager.Current.Position.TotalSeconds;
                return TimeFormat.ConvertToSongTime(ttlSec);
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

        public static async Task AlterIndex(Song song)
        {
            QueueIndex = _mearQueue.ToArray().ToList().IndexOf(song);
        }

        public static async Task LoadQueue(List<Song> songs)
        {
            if (_mearQueue == null)
            {
                _mearQueue = new Queue<Song>();
            }
            _mearQueue.Clear();
            songs.ForEach(_mearQueue.Enqueue);
            _songIndex = 0;
        }
        public static async Task DownloadSongToFS()
        {
			var songRepo = new RemoteSongRepository();
			songRepo.DownloadSong(_song);

            _song.Downloaded = true;
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
        public static async Task ResetSongChange(MusicViews type)
        {
            _songChanged[type] = false;
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

            switch (shuffleOn)
            {
                case Shuffle.Off:
                    return "ShfOff";
                case Shuffle.All:
                    return "ShfOn";
            }

            return string.Empty;
        }

        public static bool IsPlaying()
        {
            return CrossMediaManager.Current.IsPlaying();
        }
        public static bool SongHasBeenChanged(MusicViews type)
        {
            return _songChanged[type].Value;
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

        private static Task<Song> StreamSong()
        {
            var tmpFile = Path.GetTempPath() + "track.mp3";

            if (File.Exists(tmpFile))
                File.Delete(tmpFile);

            try
            {
                var songMgr = new SongManager();
                _song.SongPath = tmpFile;

                songMgr.StreamSong();

                /**
                var downloaded = songMgr.DownloadStream(ref _song);


                if (downloaded)
                {
                    PlaySong();
                }
                */

                PlaySong();
                return Task.Run(() =>
                {
                    return _song;
                });
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
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

        private static bool DetermineControlFlow()
        {
            var ctrlRepo = new DBMusicControlsRepository();
            var repeatMode = ctrlRepo.IsRepeatOn();

            var repRes = DetermineControlFlow(repeatMode);
            if (repRes)
            {
                return true;
            }

            var shuffleMode = ctrlRepo.IsShuffleOn();
            var shfRes = DetermineControlFlow(shuffleMode);
            if (shfRes)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        private static bool DetermineControlFlow(Repeat repeatMode)
        {
            switch (repeatMode)
            {
                case Repeat.ONE:
                    CrossMediaManager.Current.SeekToStart();
                    PlaySong();
                    return true;
                case Repeat.ALL:
                    PlaySong();
                    return true;
            }

            return false;
        }
        private static bool DetermineControlFlow(Shuffle shuffleMode)
        {
            switch (shuffleMode)
            {
                case Shuffle.Off:
                    return false;
                case Shuffle.All:
                    var rnd = new Random();
                    var rando = rnd.Next(QueueCount);
                    QueueIndex = rando;
                    return true;
            }

            return false;
        }
        private static void DeterminePlayType()
        {
            if (!_song.Downloaded)
            {
                StreamSong();
            }
            else
            {
                PlaySong();
            }
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
        private static async Task PlaySong()
        {
            await CrossMediaManager.Current.Play(_song.SongPath);

            if (_initialized == null)
            {
                CrossMediaManager.Current.MediaItemFinished += Current_MediaItemFinished;
                _initialized = true;
            }

            _songChanged[MusicViews.Song] = true;
            _songChanged[MusicViews.Album] = true;
            _songChanged[MusicViews.Artist] = true;
        }
        private static async Task PlaySong(string songPath)
        {
            await CrossMediaManager.Current.Play(songPath);
            InitializePlayer();

            SongChanged();
        }
        private static async Task ResumeSong()
        {
            await CrossMediaManager.Current.PlayPause();
        }

        private static void InitializePlayer()
        {
            if (_initialized == null)
            {
                CrossMediaManager.Current.MediaItemFinished += Current_MediaItemFinished;
                CrossMediaManager.Current.VolumeManager.CurrentVolume = CrossMediaManager.Current.VolumeManager.MaxVolume;
                _initialized = true;
            }
        }
        private static void SongChanged()
        {
            _songChanged[MusicViews.Song] = true;
            _songChanged[MusicViews.Album] = true;
            _songChanged[MusicViews.Artist] = true;
        }
        private static void ToggleRepeat()
        {
            var musicCtrl = new DBMusicControlsRepository();
            musicCtrl.UpdateRepeat();
            var repeatMode = musicCtrl.IsRepeatOn();

            CrossMediaManager.Current.RepeatMode = RepeatUtility.RetrieveRepeatMode(repeatMode);
        }
        private static void ToggleShuffle()
        {
            var musicCtrl = new DBMusicControlsRepository();
            musicCtrl.UpdateShuffle();
            var shuffleMode = musicCtrl.IsShuffleOn();

            CrossMediaManager.Current.ShuffleMode = ShuffleUtility.RetrieveShuffleMode(shuffleMode);
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

            DetermineControlFlow();

            switch(repeatMode)
            {
                case Repeat.ONE:
                    CrossMediaManager.Current.SeekToStart();
                    PlaySong(_song.SongPath);
                    return;
                case Repeat.ALL:
                    // Will implment this fully later once Queues are a feature
                    PlaySong(_song.SongPath);
                    return;
            }

            var shuffleMode = ctrlRepo.IsShuffleOn();

            switch (shuffleMode)
            {
                case Shuffle.Off:
                    QueueIndex++;
                    break;
                case Shuffle.All:
                    Random rnd = new Random();
                    var rando = rnd.Next(QueueCount);
                    QueueIndex = rando;
                    break;
            }

            _song = _mearQueue.ToArray()[QueueIndex];
            if (_song.Downloaded)
            {
                PlaySong();
            }
            else
            {
                StreamSong();
            }

            _songChanged[MusicViews.Player] = true;
        }
        #endregion
        #endregion


        #region Enums
        public enum MusicViews
        {
            Song = 0,
            Album,
            Artist,
            Player
        }
        #endregion
    }
}
