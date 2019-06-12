using System;
using System.Collections.Generic;
using System.Text;

using Mear.Models;

namespace Mear.Repositories.Database
{
    public class DBSettingsRepository : DBRepository
    {
        #region Fields
        #endregion


        #region Properties
        #endregion


        #region Constructors
        public DBSettingsRepository()
        {
            _tableName = "Settings";
            Initialize();
            InitializeTable();
        }
        #endregion


        #region Methods
        public bool IsDarkThemeOn()
        {
            try
            {
                if (DoesTableExist(_tableName))
                {
                    bool? darkTheme = RetrieveSettings().DarkTheme;

                    if (darkTheme != null)
                    {
                        return darkTheme.Value;
                    }
                }
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }

            return true;
        }

        public void UpdateDarkTheme()
        {
            try
            {
                if (DoesTableExist(_tableName))
                {
                    var settings = RetrieveSettings();
                    settings.DarkTheme = !settings.DarkTheme;

                    _Db.Update(settings);
                }
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }
        }
        public void UpdateDarkTheme(bool isDarkTheme)
        {
            try
            {
                if (DoesTableExist(_tableName))
                {
                    var settings = RetrieveSettings();
                    settings.DarkTheme = isDarkTheme;
                    _Db.Update(settings);
                }
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }
        }

        private Settings RetrieveSettings()
        {
            try
            {
                var settings = _Db.Table<Settings>().First();

                return settings;
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
                if (!DoesTableExist(_tableName))
                {
                    _Db.CreateTable<Settings>();
                }

                var settings = RetrieveSettings();

                if (settings == null)
                {
                    var initSettings = new Settings();
                    _Db.Insert(initSettings);
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
