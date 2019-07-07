-keepclassmembers class android.support.v7.widget.Toolbar {
    private *** mMenuView;
 }

-keepclassmembers class android.support.v7.widget.ActionMenuView {
     private *** mPresenter;
 }

-keepclassmembers class android.support.v7.widget.ActionMenuPresenter {
     *** mActionButtonPopup;
     *** mOverflowPopup;
}

-keepclassmembers class android.support.v7.view.menu.MenuPopupHelper {
    private *** mPopup;
}

-keepclassmembers class android.support.v7.view.menu.StandardMenuPopup {
    *** mPopup;
}
