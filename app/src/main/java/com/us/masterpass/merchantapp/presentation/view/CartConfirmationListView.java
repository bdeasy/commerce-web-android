package com.us.masterpass.merchantapp.presentation.view;

import com.us.masterpass.merchantapp.domain.model.Item;
import com.us.masterpass.merchantapp.domain.model.MasterpassConfirmationObject;
import com.us.masterpass.merchantapp.presentation.presenter.CartConfirmationPresenter;

import java.util.List;

/**
 * Created by Sebastian Farias on 10-10-17.
 */
public interface CartConfirmationListView extends View<CartConfirmationPresenter> {

    /**
     * Show items.
     *
     * @param itemsOnCart the items on cart
     */
    void showItems(List<Item> itemsOnCart);

    /**
     * Total price.
     *
     * @param totalPrice the total price
     */
    void totalPrice(String totalPrice);

    /**
     * Subtotal price.
     *
     * @param subtotalPrice the subtotal price
     */
    void subtotalPrice(String subtotalPrice);

    /**
     * Tax price.
     *
     * @param taxPrice the tax price
     */
    void taxPrice(String taxPrice);

    /**
     * Show loading spinner.
     *
     * @param show the show
     */
    void showLoadingSpinner(boolean show);

    /**
     * Show complete screen.
     *
     * @param masterpassConfirmationObject the masterpass confirmation object
     */
    void showCompleteScreen(MasterpassConfirmationObject masterpassConfirmationObject);

    /**
     * Is suppress shipping.
     *
     * @param suppressShipping the suppress shipping
     */
    void isSuppressShipping(boolean suppressShipping);
}
