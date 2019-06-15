using System;
using System.Collections.Generic;
using System.Text;

namespace Mear.Utilities
{
	public class TimeFormat
	{
		#region Fields
		#endregion


		#region Properties
		#endregion


		#region Constructors
		#endregion


		#region Methods
		public static string ConvertToSongTime(int seconds)
		{
			var dur = seconds;
			var min = TimeSpan.FromSeconds((double) dur).Minutes;
			var remainingSec = dur % 60;

			if (remainingSec < 10)
			{
				return $"{min}:0{remainingSec}";
			}
			else
			{
				return $"{min}:{remainingSec}";
			}
		}
		#endregion
	}
}
