using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;

using Xamarin.Forms;

using Mear.Models;
using Mear.Repositories.Remote;

namespace Mear.ViewModels
{
	public class SongViewModel : BaseViewModel
	{
		#region Fields
		private ObservableCollection<Song> _songItems;
		#endregion


		#region Properties
		public ObservableCollection<Song> SongItems
		{
			get => _songItems;
			set => _songItems = value;
		}
		#endregion


		#region Constructors
		public SongViewModel()
		{
			_songItems = new ObservableCollection<Song>();

			PopulateSongs();
		}
		#endregion


		#region Methods
		public void PopulateSongs()
		{
			var songRepo = new RemoteSongRepository();
			var songs = songRepo.RetrieveSongs().OrderBy(s => s.Title).ToList();

			foreach (var song in songs)
			{
				_songItems.Add(song);
			}
		}
		#endregion
	}
}
