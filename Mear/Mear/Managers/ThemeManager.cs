using System;
using System.Collections.Generic;
using System.Text;

using Xamarin.Forms;

using Mear.Models;
using Mear.Repositories.Database;

namespace Mear.Managers
{
    public class ThemeManager
    {
        #region Methods
        public void InitTheme()
        {
            var settingsRepo = new DBSettingsRepository();
            if (settingsRepo.IsDarkThemeOn())
            {
                ApplyDarkTheme();
            }
            else
            {
                ApplyLightTheme();
            }
        }
        public void ChangeTheme(bool? isDarkTheme = null)
        {
            var settingsRepo = new DBSettingsRepository();
            if (isDarkTheme == null)
            {
                settingsRepo.UpdateDarkTheme();
            }
            else
            {
                settingsRepo.UpdateDarkTheme(isDarkTheme.Value);
            }

            if (settingsRepo.IsDarkThemeOn())
            {
                ApplyDarkTheme();
            }
            else
            {
                ApplyLightTheme();
            }
        }

        private void ApplyDarkTheme()
        {
            App.Current.Resources["NavigationPageColor"] = Color.FromHex("#101010");
            App.Current.Resources["BarTextColor"] = Color.FromHex("#ffffff");
            App.Current.Resources["ButtonColor"] = Color.FromHex("#360485");
            App.Current.Resources["ButtonTextColor"] = Color.FromHex("#ffffff");
            App.Current.Resources["ButtonBorderColor"] = Color.FromHex("#9955fa");
            App.Current.Resources["LabelTextColor"] = Color.FromHex("#ffffff");
            App.Current.Resources["LabelBackgroundColor"] = Color.FromHex("#333333");
            App.Current.Resources["EntryTextColor"] = Color.FromHex("#ffffff");
            App.Current.Resources["EntryPlaceholderTextColor"] = Color.FromHex("#a300ff");
            App.Current.Resources["EntryBackgroundColor"] = Color.FromHex("#111111");
            App.Current.Resources["SwitchBackgroundColor"] = Color.FromHex("#222222");
            App.Current.Resources["SwitchActiveColor"] = Color.FromHex("#915dfa");
            App.Current.Resources["SearchBarBackgroundColor"] = Color.FromHex("#222222");
            App.Current.Resources["SearchBarCancelButtonColor"] = Color.FromHex("#9955fa");
            App.Current.Resources["SearchBarPlaceholderTextColor"] = Color.FromHex("eeffff");
            App.Current.Resources["SearchBarTextColor"] = Color.FromHex("#ffffff");
        }
        private void ApplyLightTheme()
        {
            App.Current.Resources["NavigationPageColor"] = Color.FromHex("#eeffff");
            App.Current.Resources["BarTextColor"] = Color.FromHex("#000000");
            App.Current.Resources["ButtonColor"] = Color.FromHex("#9955fa");
            App.Current.Resources["ButtonTextColor"] = Color.FromHex("#000000");
            App.Current.Resources["ButtonBorderColor"] = Color.FromHex("#360485");
            App.Current.Resources["LabelTextColor"] = Color.FromHex("#000000");
            App.Current.Resources["LabelBackgroundColor"] = Color.FromHex("efffff");
            App.Current.Resources["EntryTextColor"] = Color.FromHex("#000000");
            App.Current.Resources["EntryPlaceholderTextColor"] = Color.FromHex("#231412");
            App.Current.Resources["EntryBackgroundColor"] = Color.FromHex("#efffff");
            App.Current.Resources["SwitchBackgroundColor"] = Color.FromHex("#eeffff");
            App.Current.Resources["SwitchActiveColor"] = Color.FromHex("#360485");
            App.Current.Resources["SearchBarBackgroundColor"] = Color.FromHex("#eeffff");
            App.Current.Resources["SearchBarCancelButtonColor"] = Color.FromHex("#360485");
            App.Current.Resources["SearchBarPlaceholderTextColor"] = Color.FromHex("#111111");
            App.Current.Resources["SearchBarTextColor"] = Color.FromHex("#000000");
        }
        #endregion
    }
}
