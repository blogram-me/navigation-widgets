package com.github.programmerr47.navigation.toolbar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Stack;

public class NavigationToolbar {

    public static View findOverflowView(Object instance) {
        ToolbarProxy toolbar = proxyOf(instance);
        Drawable overflowDrawable = toolbar.getOverflowIcon();
        if (overflowDrawable != null) {
            Stack<ViewGroup> parents = new Stack<>();
            parents.push((ViewGroup) toolbar.internalToolbar());

            while (!parents.empty()) {
                ViewGroup parent = (ViewGroup) parents.pop();
                int size = parent.getChildCount();

                for (int i = 0; i < size; ++i) {
                    View child = parent.getChildAt(i);
                    if (child instanceof ViewGroup) {
                        parents.push((ViewGroup) child);
                    } else if (child instanceof ImageView) {
                        Drawable childDrawable = ((ImageView) child).getDrawable();
                        if (childDrawable == overflowDrawable) {
                            return child;
                        }
                    }
                }
            }
        }

        try {
            Object actionMenuView = ReflectUtil.getPrivateField(toolbar.internalToolbar(), "mMenuView");
            Object actionMenuPresenter = ReflectUtil.getPrivateField(actionMenuView, "mPresenter");
            return (View) ReflectUtil.getPrivateField(actionMenuPresenter, "mOverflowButton");
        } catch (NoSuchFieldException var9) {
            throw new IllegalStateException("Could not find overflow view for Toolbar!", var9);
        } catch (IllegalAccessException var10) {
            throw new IllegalStateException("Unable to access overflow view for Toolbar!", var10);
        }
    }

    @SuppressLint("NewApi")
    private static ToolbarProxy proxyOf(Object instance) {
        if (instance == null) {
            throw new IllegalArgumentException("Given null instance");
        } else if (instance instanceof Toolbar) {
            return new SupportToolbarProxy((Toolbar) instance);
        } else if (instance instanceof android.widget.Toolbar) {
            return new StandardToolbarProxy((android.widget.Toolbar) instance);
        } else {
            throw new IllegalStateException("Couldn't provide proper toolbar proxy instance");
        }
    }

    @TargetApi(21)
    private static class StandardToolbarProxy implements ToolbarProxy {
        private final android.widget.Toolbar toolbar;

        StandardToolbarProxy(android.widget.Toolbar toolbar) {
            this.toolbar = toolbar;
        }

        public CharSequence getNavigationContentDescription() {
            return this.toolbar.getNavigationContentDescription();
        }

        public void setNavigationContentDescription(CharSequence description) {
            this.toolbar.setNavigationContentDescription(description);
        }

        public void findViewsWithText(ArrayList<View> out, CharSequence toFind, int flags) {
            this.toolbar.findViewsWithText(out, toFind, flags);
        }

        public Drawable getNavigationIcon() {
            return this.toolbar.getNavigationIcon();
        }

        @Nullable
        public Drawable getOverflowIcon() {
            return Build.VERSION.SDK_INT >= 23 ? this.toolbar.getOverflowIcon() : null;
        }

        public int getChildCount() {
            return this.toolbar.getChildCount();
        }

        public View getChildAt(int position) {
            return this.toolbar.getChildAt(position);
        }

        public Object internalToolbar() {
            return this.toolbar;
        }
    }

    private static class SupportToolbarProxy implements ToolbarProxy {
        private final Toolbar toolbar;

        SupportToolbarProxy(Toolbar toolbar) {
            this.toolbar = toolbar;
        }

        public CharSequence getNavigationContentDescription() {
            return this.toolbar.getNavigationContentDescription();
        }

        public void setNavigationContentDescription(CharSequence description) {
            this.toolbar.setNavigationContentDescription(description);
        }

        public void findViewsWithText(ArrayList<View> out, CharSequence toFind, int flags) {
            this.toolbar.findViewsWithText(out, toFind, flags);
        }

        public Drawable getNavigationIcon() {
            return this.toolbar.getNavigationIcon();
        }

        public Drawable getOverflowIcon() {
            return this.toolbar.getOverflowIcon();
        }

        public int getChildCount() {
            return this.toolbar.getChildCount();
        }

        public View getChildAt(int position) {
            return this.toolbar.getChildAt(position);
        }

        public Object internalToolbar() {
            return this.toolbar;
        }
    }

    private interface ToolbarProxy {
        CharSequence getNavigationContentDescription();

        void setNavigationContentDescription(CharSequence description);

        void findViewsWithText(ArrayList<View> out, CharSequence toFind, int flags);

        Drawable getNavigationIcon();

        @Nullable
        Drawable getOverflowIcon();

        int getChildCount();

        View getChildAt(int position);

        Object internalToolbar();
    }

}
