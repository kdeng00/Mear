using System;
using System.Collections.Generic;
using System.Text;

using MediaManager;

using Mear.Models.PlayerControls;

namespace Mear.Utilities
{
    public class ShuffleUtility
    {
        #region Methods
        public static MediaManager.Queue.ShuffleMode RetrieveShuffleMode(Shuffle shuffleMode)
        {
            switch (shuffleMode)
            {
                case Shuffle.Off:
                    return MediaManager.Queue.ShuffleMode.Off;
                case Shuffle.All:
                    return MediaManager.Queue.ShuffleMode.All;
            }

            return MediaManager.Queue.ShuffleMode.Off;
        }
        #endregion
    }
}
