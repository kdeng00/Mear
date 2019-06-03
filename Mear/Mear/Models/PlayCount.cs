using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Text;

using SQLite;

namespace Mear.Models
{
    [DataContract]
    [Table("PlayCount")]
	public class PlayCount
	{
        [PrimaryKey, Column("Id"), AutoIncrement]
		public int Id { get; set; }
		public int? PlayCounter { get; set; }
		public int? SongId { get; set; }
	}
}
