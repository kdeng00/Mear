using System;
using System.Collections.Generic;
using System.Text;

using Mear.Models;
using Mear.Models.PlayerControls;
using Mear.Utilities;

namespace Mear.Repositories.Database
{
    public class DBMusicControlsRepository : DBRepository
    {
        #region Fields
        #endregion


        #region Properties
        #endregion


        #region Constructors
        public DBMusicControlsRepository()
        {
            Initialize();
            InitializeTable();
        }
        #endregion


        #region Methods
        public Shuffle IsShuffleOn()
        {
            try
            {
                if (DoesTableExist("MusicControls"))
                {
                    bool? shuffle = _Db.Table<MusicControls>().First().ShuffleOn;

                    if (shuffle != null)
                    {
                        return shuffle.Value ? Shuffle.All : Shuffle.Off;
                    }
                }
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }

            return Shuffle.Off;
        }
        public Repeat IsRepeatOn()
        {
            try
            {
                if (DoesTableExist("MusicControls"))
                {
                    var repeat = _Db.Table<MusicControls>().First().RepeatOn;

                    return (Repeat)repeat;
                }
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }

            return Repeat.OFF;
        }

        public void UpdateRepeat()
        {
            try
            {
                if (DoesTableExist("MusicControls"))
                {
                    var control = RetrieveMusicControls();
                    var repeatMode = (Repeat)control.RepeatOn;
                    control.RepeatOn = RepeatUtility.ToggleRepeatMode(repeatMode);

                    _Db.Update(control);
                }
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }
        }
        public void UpdateShuffle()
        {
            try
            {
                if (DoesTableExist("MusicControls"))
                {
                    var control = RetrieveMusicControls();
                    control.ShuffleOn = (!control.ShuffleOn);

                    _Db.Update(control);
                }
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }
        }

        private MusicControls RetrieveMusicControls()
        {
            try
            {
                var controls = _Db.Table<MusicControls>().First();

                return controls;
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }

            return null;
        }
        private void InitializeTable()
        {
            try
            {
                if (!DoesTableExist("MusicControls"))
                {
                    _Db.CreateTable<MusicControls>();
                }

                var controls = RetrieveMusicControls();

                if (controls == null)
                {
                    var initControl = new MusicControls();
                    _Db.Insert(initControl);
                }
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }
        }
        #endregion
    }
}
