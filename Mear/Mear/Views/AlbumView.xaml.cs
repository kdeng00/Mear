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
	public partial class AlbumView : ContentPage
	{
		#region Fields
		private AlbumViewModel _viewModel;
		#endregion


		#region Properties
		#endregion


		#region Constructors
		public AlbumView()
		{
			InitializeComponent();

			BindingContext = _viewModel = new AlbumViewModel();
		}
		#endregion


		#region Methods
		#region Events
		private void AlbumListView_ItemSelected(object sender, SelectedItemChangedEventArgs e)
		{

		}
		#endregion
		#endregion
	}
}