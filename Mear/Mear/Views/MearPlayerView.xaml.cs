﻿using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

using MediaManager;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

using Mear.Constants;
using Mear.Models;
using Mear.Models.PlayerControls;
using Mear.Playback;
using Mear.Repositories.Database;
using Mear.Repositories.Remote;
using Mear.Utilities;
using Mear.ViewModels;

using RepeatMode = Mear.Models.PlayerControls.Repeat;

namespace Mear.Views
{
	[XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class MearPlayerView : ContentPage
	{
		#region Fields
		private MearPlayerViewModel _viewModel;
		private Song _song;
		#endregion


		#region Properties
		#endregion


		#region Constructors
		public MearPlayerView()
		{
			InitializeComponent();

			BindingContext = _viewModel = new MearPlayerViewModel();
		}
		public MearPlayerView(Song song)
		{
			InitializeComponent();
			_song = song;
			InitializeControls();

			BackgroundSongElasping();
			BackgroundSongCoverUpdate();
            //BackgroundControlInit();

            InitializeOptions();

			BindingContext = _viewModel = new MearPlayerViewModel(song);
		}
		#endregion


		#region Methods
        private ToolbarItem DownloadOption()
        {
            var dnloadOpt = new ToolbarItem();
            dnloadOpt.Text = "Download";
            dnloadOpt.Order = ToolbarItemOrder.Secondary;
            dnloadOpt.Priority = 1;
            dnloadOpt.Clicked += Download_Clicked;

            return dnloadOpt;
        }
        private ToolbarItem RemoveOption()
        {
            var rmvOpt = new ToolbarItem();
            rmvOpt.Text = "Remove";
            rmvOpt.Order = ToolbarItemOrder.Secondary;
            rmvOpt.Priority = 1;
            rmvOpt.Clicked += Remove_Clicked;

            return rmvOpt;
        }
		private void InitializeOptions()
		{
            if (!_song.Downloaded)
            {
                var dnloadOpt = DownloadOption();
                ToolbarItems.Add(dnloadOpt);
            }
            else
            {
                var rmvOpt = RemoveOption();
                ToolbarItems.Add(rmvOpt);
            }
		}
		private void InitializeControls()
		{
			var songCnvrt = new TimeFormat();
			var dur = _song.Duration;
			var endTime = songCnvrt.ConvertToSongTime(dur.Value);

            Shuffle.Text = MearPlayer.RetrieveShuffleString();
            Repeat.Text = MearPlayer.RetrieveRepeatString();

			EndTime.Text = endTime;
		}
        private void RemoveSyncToolbar()
        {
            var act = ToolbarItems.Where(tl => tl.Priority == 1).First();
            ToolbarItems.Remove(act);
        }

		#region Background
		private async Task BackgroundSongElasping()
		{
			try
			{
				new Thread(async () =>
				{
					while (true)
					{
						Device.BeginInvokeOnMainThread(async () =>
						{
                            var curPos = await MearPlayer.ConvertToTime();
							StartTime.Text = $"{curPos}";
                            double? progVal = await MearPlayer.ProgressValue();
							SongProgress.Value = progVal.Value;
						});

						await Task.Delay(500);
					}
				}).Start();
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}
		}
		private async Task BackgroundSongCoverUpdate()
		{
			try
			{
				new Thread(async () =>
				{
                    // Will work on this later so I am setting it to false
					while (false)
					{
						Device.BeginInvokeOnMainThread(() =>
						{
							if (SongCover.Source.IsEmpty)
							{
								var meta = new SongMetadataRetriever();
								var data = meta.ExtractCoverArtData(_song);
							}
						});

						await Task.Delay(1000);
					}
				}).Start();
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}
		}
        // Addresses issue where the Player's controls have not been properly initialized
        private async Task BackgroundControlInit()
        {
            try
            {
                new Thread(async () =>
                {
                    while (!MearPlayer.RepeatMatchedDatabase())
                    {
                        MearPlayer.UpdateRepeatControls();
                        Repeat.Text = MearPlayer.RetrieveRepeatString();
                        await Task.Delay(750);
                    }
                }).Start();
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }
        }
		#endregion

		#region Events
		private void Previous_Clicked(object sender, EventArgs e)
		{

		}
		private async void Play_Clicked(object sender, EventArgs e)
		{
			if (CrossMediaManager.Current.IsPlaying())
			{
                await MearPlayer.ControlMusic(_song, PlayControls.PAUSE);
			}
			else
			{
                await MearPlayer.ControlMusic(_song, PlayControls.RESUME);
			}
		}
		private void Next_Clicked(object sender, EventArgs e)
		{

		}
		private void Repeat_Clicked(object sender, EventArgs e)
		{
            MearPlayer.ControlMusic(_song, PlayControls.REPEAT);
            Repeat.Text = MearPlayer.RetrieveRepeatString();
		}
		private void Shuffle_Clicked(object sender, EventArgs e)
		{
            var musicCtrl = new DBMusicControlsRepository();
            musicCtrl.UpdateShuffle();
            Shuffle.Text = MearPlayer.RetrieveShuffleString();
		}


		private async void SongProgress_DragCompleted(object sender, EventArgs e)
		{
			var progVal = SongProgress.Value;
            await MearPlayer.SeekTo(progVal);
		}

		private async void Download_Clicked(object sender, EventArgs e)
		{
            await MearPlayer.DownloadSongToFS();

            RemoveSyncToolbar();
            ToolbarItems.Add(RemoveOption());

            _song.Downloaded = true;
		}
        private async void Remove_Clicked(object sender, EventArgs e)
        {
            RemoveSyncToolbar();
            await MearPlayer.RemoveSongFromFS();

            _song.Downloaded = false;

            ToolbarItems.Add(DownloadOption());
        }
        private async void PlayCount_Clicked(object sender, EventArgs e)
        {
            var playCountRepo = new DBPlayCountRepository();
            var plyCount = playCountRepo.RetrievePlayCount(_song.Id);

            if (plyCount == null)
            {
                await DisplayAlert("Play Count", $"Song has not been played", "Ok");
                return;
            }
            await DisplayAlert("PlayCount", $"Song has been played {plyCount.PlayCounter} times", "Ok");
        }
        #endregion
        #endregion
    }
}