using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;

using Xamarin.Forms;

using Mear.Managers;
using Mear.Repositories.Database;

namespace Mear.ViewModels
{
    public class SettingsViewModel : BaseViewModel
    {
        #region Fields
        private ObservableCollection<SwitchItem> _switchItems;
        private ObservableCollection<SelectionItem> _selectionItems;
        #endregion


        #region Properties
        public ObservableCollection<SwitchItem> SwitchItems
        {
            get => _switchItems;
            set => _switchItems = value;
        }
        public ObservableCollection<SelectionItem> SelectionItems
        {
            get => _selectionItems;
            set => _selectionItems = value;
        }
        #endregion


        #region Constructors
        public SettingsViewModel()
        {
            _switchItems = new ObservableCollection<SwitchItem>();
            _selectionItems = new ObservableCollection<SelectionItem>();

            PopulateSwitch();
            PopulateSelections();
        }
        #endregion


        #region Methods
        public void ToggleTheme(bool? isDarkTheme = null)
        {
            var themeMgr = new ThemeManager();
            themeMgr.ChangeTheme(isDarkTheme);
        }
        private SwitchItem RetrieveColorSchemeSwitch()
        {
            var settingsRepo = new DBSettingsRepository();
            var darkTheme = settingsRepo.IsDarkThemeOn();

            var colorScheme = new SwitchItem
            {
                Title = "Dark Theme",
                IsOn = darkTheme
            };

            return colorScheme;
        }

        private void PopulateSwitch()
        {
            var colorScheme = RetrieveColorSchemeSwitch();
            _switchItems.Add(colorScheme);
        }
        private void PopulateSelections()
        {
            // TODO: When more selection options are added, will need to
            // come up with a better way but for now, this will suffice.
            _selectionItems.Clear();
            _selectionItems.Add(new SelectionItem
            {
                Title = "About",
                Description = "Information about the software"
            });
        }
        #endregion


        #region Classes
        public class SwitchItem
        {
            public string Title { get; set; }
            public bool IsOn { get; set; }
        }
        public class SelectionItem
        {
            public string Title { get; set; }
            public string Description { get; set; }
        }
        #endregion
    }
}
