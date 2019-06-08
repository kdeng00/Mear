﻿using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;

using Xamarin.Forms;

namespace Mear.ViewModels
{
    public class SettingsViewModel : BaseViewModel
    {
        #region Fields
        private ObservableCollection<SwitchItem> _switchItems;
        #endregion


        #region Properties
        public ObservableCollection<SwitchItem> SwitchItems
        {
            get => _switchItems;
            set => _switchItems = value;
        }
        #endregion


        #region Constructors
        public SettingsViewModel()
        {
            _switchItems = new ObservableCollection<SwitchItem>();

            PopulateSwitch();
        }
        #endregion


        #region Methods
        private SwitchItem RetrieveColorSchemeSwitch()
        {
            var colorScheme = new SwitchItem
            {
                Title = "Dark Theme",
                IsOn = true
            };

            return colorScheme;
        }
        private void PopulateSwitch()
        {
            var colorScheme = RetrieveColorSchemeSwitch();
            _switchItems.Add(colorScheme);
        }
        #endregion


        #region Classes
        public class SwitchItem
        {
            public string Title { get; set; }
            public bool IsOn { get; set; }
        }
        #endregion
    }
}
