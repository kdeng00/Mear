using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

using Mear.Constants;
using Mear.Playback;
using Mear.ViewModels;

namespace Mear.Views
{
	[XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class AlbumView : ContentPage
	{
		#region Fields
        private StackLayout _playerIndicatorLayout;
        private Button _controlButton;
        private Label _songTitleLabel;
		private AlbumViewModel _viewModel;
		#endregion


		#region Properties
		#endregion


		#region Constructors
		public AlbumView()
		{
			InitializeComponent();
            AddPlayerIndicator();
            BackgroundControl();

			BindingContext = _viewModel = new AlbumViewModel();
		}
		#endregion


		#region Methods
        private async Task AddPlayerIndicator()
        {
            var mainLayout = AlbumViewMainLayout;
            if (_playerIndicatorLayout == null)
            {
                _playerIndicatorLayout = await ConfigureSongIndicatorLayout();
            }

            mainLayout.Children.Add(_playerIndicatorLayout);
        }
        private async Task<StackLayout> ConfigureSongIndicatorLayout()
        {
            var indicatorLayout = new StackLayout();
            indicatorLayout.Orientation = StackOrientation.Horizontal;
            indicatorLayout.Padding = 5;
            _controlButton = new Button();
            _controlButton.Clicked += ControlButton_Clicked;
            _songTitleLabel = new Label();
            _songTitleLabel.HorizontalOptions = LayoutOptions.Center;
            _songTitleLabel.VerticalOptions = LayoutOptions.Start;
            var isSongPlayer = MearPlayer.IsPlaying();

            if (isSongPlayer)
            {
                _controlButton.Text = "P";
                _songTitleLabel.Text = await MearPlayer.SongTitle();
            }
            else
            {
                _controlButton.Text = "S";
                _songTitleLabel.Text = string.Empty;
            }

            indicatorLayout.Children.Add(_controlButton);
            indicatorLayout.Children.Add(_songTitleLabel);

            return indicatorLayout;
        }

        #region Background
        private async Task BackgroundControl()
        {
            try
            {
                new Thread(async () =>
                {
                    while (true)
                    {
                        Device.BeginInvokeOnMainThread(async () =>
                        {
                            if (MearPlayer.SongHasBeenChanged(MearPlayer.MusicViews.Album))
                            {
                                _songTitleLabel.Text = MearPlayer.SongTitle().Result;
                                MearPlayer.ResetSongChange(MearPlayer.MusicViews.Album);
                            }
                        });
                        await Task.Delay(800);
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
        private async void ControlButton_Clicked(object sender, EventArgs e)
        {
            if (MearPlayer.IsPlaying())
            {
                MearPlayer.ControlMusic(null, PlayControls.PAUSE);
            }
            else
            {
                MearPlayer.ControlMusic(null, PlayControls.RESUME);
            }
        }

		private void AlbumListView_ItemSelected(object sender, SelectedItemChangedEventArgs e)
		{
            var albumItem = (sender as ListView).SelectedItem = null;
		}

        private async void SearchAlbum_TextChanged(object sender, TextChangedEventArgs e)
        {
            var text = e.NewTextValue;
            _viewModel.SearchAlbums(text);
        }
        #endregion
        #endregion
    }
}