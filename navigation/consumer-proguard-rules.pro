-keepclassmembers class androidx.appcompat.widget.Toolbar {
    private *** mMenuView;
 }

-keepclassmembers class androidx.appcompat.widget.ActionMenuView {
     private *** mPresenter;
 }

-keepclassmembers class androidx.appcompat.widget.ActionMenuPresenter {
     *** mActionButtonPopup;
     *** mOverflowPopup;
}

-keepclassmembers class androidx.appcompat.view.menu.MenuPopupHelper {
    private *** mPopup;
}

-keepclassmembers class androidx.appcompat.view.menu.StandardMenuPopup {
    *** mPopup;
}
