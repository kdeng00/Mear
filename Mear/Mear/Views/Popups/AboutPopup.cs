using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

using Rg.Plugins.Popup;
using Rg.Plugins.Popup.Pages;
using Xamarin.Forms;

using Mear.ViewModels.Popups;

namespace Mear.Views.Popups
{
    public partial class AboutPopup : Rg.Plugins.Popup.Pages.PopupPage
    {
        #region Fields
        private AboutViewModel _viewModel;
        #endregion


        #region Constructors
        public AboutPopup()
        {
            InitializeComponent();

            BindingContext = _viewModel = new AboutViewModel();
        }
        #endregion

        #region Methods
        #region PopUpInit
        protected override void OnAppearing()
        {
            base.OnAppearing();
        }

        protected override void OnDisappearing()
        {
            base.OnDisappearing();
        }

        protected override void OnAppearingAnimationBegin()
        {
            base.OnAppearingAnimationBegin();
        }

        protected override void OnAppearingAnimationEnd()
        {
            base.OnAppearingAnimationEnd();
        }

        protected override void OnDisappearingAnimationBegin()
        {
            base.OnDisappearingAnimationBegin();
        }

        protected override void OnDisappearingAnimationEnd()
        {
            base.OnDisappearingAnimationEnd();
        }

        protected override Task OnAppearingAnimationBeginAsync()
        {
            return base.OnAppearingAnimationBeginAsync();
        }

        protected override Task OnAppearingAnimationEndAsync()
        {
            return base.OnAppearingAnimationEndAsync();
        }

        protected override Task OnDisappearingAnimationBeginAsync()
        {
            return base.OnDisappearingAnimationBeginAsync();
        }

        protected override Task OnDisappearingAnimationEndAsync()
        {
            return base.OnDisappearingAnimationEndAsync();
        }


        protected override bool OnBackButtonPressed()
        {
            return base.OnBackButtonPressed();
        }

        protected override bool OnBackgroundClicked()
        {
            return base.OnBackgroundClicked();
        }
        #endregion
        #endregion
    }
}
