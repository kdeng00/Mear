using System;
using System.Collections.Generic;
using System.Text;

using Mear.Models;

namespace Mear.Repositories.Database
{
    // TODO: Implement the database functionality #35
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

        public void UpdateRepeat()
        {
            try
            {
                if (DoesTableExist("MusicControls"))
                {
                    var control = RetrieveMusicControls();
                    control.RepeatOn = !control.RepeatOn;
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
