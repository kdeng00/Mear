using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

using Mear.Models;

namespace Mear.Views
{
	[XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class MearPlayerView : ContentPage
	{
		#region Fields
		private Song _song;
		#endregion


		#region Properties
		#endregion


		#region Constructors
		public MearPlayerView()
		{
			InitializeComponent();
			Initialize();
		}
		public MearPlayerView(Song song)
		{
			InitializeComponent();
			_song = song;
			Initialize();
			InitializeControls();
		}
		#endregion


		#region Methods
		private void Initialize()
		{
			SongTitle.Text = "";
			ArtistName.Text = "";
			AlbumName.Text = "";
			EndTime.Text = "";
		}
		private void InitializeControls()
		{
			SongTitle.Text = _song.Title;
			ArtistName.Text = _song.Artist;
			AlbumName.Text = _song.Album;
			EndTime.Text = $"{_song.Duration}";
		}

		#region Events
		private void Previous_Clicked(object sender, EventArgs e)
		{

		}
		private void Play_Clicked(object sender, EventArgs e)
		{

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
		#endregion
		#endregion
	}
}