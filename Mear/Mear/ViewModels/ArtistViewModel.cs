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
	public class ArtistViewModel : BaseViewModel
	{
		#region Fields
		private ObservableCollection<Artist> _artistItems;
		private Command _refreshArtists;
		#endregion


		#region Properties
		public ObservableCollection<Artist> ArtistItems
		{
			get => _artistItems;
		}

		public Command RefreshArtists
		{
			get => _refreshArtists;
		}
		#endregion


		#region Constructors
		public ArtistViewModel()
		{
			_artistItems = new ObservableCollection<Artist>();
			_refreshArtists = new Command(async () => await PopulateArtistsAsync());

			PopulateArtistsAsync();
		}
		#endregion


		#region Methods
		private async Task PopulateArtistsAsync()
		{
			try
			{
				var artistRepo = new RemoteArtistRepository();
				var artists = artistRepo.RetrieveArtists().OrderBy(a => a.Name).ToList();

				_artistItems.Clear();

				foreach (var artist in artists)
				{
					_artistItems.Add(artist);
				}
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}

            IsRefreshing = false;
		}
		#endregion
	}
}
