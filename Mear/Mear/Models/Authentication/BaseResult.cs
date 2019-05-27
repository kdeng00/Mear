using System;
using System.Collections.Generic;
using System.Text;

using Newtonsoft.Json;

namespace Mear.Models.Authentication
{
	public class BaseResult
	{
		[JsonProperty("message")]
		public string Message { get; set; }
	}
}
