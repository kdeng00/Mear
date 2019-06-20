using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Text;

using Newtonsoft.Json;
using SQLite;

namespace Mear.Models.Authentication
{
    [DataContract]
    [Table("User")]
	public class User
	{
        [PrimaryKey, Column("Id"), AutoIncrement]
		[JsonProperty("id")]
		public int Id { get; set; }
		[JsonProperty("username")]
		public string Username { get; set; }
        [Ignore]
		[JsonProperty("nickname")]
		public string Nickname { get; set; }
		[JsonProperty("password")]
		public string Password { get; set; }
        [Ignore]
		[JsonProperty("email")]
		public string Email { get; set; }
        [Ignore]
		[JsonProperty("phone_number")]
		public string PhoneNumber { get; set; }
        [Ignore]
		[JsonProperty("first_name")]
		public string Firstname { get; set; }
        [Ignore]
		[JsonProperty("last_name")]
		public string Lastname { get; set; }
        [Ignore]
		[JsonProperty("email_verified")]
		public bool EmailVerified { get; set; }
        [Ignore]
		[JsonProperty("date_created")]
		public DateTime DateCreated { get; set; }
        [Ignore]
		[JsonProperty("last_login")]
		public DateTime? LastLogin { get; set; }
	}
}
