using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

using Mear.Models;
using Mear.Managers;
using Mear.Models.Authentication;
using Mear.Repositories.Mock;

namespace Mear.Views
{
	[XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class RegisterPage : ContentPage
	{
		#region Fields
		#endregion


		#region Properties
		#endregion


		#region Constructors
		public RegisterPage()
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
				var firstname = Firstname.Text.ToString();
				var lastname = Lastname.Text.ToString();
				var emailAddress = Email.Text.ToString();
				var phoneNumber = Phone.Text.ToString();
				var username = Username.Text.ToString();
				var password = Password.Text.ToString();
				var passwordConfirm = PasswordConfirm.Text.ToString();

				if (!password.Equals(passwordConfirm))
				{
					return null;
				}

				return new User
				{
					Firstname = firstname,
					Lastname = lastname,
					Email = emailAddress,
					PhoneNumber = phoneNumber,
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
		private async void RegisterUser_Clicked(object sender, EventArgs e)
		{
			RegisterUser.IsEnabled = false;

			var user = ExtractCredentials();

			if (user != null)
			{
				var regMgr = new RegisterManager(user);
				var regRes = regMgr.RegisterUser();

				if (regRes.SuccessfullyRegistered)
				{
					// TODO: Implement functionality when registration succeeds
					await DisplayAlert("Icarus Account Creation", "Successfully created Icarus user", "Ok");
					await Navigation.PopModalAsync(true);
				}
				else
				{
					await DisplayAlert("Icarus Account Creation", "Icarus user was not created", "Ok");
					// TODO: Implement functionality when registration fails
				}
			}

			RegisterUser.IsEnabled = true;
		}
		#endregion

		#region Test
		private void DummyUser()
		{
			var mockUserRepo = new MockUserRepository();
			var mockUser = mockUserRepo.Users[0];

			Firstname.Text = mockUser.Firstname;
			Lastname.Text = mockUser.Lastname;
			Email.Text = mockUser.Email;
			Phone.Text = mockUser.PhoneNumber;
			Username.Text = mockUser.Username;
			Password.Text = mockUser.Password;
			PasswordConfirm.Text = mockUser.Password;
		}
		#endregion
		#endregion
	}
}