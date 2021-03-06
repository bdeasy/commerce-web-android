package com.us.masterpass.merchantapp.data.external;

import android.util.Log;
import com.us.masterpass.merchantapp.domain.model.MasterpassConfirmationObject;
import java.net.MalformedURLException;
import java.util.Map;

/**
 * {@link RestApi} implementation for retrieving data from the network.
 */
public class RestApi {

  private static final String TAG = RestApi.class.getSimpleName();

  /**
   * Gets items.
   *
   * @return the items
   */
  public String getItems() {
    String response = null;
    try {
      response = getItemsFromApi();
    } catch (MalformedURLException e) {
      Log.e(TAG, e.getMessage());
    }
    return response;
  }

  private String getItemsFromApi() throws MalformedURLException {
    return ApiConnection.createGET(MasterpassUrlConstants.URL_API_GET_ITEMS).requestSyncCall();
  }

  /**
   * Gets data confirmation.
   *
   * @param checkoutData the checkout data
   * @return the data confirmation
   */
  public String getDataConfirmation(Map<String, Object> checkoutData) {
    String response = null;
    try {
      response = getDataConfirmationFromApi(checkoutData);
    } catch (MalformedURLException e) {
      Log.e(TAG, e.getMessage());
    }
    return response;
  }

  private String getDataConfirmationFromApi(Map<String, Object> checkoutData)
      throws MalformedURLException {

    return ApiConnection.createPOST(MasterpassUrlConstants.URL_API_POST_CONFIRMATION, checkoutData)
        .requestSyncCallWithBody();
  }

  /**
   * Gets pairing id.
   *
   * @param checkoutData the checkout data
   * @return the pairing id
   */
  public String getPairingId(Map<String, Object> checkoutData) {
    String response = null;
    try {
      response = getPairingIdFromApi(checkoutData);
    } catch (MalformedURLException e) {
      Log.e(TAG, e.getMessage());
    }
    return response;
  }

  private String getPairingIdFromApi(Map<String, Object> checkoutData)
      throws MalformedURLException {
    return ApiConnection.createPOST(MasterpassUrlConstants.URL_API_POST_PAIRING, checkoutData)
        .requestSyncCallWithBody();
  }

  /**
   * Sets complete transaction.
   *
   * @param masterpassConfirmationObject the masterpass confirmation object
   * @return the complete transaction
   */
  public String setCompleteTransaction(MasterpassConfirmationObject masterpassConfirmationObject) {
    String response = null;
    try {
      response = getCompleteTransactionFromApi(masterpassConfirmationObject);
    } catch (MalformedURLException e) {
      Log.e(TAG, e.getMessage());
    }
    return response;
  }

  private String getCompleteTransactionFromApi(
      MasterpassConfirmationObject masterpassConfirmationObject) throws MalformedURLException {
    return ApiConnection.createPOST(MasterpassUrlConstants.URL_API_POST_COMPLETE,
        masterpassConfirmationObject).requestSyncCallWithBody();
  }
}
