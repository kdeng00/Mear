using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Rg.Plugins.Popup.Extensions;
using Rg.Plugins.Popup.Services;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

using Mear.Views.Popups;
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
            var switchItem = (Switch)sender;
            bool? isDarkTheme = e.Value;
            _viewModel.ToggleTheme(isDarkTheme);

        }
        private async void SelectionOptions_ItemSelected(object sender, SelectedItemChangedEventArgs e)
        {
            var item = ((sender as ListView).SelectedItem) as SettingsViewModel.SelectionItem;
            try
            {
                if (item.Title.Equals("About"))
                {
                    await PopupNavigation.Instance.PushAsync(new AboutPopup(), false);
                }
            }
            catch (Exception ex)
            {
                var msg = ex.Message;
            }

            (sender as ListView).SelectedItem = null;
        }
        #endregion

        #endregion

        private void Slider_ValueChanged(object sender, ValueChangedEventArgs e)
        {
            var value = Convert.ToInt32((sender as Slider).Value);
            var resLbl = ((sender as Slider).Parent as Grid).Children[1] as Label;
            _viewModel.SliderItems.First().DefaultInterval = Convert.ToInt32(value);
            resLbl.Text = $"{value}";
        }
    }
}