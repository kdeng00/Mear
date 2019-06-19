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
        private ProgressBar _songProgress;
        private Button _controlButton;
        private Label _songArtistLabel;
        private Label _songTitleLabel;
		private AlbumViewModel _viewModel;
		#endregion


		#region Properties
		#endregion


		#region Constructors
		public AlbumView()
		{
			InitializeComponent();

			BindingContext = _viewModel = new AlbumViewModel();

            Initialize();
		}
		#endregion


		#region Methods
        private async Task Initialize()
        {
            AddPlayerIndicator();
            BackgroundControl();
            BackgroundSongProgress();
        }
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
            var songDetailLayout = new StackLayout();
            indicatorLayout.Orientation = StackOrientation.Horizontal;
            indicatorLayout.HorizontalOptions = LayoutOptions.Fill;
            indicatorLayout.MinimumWidthRequest = 400;
            indicatorLayout.Padding = 5;

            _songProgress = new ProgressBar();
            _songProgress.HorizontalOptions = LayoutOptions.Fill;
            _songProgress.MinimumWidthRequest = 400;
            _songProgress.Progress = 0;

            _controlButton = new Button();
            _controlButton.Clicked += ControlButton_Clicked;
            _songTitleLabel = new Label();
            _songTitleLabel.FontSize = 13;
            _songTitleLabel.HorizontalOptions = LayoutOptions.Start;
            _songTitleLabel.VerticalOptions = LayoutOptions.Start;
            _songArtistLabel = new Label();
            _songArtistLabel.FontSize = 10;

            var isSongPlayer = MearPlayer.IsPlaying();

            if (isSongPlayer)
            {
                _controlButton.Text = "P";
                _songTitleLabel.Text = MearPlayer.OnSong.Title;
                _songArtistLabel.Text = MearPlayer.OnSong.Artist;
            }
            else
            {
                _controlButton.Text = "S";
                _songTitleLabel.Text = string.Empty;
                _songArtistLabel.Text = string.Empty;
            }

            _songTitleLabel.HorizontalOptions = LayoutOptions.Fill;

            var tapped = new TapGestureRecognizer();
            tapped.Tapped += OpenPlayer;
            songDetailLayout.GestureRecognizers.Add(tapped);

            songDetailLayout.Children.Add(_songTitleLabel);
            songDetailLayout.Children.Add(_songArtistLabel);
            songDetailLayout.Children.Add(_songProgress);

            indicatorLayout.Children.Add(_controlButton);
            indicatorLayout.Children.Add(songDetailLayout);

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
                                _songTitleLabel.Text = MearPlayer.OnSong.Title;
                                _songArtistLabel.Text = MearPlayer.OnSong.Artist;
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
        private async Task BackgroundSongProgress()
        {
            try
            {
                new Thread(async () =>
                {
                    while (true)
                    {
                        Device.BeginInvokeOnMainThread(async () =>
                        {
                            var progress = MearPlayer.ProgressValue().Result;
                            if (progress != null)
                            {
                                _songProgress.Progress = progress.Value / 100.0;
                            }
                        });

                        await Task.Delay(250);
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
        private async void OpenPlayer(object sender, EventArgs e)
        {
            if (MearPlayer.IsPlaying())
            {
                Navigation.PushModalAsync(new NavigationPage(new MearPlayerView(MearPlayer.OnSong)));
            }
        }
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