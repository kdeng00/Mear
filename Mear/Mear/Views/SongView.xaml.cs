using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

using Mear.Models;
using Mear.Playback;
using Mear.Repositories.Remote;
using Mear.ViewModels;

namespace Mear.Views
{
	[XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class SongView : ContentPage
	{
		#region Fields
		private SongViewModel _viewModel;
		#endregion


		#region Properties
		#endregion


		#region Constructors
		public SongView()
		{
			InitializeComponent();

			BindingContext = _viewModel = new SongViewModel();
		}
		#endregion

		#region Methods
		#region Events
		private async void SongListView_ItemSelected(object sender, SelectedItemChangedEventArgs e)
		{
			var i = "here";
			if (SongListView.SelectedItem == null)
			{
				SongListView.SelectedItem = null;
				return;
			}

			try
			{
				var song = (Song)SongListView.SelectedItem;
				await MearPlayer.StreamSongDemoAsync(song);
				await Navigation.PushModalAsync(new MearPlayerView());
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}

			SongListView.SelectedItem = null;
		}

		private async void SongOptions_Clicked(object sender, EventArgs e)
		{
			try
			{
				var btn = (Button)sender;
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}
		}
		#endregion

		#region Test
		public void TestRemoteSong()
		{
			var songRepo = new RemoteSongRepository();
			var songs = songRepo.RetrieveSongs();
		}
		#endregion

		#endregion
	}
}