package com.mastercard.testapp.presentation.presenter.base;

import com.mastercard.mp.switchservices.checkout.ExpressCheckoutRequest;
import com.mastercard.testapp.domain.model.Item;
import com.mastercard.testapp.domain.model.MasterpassConfirmationObject;
import java.util.List;

/**
 * Created by Sebastian Farias on 10-10-17.
 */
public interface CartConfirmationPresenterInterface extends Presenter {

  /**
   * Load items on cart.
   *
   * @param forceUpdate the force update
   */
  void loadItemsOnCart(boolean forceUpdate);

  /**
   * Show items on cart.
   *
   * @param itemsOnCart the items on cart
   */
  void showItemsOnCart(List<Item> itemsOnCart);

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
   * Confirm checkout.
   *
   * @param masterpassConfirmationObject the masterpass confirmation object
   */
  void confirmCheckout(MasterpassConfirmationObject masterpassConfirmationObject);

  /**
   * Is suppress shipping.
   *
   * @param suppressShipping the suppress shipping
   */
  void isSuppressShipping(boolean suppressShipping);

  /**
   * Express checkout.
   *
   * @param masterpassExpressCheckoutObject the masterpass confirmation object
   */
  void expressCheckout(ExpressCheckoutRequest masterpassExpressCheckoutObject);
}
