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
		#endregion
	}
}
