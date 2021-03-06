package com.mastercard.testapp.presentation.view;

import com.mastercard.testapp.domain.model.Item;
import com.mastercard.testapp.presentation.presenter.ItemsPresenter;
import java.util.List;

/**
 * Created by Sebastian Farias on 08-10-17.
 */
public interface ItemsListView extends View<ItemsPresenter> {
  /**
   * Sets loading indicator.
   *
   * @param active the active
   */
  void setLoadingIndicator(boolean active);

  /**
   * Show items.
   *
   * @param items the items
   */
  void showItems(List<Item> items);

  /**
   * Show loading items error.
   */
  void showLoadingItemsError();

  /**
   * Update badge.
   *
   * @param totalCartCount the total cart count
   */
  void updateBadge(String totalCartCount);

  /**
   * Load cart activity.
   */
  void loadCartActivity();

  /**
   * Load cart activity show error.
   */
  void loadCartActivityShowError();

  /**
   * Load settings activity.
   */
  void loadSettingsActivity();

  /**
   * Load login activity.
   */
  void loadLoginActivity();

  /**
   * Load myaccount activity.
   */
  void loadMyAccountActivity();

  /**
   * Show alert is logged.
   */
  void showAlertIsLogged();
}
