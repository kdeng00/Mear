using System;
using System.Collections.Generic;
using System.Text;

using Mear.Models.Authentication;

namespace Mear.Repositories.Mock
{
	public class MockUserRepository
	{
		#region Fields
		private List<User> _users;
		#endregion


		#region Methods
		public List<User> Users
		{
			get => _users;
		}
		#endregion


		#region Constructors
		public MockUserRepository()
		{
			InitializeMockUser();
		}
		#endregion


		#region Methods
		private void InitializeMockUser()
		{
			_users = new List<User>();

			var firstname = "";
			var lastname = "";
			var email = "";
			var phone = "";
			var username = "";
			var password = "";

			_users.Add(new User
			{
				Firstname = firstname,
				Lastname = lastname,
				Email = email,
				PhoneNumber = phone,
				Username = username,
				Password = password
			});
		}
		#endregion
	}
}
