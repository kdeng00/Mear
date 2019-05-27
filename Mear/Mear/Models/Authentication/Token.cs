using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Text;

using SQLite;

namespace Mear.Models.Authentication
{
	[DataContract]
	[Table("Token")]
	public class Token
	{
		[PrimaryKey, Column("Id"), AutoIncrement]
		public int Id { get; set; }
		public string AccessToken { get; set; }
		public int UserId { get; set; }
	}
}
