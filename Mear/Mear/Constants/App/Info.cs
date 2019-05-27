using System;
using System.Collections.Generic;
using System.Text;

namespace Mear.Constants.App
{
	public class Info
	{
		#region Fields
		private static string _appName = "Mear";
		private static string _appVersion = "Dev Pre-release";
		#endregion


		#region Properties
		public static string AppName
		{
			get => _appName;
		}
		public static string AppVersion
		{
			get => _appVersion;
		}
		#endregion
	}
}
