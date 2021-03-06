package com.us.masterpass.merchantapp.domain.usecase.paymentMethod;

import com.mastercard.mp.checkout.MasterpassError;
import com.us.masterpass.merchantapp.data.device.MerchantPaymentMethod;
import com.us.masterpass.merchantapp.domain.masterpass.MasterpassSdkCoordinator;
import com.us.masterpass.merchantapp.domain.masterpass.MasterpassSdkInterface;
import com.us.masterpass.merchantapp.domain.usecase.base.UseCase;
import java.util.List;

/**
 * UseCase for AddPaymentMethod
 */

public class AddPaymentMethodUseCase
    extends UseCase<AddPaymentMethodUseCase.RequestValues, AddPaymentMethodUseCase.ResponseValue> {
  public AddPaymentMethodUseCase() {
  }

  @Override protected void executeUseCase(RequestValues requestValues) {

    MasterpassSdkCoordinator.getInstance()
        .addPaymentMethod(new MasterpassSdkInterface.GetMasterpassPaymentMethod() {
          @Override public void sdkResponseSuccess(List<MerchantPaymentMethod> paymentMethodList) {
            ResponseValue responseValue = new ResponseValue(paymentMethodList);
            getUseCaseCallback().onSuccess(responseValue);
          }

          @Override public void sdkResponseError(MasterpassError masterpassError) {
            getUseCaseCallback().onError();
          }
        });
  }

  public static final class RequestValues implements UseCase.RequestValues {

  }

  public static final class ResponseValue implements UseCase.ResponseValue {
    private final List<MerchantPaymentMethod> paymentMethodList;

    public ResponseValue(List<MerchantPaymentMethod> merchantPaymentMethodList) {
      this.paymentMethodList = merchantPaymentMethodList;
    }

    public List<MerchantPaymentMethod> getPaymentMethodList() {
      return paymentMethodList;
    }
  }
}
