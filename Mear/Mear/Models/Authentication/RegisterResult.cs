using System;
using System.Collections.Generic;
using System.Text;

using Newtonsoft.Json;

namespace Mear.Models.Authentication
{
	public class RegisterResult : BaseResult
	{
		[JsonProperty("username")]
		public string Username { get; set; }
		[JsonProperty("successfully_registered")]
		public bool SuccessfullyRegistered { get; set; }
	}
}
