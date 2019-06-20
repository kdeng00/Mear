using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

using Mear.Managers;
using Mear.Models.Authentication;
using Mear.Repositories.Database;
using Mear.Repositories.Mock;

namespace Mear.Views
{
	[XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class LoginPage : ContentPage
	{
		#region Fields
		#endregion


		#region Properties
		#endregion


		#region Constructors
		public LoginPage()
		{
			InitializeComponent();
			DummyUser();
		}
		#endregion

		#region Methods
		private User ExtractCredentials()
		{
			try
			{
				var username = Username.Text;
				var password = Password.Text;

				return new User
				{
					Username = username,
					Password = password
				};
			}
			catch (Exception ex)
			{
				var msg = ex.Message;
			}
			return null;
		}

		#region Events
		private async void AuthenticateUser_Clicked(object sender, EventArgs e)
		{
			AuthenticateUser.IsEnabled = false;

			var user = ExtractCredentials();

			if (user != null)
			{
				var loginMgr = new LoginManager(user);

				if (loginMgr.Authenticate())
				{
					App.Current.MainPage = new MusicLibrary();
				}
				else
				{
					await DisplayAlert("Icarus Login", "Unable to login", "Ok");
				}
			}

			AuthenticateUser.IsEnabled = true;
		}
		#endregion

		#region Test
		private void DummyUser()
		{
			var mockUserRepo = new MockUserRepository();
			var mockUser = mockUserRepo.Users[0];

			Username.Text = mockUser.Username;
			Password.Text = mockUser.Password;
		}
		#endregion
		#endregion
	}
}