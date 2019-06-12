using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;

using Mear.ViewModels;

namespace Mear.ViewModels.Popups
{
    public class AboutViewModel : BaseViewModel
    {
        #region Fields
        private ObservableCollection<ContributorItem> _contributorItems;
        #endregion


        #region Properties
        public ObservableCollection<ContributorItem> ContributorItems
        {
            get => _contributorItems;
            set => _contributorItems = value;
        }
        #endregion


        #region Constructors
        public AboutViewModel()
        {
            _contributorItems = new ObservableCollection<ContributorItem>();

            PopulateContributors();
        }
        #endregion


        #region Methods
        private void PopulateContributors()
        {
            _contributorItems.Clear();
            _contributorItems.Add(new ContributorItem
            {
                Contributor = "amazing-username",
                Role = "Author"
            });
        }
        #endregion


        #region Classes
        public class ContributorItem
        {
            public string Contributor { get; set; }
            public string Role { get; set; }
        }
        #endregion
    }
}
