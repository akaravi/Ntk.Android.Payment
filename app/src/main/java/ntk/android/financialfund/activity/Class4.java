package ntk.android.financialfund.activity;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;

import es.dmoral.toasty.Toasty;
import io.reactivex.annotations.NonNull;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterDataModel;
import ntk.android.financialfund.R;
import ntk.android.financialfund.adapter.AccountSelectAdapter;
import ntk.android.financialfund.adapter.LoanSelectAdapter;
import ntk.android.financialfund.server.model.AccountModel;
import ntk.android.financialfund.server.model.LoanModel;
import ntk.android.financialfund.server.service.AccountService;
import ntk.android.financialfund.server.service.LoanService;

public class Class4 extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class4);
        ((TextView) findViewById(R.id.txtToolbar)).setText(getString(R.string.mainCard4));
        findViewById(R.id.btn_cancel).setOnClickListener(view -> finish());
        findViewById(R.id.back_button).setOnClickListener(view -> finish());
        getAccounts();
        getLoans();
        findViewById(R.id.btnOk).setOnClickListener(view -> checkData());
    }

    private void checkData() {
        AutoCompleteTextView accountId = findViewById(R.id.etAccountId);
        TextInputEditText amount = findViewById(R.id.etAmount);
        AutoCompleteTextView loanEt = findViewById(R.id.etLoan);
        if (accountId.getText().toString().equalsIgnoreCase(""))
            Toasty.error(this, "لطفا حساب سپرده ی خود را انتخاب کنید").show();
        else if (loanEt.getText().toString().equalsIgnoreCase(""))
            Toasty.error(this, "لطفا وام خود را انتخاب کنید").show();
        else if (amount.getText().toString().equalsIgnoreCase(""))
            Toasty.error(this, "مبلغ واریزی را مشخص نمایید").show();
        else {
            long price = 0;
            try {
                price = Long.parseLong(amount.getText().toString());
            } catch (Exception e) {
                Toasty.error(this, "مبلغ واریزی نا معتبر است").show();
                return;
            }
            if (price < 100) {
                Toasty.error(this, "حداقل مبلغ واریزی 100 ریال می باشد").show();
            } else
                callApi();
        }

    }

    private void callApi() {

    }

    private void getAccounts() {
        switcher.showProgressView();
        ServiceExecute.execute(new AccountService(this).getAll(new FilterDataModel())).subscribe(new NtkObserver<ErrorException<AccountModel>>() {
            @Override
            public void onNext(@NonNull ErrorException<AccountModel> accountModelErrorException) {
                switcher.showContentView();
                AutoCompleteTextView account = (AutoCompleteTextView) findViewById(R.id.etAccountId);
                TextInputEditText Name = findViewById(R.id.etName);
                account.setAdapter(new AccountSelectAdapter(Class4.this, accountModelErrorException.ListItems));
                account.setOnItemClickListener((adapterView, view12, i, l) -> {
                    if (i >= 0) {
                        account.setText(((AccountModel) adapterView.getItemAtPosition(i)).AccountId);
                        Name.setText(((AccountModel) adapterView.getItemAtPosition(i)).Name);
                    } else {
                        account.setText("");
                        Name.setText("");
                    }
                });
            }

            @Override
            public void onError(@NonNull Throwable e) {
                switcher.showErrorView(e.getMessage(), Class4.this::getAccounts);
            }
        });
    }

    private void getLoans() {
        switcher.showProgressView();
        ServiceExecute.execute(new LoanService(this).getAll(new FilterDataModel())).subscribe(new NtkObserver<ErrorException<LoanModel>>() {
            @Override
            public void onNext(@NonNull ErrorException<LoanModel> accountModelErrorException) {
                switcher.showContentView();
                AutoCompleteTextView loanEt = (AutoCompleteTextView) findViewById(R.id.etLoan);
                TextInputEditText Name = findViewById(R.id.etLoanName);
                TextInputEditText Amount = findViewById(R.id.etAmount);
                loanEt.setAdapter(new LoanSelectAdapter(Class4.this, accountModelErrorException.ListItems));
                loanEt.setOnItemClickListener((adapterView, view12, i, l) -> {
                    if (i >= 0) {
                        loanEt.setText(((LoanModel) adapterView.getItemAtPosition(i)).Name);
                        Name.setText(((LoanModel) adapterView.getItemAtPosition(i)).AccountId);
                        Amount.setText(((LoanModel) adapterView.getItemAtPosition(i)).Amount + "");
                    } else {
                        loanEt.setText("");
                        Name.setText("");
                        Amount.setText("");
                    }
                });
            }

            @Override
            public void onError(@NonNull Throwable e) {
                switcher.showErrorView(e.getMessage(), Class4.this::getLoans);
            }
        });
    }
}
