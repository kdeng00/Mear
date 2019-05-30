using System;
using System.Collections.Generic;
using System.Text;

using Newtonsoft.Json;

namespace Mear.Models
{
	public class Album
	{
		[JsonProperty("id")]
		public int Id { get; set; }
		[JsonProperty("title")]
		public string Title { get; set; }
		[JsonProperty("album_artist")]
		public string AlbumArtist { get; set; }
		[JsonProperty("song_count")]
		public int SongCount { get; set; }
	}
}
