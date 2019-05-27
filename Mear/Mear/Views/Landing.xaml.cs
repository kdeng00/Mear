using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace Mear.Views
{
	[XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class Landing : ContentPage
	{
		public Landing()
		{
			InitializeComponent();
		}

		private void Login_Clicked(object sender, EventArgs e)
		{
			Navigation.PushModalAsync(new LoginPage());
		}

		private void Register_Clicked(object sender, EventArgs e)
		{
			Navigation.PushModalAsync(new RegisterPage());
		}
	}
}