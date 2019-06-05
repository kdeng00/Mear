using System;
using System.Collections.Generic;
using System.Text;

using Mear.Models;

namespace Mear.Repositories.Database
{
    // TODO: Implement the database functionality #35
    public class DBMusicControls : DBRepository
    {
        #region Fields
        #endregion


        #region Properties
        #endregion


        #region Constructors
        public DBMusicControls()
        {
            Initialize();
        }
        #endregion


        #region Methods
        public bool IsShuffleOn()
        {
            try
            {
                if (DoesTableExist("MusicControls"))
                {
                    bool? shuffle = _Db.Table<MusicControls>().First().ShuffleOn;

                    if (shuffle != null)
                    {
                        return shuffle.Value; ;
                    }
                }
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }

            return false;
        }
        public bool IsRepeatOn()
        {
            try
            {
                if (DoesTableExist("MusicControls"))
                {
                    bool? repeat = _Db.Table<MusicControls>().First().RepeatOn;

                    if (repeat != null)
                    {
                        return repeat.Value;
                    }
                }
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }

            return false;
        }
        #endregion
    }
}
