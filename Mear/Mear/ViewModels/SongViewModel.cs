using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;

using Mear.Models;
using Mear.Repositories.Local;
using Mear.Repositories.Remote;

namespace Mear.ViewModels
{
    public class SongViewModel : BaseViewModel
    {
        #region Fields
        private ObservableCollection<Song> _songItems;
        private Command _refreshSongs;
        private Command _searchSongsCommand;
        private string _searchedText;
        #endregion


        #region Properties
        public ObservableCollection<Song> SongItems
        {
            get => _songItems;
            set => _songItems = value;
        }

        public Command RefreshSongs
        {
            get => _refreshSongs;
            set => _refreshSongs = value;
        }
        public Command SearchSongsCommand
        {
            get => _searchSongsCommand;
            set => _searchSongsCommand = value;
        }

        public string SearchedText
        {
            get => _searchedText;
            set
            {
                _searchedText = value;
                NotifyPropertyChanged("SearchSong");
                //SearchSongText(_searchedText);
            }
        }
        #endregion


        #region Constructors
        public SongViewModel()
        {
            _songItems = new ObservableCollection<Song>();
            _refreshSongs = new Command(async () => await PopulateSongsAsync());
            _searchSongsCommand = new Command(async () => await SearchSongText());

            PopulateSongs();
        }
        #endregion


        #region Methods
        public void PopulateSongs()
        {
            var songRepo = new RemoteSongRepository();
            var localSongRepo = new LocalSongRepository();
            var lclSongs = localSongRepo.RetrieveSongs();
            var rmtSongs = songRepo.RetrieveSongs().OrderBy(s => s.Title).ToList();
            var songs = new List<Song>();
            if (lclSongs != null)
            {
                lclSongs = lclSongs.OrderBy(s => s.Title).ToList();
                songs = MergeSongs(lclSongs, rmtSongs);
            }
            else
            {
                songs = rmtSongs;
            }

            foreach (var song in songs)
            {
                _songItems.Add(song);
            }
        }

        private List<Song> MergeSongs(List<Song> local, List<Song> remote)
        {
            try
            {
                foreach (Song lclSong in local)
                {
                    Song song = remote.Find(s => (s.Title.Equals(lclSong.Title)));
                    remote.Remove(song);
                    remote.Add(lclSong);
                }

                return remote;
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }

            return null;
        }
        private async Task PopulateSongsAsync()
        {
            try
            {
                var songRepo = new RemoteSongRepository();
                var localSongRepo = new LocalSongRepository();
                var lclSongs = localSongRepo.RetrieveSongs();
                var rmtSongs = songRepo.RetrieveSongs().OrderBy(s => s.Title).ToList();
                var songs = new List<Song>();
                if (lclSongs != null)
                {
                    lclSongs = lclSongs.OrderBy(s => s.Title).ToList();
                    songs = MergeSongs(lclSongs, rmtSongs);
                }
                else
                {
                    songs = rmtSongs;
                }

                _songItems.Clear();

                foreach (var song in songs)
                {
                    _songItems.Add(song);
                }
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }

            IsRefreshing = false;
        }

        private async Task SearchSongText()
        {
            if (_searchedText == null)
            {
                return;
            }
            var searchPhrase = _searchedText.Trim();

            const int SEARCH_PHRASE_MIN_LENGTH = 3;
            if (searchPhrase.Length < SEARCH_PHRASE_MIN_LENGTH) return; // SEARCH_PHRASE_MIN_LENGTH = 3

            _songItems.Where(sng => sng.Title.Contains(_searchedText) || sng.Artist.Contains(_searchedText));

        }
        #endregion
    }
    }
