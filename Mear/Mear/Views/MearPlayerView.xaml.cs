using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

using MediaManager;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

using Mear.Models;
using Mear.Repositories.Remote;
using Mear.Utilities;
using Mear.ViewModels;

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

			BindingContext = _viewModel = new MearPlayerViewModel(song);
		}
		#endregion


		#region Methods
		private void Initialize()
		{
		}
		private void InitializeControls()
		{
			var songCnvrt = new TimeFormat();
			var dur = _song.Duration;
			var endTime = songCnvrt.ConvertToSongTime(dur.Value);

			EndTime.Text = endTime;
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
						Device.BeginInvokeOnMainThread(() =>
						{
							var ttlSec = (int)CrossMediaManager.Current.Position.TotalSeconds;
							var cnvrt = new TimeFormat();
							var curPos = cnvrt.ConvertToSongTime(ttlSec);
							StartTime.Text = $"{curPos}";
							double progVal = ((double)ttlSec) / ((double)_song.Duration);
							progVal *= 100;
							SongProgress.Value = progVal;
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
					while (true)
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
		#endregion

		#region Events
		private void Previous_Clicked(object sender, EventArgs e)
		{

		}
		private async void Play_Clicked(object sender, EventArgs e)
		{
			if (CrossMediaManager.Current.IsPlaying())
			{
				await CrossMediaManager.Current.Pause();
			}
			else
			{
				await CrossMediaManager.Current.Play();
			}
		}
		private void Next_Clicked(object sender, EventArgs e)
		{

		}
		private void Repeat_Clicked(object sender, EventArgs e)
		{

		}
		private void Shuffle_Clicked(object sender, EventArgs e)
		{

		}


		private async void SongProgress_DragCompleted(object sender, EventArgs e)
		{
			var progVal = SongProgress.Value;
			double newPos = (progVal / 100) * ((double)_song.Duration);
			await CrossMediaManager.Current.SeekTo(TimeSpan.FromSeconds(newPos));
		}

		private void Download_Clicked(object sender, EventArgs e)
		{
			var songRepo = new RemoteSongRepository();
			songRepo.DownloadSong(_song);
		}
		#endregion

		#endregion

	}
}