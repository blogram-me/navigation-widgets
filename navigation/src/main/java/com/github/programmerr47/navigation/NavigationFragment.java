package com.github.programmerr47.navigation;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation.OnTabSelectedListener;
import com.github.programmerr47.navigation.NavigationIcons.NavigationIcon;
import com.github.programmerr47.navigation.layoutfactory.DummyLayoutFactory;
import com.github.programmerr47.navigation.menu.MenuActions;
import com.github.programmerr47.navigation.toolbar.NavigationToolbar;
import com.hadi.menu.overflow.OverFlowMenu;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.aurelhubert.ahbottomnavigation.AHBottomNavigation.TitleState.ALWAYS_SHOW;
import static com.github.programmerr47.navigation.AndroidUtils.bind;
import static com.github.programmerr47.navigation.NavigationBuilder.NO_NAV_ICON;

public abstract class NavigationFragment extends Fragment implements OnTabSelectedListener {
    private NavigationBuilder<?> navigationBuilder;

    protected Toolbar toolbar;
    protected AHBottomNavigation bottomNavigation;

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

        Menu menu = toolbar.getMenu();
        if (menu != null) {
            menu.clear();
        }

        if (navigationBuilder.menuRes != -1) {
            final MenuActions actions = navigationBuilder.menuActions.build();
            toolbar.inflateMenu(navigationBuilder.menuRes);

            try {
                View overflowView = NavigationToolbar.findOverflowView(toolbar);
                overflowView.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                OverFlowMenu.createDefaultMenu(toolbar.getContext(), navigationBuilder.menuRes)
                                        .setOnMenuItemClickListener(new OverFlowMenu.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(int i) {
                                                actions.onItemClick(i);
                                            }
                                        })
                                        .show(v);
                            }
                        }
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private MenuInflater getMenuInflater() {
        return new SupportMenuInflater(toolbar.getContext());
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
