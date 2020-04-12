package dev.olog.feature.service.floating.api;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static android.view.View.GONE;

/**
 * The visual area occupied by a {@link HoverView}. A {@code Screen} acts as a factory for the
 * visual elements used within a {@code HoverView}.
 */
class Screen {

    private static final String TAG = "Screen";

    private ViewGroup mContainer;
    private ContentDisplay mContentDisplay;
    private ExitView mExitView;
    private ShadeView mShadeView;
    @NonNull
    private Map<String, FloatingTab> mTabs = new HashMap<>();
    private boolean mIsDebugMode = false;

    Screen(@NonNull ViewGroup hoverMenuContainer) {
        mContainer = hoverMenuContainer;

        mShadeView = new ShadeView(mContainer.getContext());
        mContainer.addView(mShadeView, new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        mShadeView.hideImmediate();

        mExitView = new ExitView(mContainer.getContext());
        mContainer.addView(mExitView, new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        mExitView.setVisibility(GONE);

        mContentDisplay = new ContentDisplay(mContainer.getContext());
        mContainer.addView(mContentDisplay);
        mContentDisplay.setVisibility(GONE);
    }

    public void enableDrugMode(boolean debugMode) {
        mIsDebugMode = debugMode;

        mContentDisplay.enableDebugMode(debugMode);
        for (FloatingTab tab : mTabs.values()) {
            tab.enableDebugMode(debugMode);
        }
    }

    public int getWidth() {
        return mContainer.getWidth();
    }

    public int getHeight() {
        return mContainer.getHeight();
    }

    @NonNull
    public FloatingTab createChainedTab(@NonNull HoverMenu.SectionId sectionId, @NonNull View tabView) {
        String tabId = sectionId.toString();
        return createChainedTab(tabId, tabView);
    }

    @NonNull
    public FloatingTab createChainedTab(@NonNull String tabId, @NonNull View tabView) {
        Timber.d(TAG + "Existing tabs...");
        for (String existingTabId : mTabs.keySet()) {
            Timber.d(TAG + existingTabId);
        }
        if (mTabs.containsKey(tabId)) {
            return mTabs.get(tabId);
        } else {
            Timber.d(TAG + "Creating new tab with ID: " + tabId);
            FloatingTab chainedTab = new FloatingTab(mContainer.getContext(), tabId);
            chainedTab.setTabView(tabView);
            chainedTab.enableDebugMode(mIsDebugMode);
            mContainer.addView(chainedTab);
            mTabs.put(tabId, chainedTab);
            return chainedTab;
        }
    }

    @Nullable
    public FloatingTab getChainedTab(@Nullable HoverMenu.SectionId sectionId) {
        String tabId = null != sectionId ? sectionId.toString() : null;
        return getChainedTab(tabId);
    }

    @Nullable
    public FloatingTab getChainedTab(@Nullable String tabId) {
        return mTabs.get(tabId);
    }

    public void destroyChainedTab(@NonNull FloatingTab chainedTab) {
        mTabs.remove(chainedTab.getTabId());
        chainedTab.setTabView(null);
        mContainer.removeView(chainedTab);
    }

    public ContentDisplay getContentDisplay() {
        return mContentDisplay;
    }

    public ExitView getExitView() {
        return mExitView;
    }

    public ShadeView getShadeView() {
        return mShadeView;
    }
}
