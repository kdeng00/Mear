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
    public partial class SettingsPage : ContentPage
    {
        #region Fields
        private SettingsViewModel _viewModel;
        #endregion


        #region Constructors
        public SettingsPage()
        {
            InitializeComponent();

            BindingContext = _viewModel = new SettingsViewModel();
        }
        #endregion


        #region Methods
        #region Events
        private void Switch_Toggled(object sender, ToggledEventArgs e)
        {
            bool? isDarkTheme = e.Value;
            _viewModel.ToggleTheme(isDarkTheme);
        }
        #endregion
        #endregion
    }
}