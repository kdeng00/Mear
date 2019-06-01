using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;

using Mear.Models;

namespace Mear.ViewModels
{
	public class MearPlayerViewModel : BaseViewModel
	{
		#region Fields
		private ObservableCollection<Song> _song;
		#endregion


		#region Properties
		public ObservableCollection<Song> SongItem
		{
			get => _song;
		}
		#endregion


		#region Constructors
		public MearPlayerViewModel()
		{
		}
		public MearPlayerViewModel(Song song)
		{
			_song = new ObservableCollection<Song>();
			_song.Clear();
			_song.Add(song);
		}
		#endregion


		#region Methods
		#endregion
	}
}
