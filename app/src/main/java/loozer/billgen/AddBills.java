package loozer.billgen;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import loozer.billgen.R;
import loozer.billgen.db.ProductHandler;

public class AddBills extends AppCompatActivity {

    ProductHandler productHandler = null;

    Product ghee = null;
    Product butter = null;

    private double gheeCost = 0.0;
    private double butterCost = 0.0;

    List<Product> productList = null;

    private CheckBox cbxGhee = null;
    private CheckBox cbxButter = null;
    private EditText txtGhee = null;
    private EditText txtButter = null;
    private TextView totalCost = null;
    private Button btnAddBills = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bills);

        productHandler = ProductHandler.getHandler(getBaseContext());

        ghee = new Product(Product.ProductName.Ghee,gheeCost);
        butter = new Product(Product.ProductName.Butter,butterCost);
        productList = new ArrayList<Product>();

        cbxGhee = (CheckBox)findViewById(R.id.cbxGhee);
        cbxButter = (CheckBox)findViewById(R.id.cbxButter);
        txtGhee = (EditText)findViewById(R.id.edtxtGhee);
        txtButter = (EditText)findViewById(R.id.edtxtButter);
        totalCost = (TextView)findViewById(R.id.txtCost);
        btnAddBills = (Button)findViewById(R.id.btnAddBill);

        ViewHandler handler = new ViewHandler();

        cbxGhee.setOnCheckedChangeListener(handler);
        cbxButter.setOnCheckedChangeListener(handler);
        btnAddBills.setOnClickListener(handler);

        TextChangeListener textChangeListener = new TextChangeListener();

        txtGhee.addTextChangedListener(textChangeListener);
        txtButter.addTextChangedListener(textChangeListener);

        setRates();
        dispTotal();
    }

    private class ViewHandler implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(buttonView.getId() == cbxGhee.getId()){
                if(cbxGhee.isChecked()){
                    txtGhee.setEnabled(true);
                    txtGhee.setHint("Enter Ghee Qty");
                    ghee.setPrice(doCalc(ghee.getQty(), ghee.getRate()));
                    dispTotal();
                }
                else{
                    txtGhee.setEnabled(false);
                    txtGhee.setHint("Disabled");
                    ghee.setPrice(0.0);
                    dispTotal();
                }
            }
            if(buttonView.getId() == cbxButter.getId()){
                if(cbxButter.isChecked()){
                    txtButter.setEnabled(true);
                    txtButter.setHint("Enter Butter Qty");
                    butter.setPrice(doCalc(butter.getQty(), butter.getRate()));
                    dispTotal();
                }
                else{
                    txtButter.setEnabled(false);
                    txtButter.setHint("Disabled");
                    butter.setPrice(0.0);
                    dispTotal();
                }
            }
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == btnAddBills.getId()){
                if(cbxGhee.isChecked() && !txtGhee.getText().toString().isEmpty()){
                    productList.add(ghee);
                }
                if(cbxButter.isChecked() && !txtButter.getText().toString().isEmpty()){
                    productList.add(butter);
                }
                if(cbxGhee.isChecked() && txtGhee.getText().toString().isEmpty()){
                        Toast.makeText(getBaseContext(), "Please enter Ghee Qty.!", Toast.LENGTH_SHORT).show();
                }
                else if(cbxButter.isChecked() && txtButter.getText().toString().isEmpty()){
                        Toast.makeText(getBaseContext(), "Please enter Butter Qty.!", Toast.LENGTH_SHORT).show();
                }
                else if(productList.size()>0){
                    Toast.makeText(getBaseContext(), "List > 0", Toast.LENGTH_SHORT).show();
                    productHandler.addToDB(productList);
                    Toast.makeText(getBaseContext(), "Bill Added", Toast.LENGTH_SHORT).show();
                    clearAll();
                }
                else{
                    Toast.makeText(getBaseContext(), "Please select atleast a Product.!", Toast.LENGTH_SHORT).show();
                }
            }
            productList.clear();
        }
    }

    /**
     * Private Methods....!
     */

    private void clearAll(){
        productList.clear();
        if(cbxGhee.isChecked()){
            cbxGhee.toggle();
            txtGhee.setText("");
        }
        if(cbxButter.isChecked()) {
            cbxButter.toggle();
            txtButter.setText("");
        }
    }

    private void dispTotal() {
        double total = 0.0;
        double gCost = 0.0;
        double bCost = 0.0;
        if(cbxButter.isChecked()){
            bCost = butter.getPrice();
        }
        else{
            bCost =0.0;
        }
        if(cbxGhee.isChecked()){
            gCost =ghee.getPrice();
        }
        else{
            gCost = 0.0;
        }
        total =  gCost + bCost;
        DecimalFormat df = new DecimalFormat("#0.00");
        totalCost.setText(df.format(total));
    }

    private double doCalc(double measure,double cost){
        return ((measure/1000) * cost);
    }

    private void setRates() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        gheeCost  = Double.parseDouble(sp.getString("prefGheeRate", "0.0"));
        butterCost = Double.parseDouble(sp.getString("prefButterRate", "0.0"));
        ghee.setRate(gheeCost);
        butter.setRate(butterCost);

        ghee.setPrice(doCalc(ghee.getQty(), ghee.getRate()));
        butter.setPrice(doCalc(butter.getQty(), butter.getRate()));

        //Just for verification...!

        Log.i("Ghee Rate: ", sp.getString("prefGheeRate", "0.0"));
        Log.i("Butter Rate: ", sp.getString("prefButterRate", "0.0"));
    }

    private class TextChangeListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.hashCode() == txtGhee.getText().hashCode()){
                Toast.makeText(getBaseContext(),"Ghee Box",Toast.LENGTH_SHORT).show();

                double qty = 0.0;
                String txtQtyGhee = txtGhee.getText().toString();
                if(txtQtyGhee.isEmpty()||txtQtyGhee == null){
                    qty = 0.0;
                }
                else{
                    qty = Double.parseDouble(txtQtyGhee);
                }
                ghee.setQty(qty);
                ghee.setPrice(doCalc(ghee.getQty(), ghee.getRate()));
                dispTotal();
            }

            if(s.hashCode() == txtButter.getText().hashCode()){
                Toast.makeText(getBaseContext(),"Butter Box",Toast.LENGTH_SHORT).show();

                double qty = 0.0;
                String txtQtyButter = txtButter.getText().toString();
                if(txtQtyButter.isEmpty()||txtQtyButter == null){
                    qty = 0.0;
                }
                else{
                    qty = Double.parseDouble(txtQtyButter);
                }
                butter.setQty(qty);
                butter.setPrice(doCalc(butter.getQty(), butter.getRate()));
                dispTotal();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
