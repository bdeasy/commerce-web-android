package com.us.masterpass.merchantapp.presentation.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.us.masterpass.merchantapp.R;
import com.us.masterpass.merchantapp.data.device.CartLocalStorage;
import com.us.masterpass.merchantapp.domain.model.Item;
import com.us.masterpass.merchantapp.domain.model.MasterpassConfirmationObject;
import com.us.masterpass.merchantapp.presentation.PresentationConstants;
import com.us.masterpass.merchantapp.presentation.adapter.CartConfirmationAdapter;
import com.us.masterpass.merchantapp.presentation.presenter.CartCompletePresenter;
import com.us.masterpass.merchantapp.presentation.view.CartCompleteListView;
import java.util.ArrayList;
import java.util.List;

import static com.us.masterpass.merchantapp.domain.Utils.checkNotNull;

/**
 * Created by Sebastian Farias on 10-10-17.
 */
public class CartCompleteFragment extends Fragment implements CartCompleteListView {

    private CartConfirmationAdapter mAdapter;
    private CartCompletePresenter mPresenter;
    private TextView totalPrice;
    private TextView subtotalPrice;
    private TextView taxPrice;
    private LinearLayout llShippingOptions;

    /**
     * Instantiates a new Cart complete fragment.
     */
    public CartCompleteFragment() {
    //public constructor
    }

    /**
     * New instance cart complete fragment.
     *
     * @return the cart complete fragment
     */
    public static CartCompleteFragment newInstance() {
        return new CartCompleteFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new CartConfirmationAdapter(new ArrayList<Item>(0));
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart_complete_list_view, container, false);

        Bundle bundle = this.getArguments();
        MasterpassConfirmationObject masterpassConfirmationObject =
                (MasterpassConfirmationObject) bundle.getSerializable(PresentationConstants.KEY_DATA_CONFIRMATION);

        totalPrice = view.findViewById(R.id.cart_total_text);
        subtotalPrice = view.findViewById(R.id.cart_subtotal_text);
        taxPrice = view.findViewById(R.id.cart_tax_text);

        ListView listView = view.findViewById(R.id.items_list_view);
        listView.setAdapter(mAdapter);

        TextView cardName = view.findViewById(R.id.cart_confimation_card_name);
        TextView cardNumber = view.findViewById(R.id.cart_confimation_card_number);
        TextView shippingAddress = view.findViewById(R.id.cart_confimation_address);
        TextView confirmationOrder = view.findViewById(R.id.cart_confirmation_order);
        cardName.setText(masterpassConfirmationObject.getCardBrandName());
        cardNumber.setText(masterpassConfirmationObject.getCardAccountNumberHidden());
        shippingAddress.setText(masterpassConfirmationObject.getShippingLine1());
        confirmationOrder.setText(masterpassConfirmationObject.getCartId());
        llShippingOptions = view.findViewById(R.id.ll_ship_content);

        LinearLayout llBack = view.findViewById(R.id.cart_complete_btn);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO REMOVE ONLY TEST
                CartLocalStorage.getInstance(getContext()).removeModel();
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void showItems(List<Item> items) {
        mAdapter.replaceData(items);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    //onActivityResult
    }

    @Override
    public void totalPrice(String totalPrice) {
        this.totalPrice.setText(totalPrice);
    }

    @Override
    public void subtotalPrice(String subtotalPrice) {
        this.subtotalPrice.setText(subtotalPrice);
    }

    @Override
    public void taxPrice(String taxPrice) {
        this.taxPrice.setText(taxPrice);
    }

    @Override
    public void isSuppressShipping(boolean suppressShipping) {
        if (suppressShipping){
            llShippingOptions.setVisibility(View.GONE);
        } else {
            llShippingOptions.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void setPresenter(CartCompletePresenter presenter) {
        mPresenter = checkNotNull(presenter);
    }
}