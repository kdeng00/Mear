﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

using Mear.Models.Authentication;
using Mear.Repositories.Database;

namespace Mear.Views
{
	[XamlCompilation(XamlCompilationOptions.Compile)]
	public partial class MusicLibrary : TabbedPage
	{
		#region Fields
		#endregion


		#region Properties
		#endregion


		#region Constructors
		public MusicLibrary()
		{
			InitializeComponent();
		}
		#endregion


		#region Methods
		#region Events
		private void Options_Clicked(object sender, EventArgs e)
		{
			var i = 0;
		}
		private void Settings_Clicked(object sender, EventArgs e)
		{
		}
		#endregion

		#region Test
		public void TokenTest()
		{
			DBTokenRepository tokRepo = new DBTokenRepository();
			var token = tokRepo.RetrieveToken();
		}
		#endregion
		#endregion
	}
}