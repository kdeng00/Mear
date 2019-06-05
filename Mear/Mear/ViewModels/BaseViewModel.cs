using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Text;

using Xamarin.Forms;

namespace Mear.ViewModels
{
	public class BaseViewModel : INotifyPropertyChanged
	{
		#region Fields
		#region INotifyPropertyChanged
		public event PropertyChangedEventHandler PropertyChanged;
        #endregion
        private bool _isRefreshing;
        #endregion


        #region Properties
        public bool IsRefreshing
        {
            get => _isRefreshing;
            set
            {
                _isRefreshing = value;
                NotifyPropertyChanged("IsRefreshing");
            }
        }
        #endregion


        #region Methods
        private void NotifyPropertyChanged(string propertyName)
        {
            if (PropertyChanged != null)
            {
                PropertyChanged(this, new PropertyChangedEventArgs(propertyName));
            }
        }
        #endregion
    }
}
