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
		public string ConvertToSongTime(int seconds)
		{
			var curTime = string.Empty;
			var dur = seconds;
			var min = TimeSpan.FromSeconds((double) dur).Minutes;
			var remainingSec = dur % 60;

			if (remainingSec < 10)
			{
				curTime = $"{min}:0{remainingSec}";
			}
			else
			{
				curTime = $"{min}:{remainingSec}";
			}

			return curTime;
		}
		#endregion
	}
}
