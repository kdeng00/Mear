﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using MediaManager;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

using Mear.Constants;
using Mear.Models;
using Mear.Playback;
using Mear.Repositories.Database;
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
			if (SongListView.SelectedItem == null)
			{
				SongListView.SelectedItem = null;
				return;
			}

			try
			{
				var song = (Song)SongListView.SelectedItem;
				if (song.Downloaded)
				{
                    await MearPlayer.ControlMusic(song, PlayControls.PLAYOFFLINE);
				}
				else
				{
                    song = await MearPlayer.ControlMusic(song, PlayControls.STREAM);
				}

			    Navigation.PushModalAsync(new NavigationPage(new MearPlayerView(song)));
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}

			SongListView.SelectedItem = null;
		}

        private void SearchSong_TextChanged(object sender, TextChangedEventArgs e)
        {
            var text = e.NewTextValue.ToString();
            _viewModel.FilterSongs(text);
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