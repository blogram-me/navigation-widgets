package com.github.programmerr47.navigation;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.menu.ListMenuItemView;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.MenuPopupWindow;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation.OnTabSelectedListener;
import com.github.programmerr47.navigation.NavigationIcons.NavigationIcon;
import com.github.programmerr47.navigation.layoutfactory.DummyLayoutFactory;
import com.github.programmerr47.navigation.menu.MenuActions;

import java.lang.reflect.Field;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.aurelhubert.ahbottomnavigation.AHBottomNavigation.TitleState.ALWAYS_SHOW;
import static com.github.programmerr47.navigation.AndroidUtils.bind;
import static com.github.programmerr47.navigation.NavigationBuilder.NO_NAV_ICON;

public abstract class NavigationFragment extends Fragment implements OnTabSelectedListener {
    private NavigationBuilder<?> navigationBuilder;

    protected Toolbar toolbar;
    protected AHBottomNavigation bottomNavigation;
    protected RtlToolbarHandler rtlToolbarHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        navigationBuilder = buildNavigation();
        return navigationBuilder.layoutFactory().produceLayout(inflater, container);
    }

    @Override
    @CallSuper
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        if (view != null && navigationBuilder != null) {
            toolbar = bind(view, navigationBuilder.toolbarId);
            bottomNavigation = bind(view, navigationBuilder.bottomBarId);
        } else {
            toolbar = null;
            bottomNavigation = null;
        }

        prepareNavigation();
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        super.onDestroyView();
        navigationBuilder = null;
        toolbar = null;
        bottomNavigation = null;

        if (rtlToolbarHandler != null) {
            rtlToolbarHandler.stop();
            rtlToolbarHandler = null;
        }
    }

    protected void invalidateNavigation(NavigationBuilder newNavigation) {
        navigationBuilder = newNavigation;
        prepareNavigation();
    }

    private void prepareNavigation() {
        if (toolbar != null) {
            prepareToolbar(toolbar);
        }

        if (bottomNavigation != null) {
            prepareBottomNavigation(bottomNavigation);
        }
    }

    @SuppressLint("RestrictedApi")
    protected void prepareToolbar(final Toolbar toolbar) {
        if (navigationBuilder.toolbarTitleRes != 0) {
            toolbar.setTitle(navigationBuilder.toolbarTitleRes);
        } else {
            toolbar.setTitle(navigationBuilder.toolbarTitle);
        }
        if (navigationBuilder.toolbarSubtitleRes != 0) {
            toolbar.setSubtitle(navigationBuilder.toolbarSubtitleRes);
        } else {
            toolbar.setSubtitle(navigationBuilder.toolbarSubtitle);
        }
        if (navigationBuilder.toolbarLogoRes != 0) {
            toolbar.setLogo(navigationBuilder.toolbarLogoRes);
        } else {
            toolbar.setLogo(navigationBuilder.toolbarLogo);
        }
        if (navigationBuilder.toolbarNavigationIcon == NO_NAV_ICON) {
            toolbar.setNavigationIcon(null);
            toolbar.setNavigationOnClickListener(null);
        } else {
            NavigationIcon navIcon = navigationBuilder.navigationDefaults().navigationIcons().fromType(navigationBuilder.toolbarNavigationIcon);
            toolbar.setNavigationIcon(navIcon.iconDrawable(toolbar.getContext()));
            toolbar.setNavigationOnClickListener(navigationBuilder.navigationDefaults().navigationIconListener());
        }

        rtlToolbarHandler = RtlToolbarHandler.with(toolbar).start();

        Menu menu = toolbar.getMenu();
        if (menu != null) {
            menu.clear();
        }
        if (!navigationBuilder.menuRes.isEmpty()) {
            final MenuActions actions = navigationBuilder.menuActions.build();
            for (Integer menuRes : navigationBuilder.menuRes) {
                toolbar.inflateMenu(menuRes);
            }

            if (menu instanceof MenuBuilder) {
                ((MenuBuilder) menu).setOptionalIconsVisible(true);
            }

            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return actions.onMenuItemClick(item);
                }
            });
        }
    }

    protected void prepareBottomNavigation(AHBottomNavigation bottomNavigation) {
        NavigationItems navigationItems = navigationBuilder.navigationDefaults().navigationItems();
        bottomNavigation.removeAllItems();
        bottomNavigation.addItems(navigationItems.bottomNavigationItems());
        bottomNavigation.setCurrentItem(navigationItems.indexFromType(navigationBuilder.currentBottomBarItem), false);
        bottomNavigation.setOnTabSelectedListener(this);
        bottomNavigation.setTitleState(ALWAYS_SHOW);
        bottomNavigation.setColored(true);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation result = new Animation() {
        };
        result.setDuration(0);
        return result;
    }

    @Override
    public final boolean onTabSelected(int position, boolean wasSelected) {
        int itemType = navigationBuilder.navigationDefaults().navigationItems().get(position).type();
        return onTabTypeSelected(itemType, wasSelected);
    }

    public boolean onTabTypeSelected(int type, boolean wasSelected) {
        return true;
    }

    public void showBottomNavigation() {
        if (bottomNavigation != null) {
            bottomNavigation.setVisibility(VISIBLE);
        }
    }

    public void hideBottomNavigation() {
        if (bottomNavigation != null) {
            bottomNavigation.setVisibility(GONE);
        }
    }

    protected NavigationBuilder buildNavigation() {
        return new CustomLayoutNavigationBuilder(new DummyLayoutFactory(null));
    }
}
