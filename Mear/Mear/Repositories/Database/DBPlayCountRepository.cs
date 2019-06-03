﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Mear.Models;

namespace Mear.Repositories.Database
{
    public class DBPlayCountRepository : DBRepository
    {
        #region Fields
        #endregion


        #region Properties
        #endregion


        #region Constructors
        public DBPlayCountRepository()
        {
            Initialize();
        }
        #endregion


        #region Methods
        public PlayCount RetrievePlayCount(int songId)
        {
            try
            {
                if (DoesTableExist("PlayCount"))
                {
                    var playCount = _Db.Table<PlayCount>()
                        .Where(p => p.SongId==songId).First();

                    if (playCount != null)
                    {
                        return playCount;
                    }
                }
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }

            return null;
        }

        public void SavePlayCount(Song song)
        {
            try
            {
                if (!DoesTableExist("PlayCount"))
                {
                    _Db.CreateTable<PlayCount>();
                }
                var playCount = new PlayCount
                {
                    PlayCounter = 1,
                    SongId = song.Id
                };

                _Db.Insert(playCount);
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }
        }
        public void UpdatePlayCount(Song song)
        {
            try
            {
                if (DoesTableExist("PlayCount"))
                {
                    var songId = song.Id;
                    var plyCount = RetrievePlayCount(songId);
                    if (plyCount != null)
                    {
                        plyCount.PlayCounter += 1;

                        _Db.Update(plyCount);
                    }
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