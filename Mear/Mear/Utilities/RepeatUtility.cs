using System;
using System.Collections.Generic;
using System.Text;

using MediaManager;

using Mear.Models.PlayerControls;

namespace Mear.Utilities
{
    public class RepeatUtility
    {
        #region Fields
        #endregion


        #region Properties
        #endregion


        #region Constructors
        #endregion


        #region Methods
        public static MediaManager.Playback.RepeatMode RetrieveRepeatMode(Repeat repeatMode)
        {
            switch (repeatMode)
            {
                case Repeat.OFF:
                    return MediaManager.Playback.RepeatMode.Off;
                case Repeat.ONE:
                    return MediaManager.Playback.RepeatMode.One;
                case Repeat.ALL:
                    return MediaManager.Playback.RepeatMode.All;
            }

            return MediaManager.Playback.RepeatMode.Off;
        }

        public static string RepeatString(Repeat repeatMode)
        {
            switch (repeatMode)
            {
                case Repeat.OFF:
                    return "RepOff";
                case Repeat.ONE:
                    return "RepOne";
                case Repeat.ALL:
                    return "RepAll";
            }

            return string.Empty;
        }
        public static string ToggleRepeatString(Repeat repeatMode)
        {
            switch (repeatMode)
            {
                case Repeat.OFF:
                    return "RepOn";
                case Repeat.ONE:
                    return "RepAll";
                case Repeat.ALL:
                    return "RepOff";
            }

            return string.Empty;
        }

        public static int ToggleRepeatMode(Repeat repeatMode)
        {
            switch (repeatMode)
            {
                case Repeat.OFF:
                    return (int)Repeat.ONE;
                case Repeat.ONE:
                    return (int)Repeat.ALL;
                case Repeat.ALL:
                    return (int)Repeat.OFF;
            }

            return 0;
        }
        #endregion
    }
}
