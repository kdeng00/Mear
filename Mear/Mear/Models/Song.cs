using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Text;

using Newtonsoft.Json;
using SQLite;

namespace Mear.Models
{
	[DataContract]
	[Table("Song")]
    public class Song
    {
		[PrimaryKey, Column("Id"), AutoIncrement]
        [JsonProperty("id")]
        public int Id { get; set; }
        [JsonProperty("title")]
        public string Title { get; set; }
        [JsonProperty("album")]
        public string Album { get; set; }
        [JsonProperty("artist")]
        public string Artist { get; set; }
        [JsonProperty("year")]
        public int? Year { get; set; }
        [JsonProperty("genre")]
        public string Genre { get; set; }
        [JsonProperty("duration")]
        public int? Duration { get; set; }
        [JsonProperty("filename")]
        public string Filename { get; set; }
        [JsonIgnore]
        public string SongPath { set; get; }
    }
}
