using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

using Mear.ViewModels;

namespace Mear.Views
{
	[XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class ArtistView : ContentPage
	{
		#region Fields
		private ArtistViewModel _viewModel;
		#endregion


		#region Properties
		#endregion


		#region Constructors
		public ArtistView()
		{
			InitializeComponent();

			BindingContext = _viewModel = new ArtistViewModel();
		}
		#endregion


		#region Methods
		#region Events
		private void ArtistListView_ItemSelected(object sender, SelectedItemChangedEventArgs e)
		{
            var artistItem = (sender as ListView).SelectedItem = null;
		}
		#endregion
		#endregion
	}
}