using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;

using Mear.Models;
using Mear.Repositories.Remote;

namespace Mear.ViewModels
{
	public class AlbumViewModel : BaseViewModel
	{
		#region Fields
		private ObservableCollection<Album> _albumItems;
		private Command _refreshAlbums;
        private Command _searchAlbumCommand;
        private List<Album> _albums;
		#endregion


		#region Properties
		public ObservableCollection<Album> AlbumItems
		{
			get => _albumItems;
		}

		public Command RefreshAlbums
		{
			get => _refreshAlbums;
		}
        public Command SearchAlbumCommand
        {
            get => _searchAlbumCommand;
            set => _searchAlbumCommand = value;
        }
		#endregion


		#region Constructors
		public AlbumViewModel()
		{
			_albumItems = new ObservableCollection<Album>();
			_refreshAlbums = new Command(async () => await PopulateAlbumsAsync());
            _searchAlbumCommand = new Command(async () => await SearchAlbum());

			PopulateAlbumsAsync();
		}
		#endregion


		#region Methods
        public async void SearchAlbums(string text)
        {
            if (string.IsNullOrEmpty(text))
            {
                _albumItems.Clear();
                _albums.ToList().ForEach(_albumItems.Add);
            }
            else
            {
                var albumItems = _albums.Where(a => a.Title.Contains(text)).ToList();
                _albumItems.Clear();
                albumItems.ToList().ForEach(_albumItems.Add);
            }
        }

		private async Task PopulateAlbumsAsync()
		{
			try
			{
				var albumRepo = new RemoteAlbumRepository();
				_albums = albumRepo.RetrieveAlbums().OrderBy(a => a.Title).ToList();

				_albumItems.Clear();

                _albums.ToList().ForEach(_albumItems.Add);
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}

            IsRefreshing = false;
		}
        private async Task SearchAlbum()
        {
        }
		#endregion
	}
}
