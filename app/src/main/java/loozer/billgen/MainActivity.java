package loozer.billgen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import loozer.billgen.db.ProductHandler;

public class MainActivity extends AppCompatActivity {

    private final int RESULT_SETTINGS = 0;

    Product ghee = null;
    Product butter = null;

    private double gheeCost = 0.0;
    private double butterCost = 0.0;

    private PDFGenerator billGen = null;
    private Spinner gheeSpinner = null;
    private Spinner butterSpinner = null;
    private CheckBox cbxGhee = null;
    private CheckBox cbxButter = null;
    private TextView txtTotalCost = null;
    private Button btnMakeBill = null;
    private String[] spinnerQtys = new String[]{"250 Grams","500 Grams","750 Grams","1 KG","2 KG","3 KG","4 KG","5 KG"};
    List<Product> productList = null;

    ProductHandler dbHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHandler = ProductHandler.getHandler(getBaseContext());

        ghee = new Product(Product.ProductName.Ghee,gheeCost);
        butter = new Product(Product.ProductName.Butter,butterCost);
        productList = new ArrayList<Product>();

        btnMakeBill = (Button) findViewById(R.id.btnPrintBill);
        cbxGhee = (CheckBox)findViewById(R.id.cbxGhee);
        cbxButter = (CheckBox)findViewById(R.id.cbxButter);

        ArrayAdapter<String> spinnerItems = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,spinnerQtys);
        gheeSpinner = (Spinner)findViewById(R.id.spinnerGhee);
        butterSpinner = (Spinner)findViewById(R.id.spinnerButter);
        gheeSpinner.setAdapter(spinnerItems);
        butterSpinner.setAdapter(spinnerItems);
        gheeSpinner.setEnabled(false);
        butterSpinner.setEnabled(false);

        Handler handler = new Handler(this);

        cbxGhee.setOnCheckedChangeListener(handler);
        cbxButter.setOnCheckedChangeListener(handler);

        SpinnerHandler spHandler = new SpinnerHandler();

        butterSpinner.setOnItemSelectedListener(spHandler);
        gheeSpinner.setOnItemSelectedListener(spHandler);

        btnMakeBill.setOnClickListener(handler);

        txtTotalCost = (TextView)findViewById(R.id.payTotal);
        billGen = new PDFGenerator(getBaseContext(),this);

        setRates();
        dispTotal();
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

        Log.i("Ghee Rate: ",sp.getString("prefGheeRate", "0.0"));
        Log.i("Butter Rate: ", sp.getString("prefButterRate", "0.0"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRates();
        dispTotal();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Toast.makeText(this,"Settings Clicked",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this,UserSettings.class);
            startActivityForResult(i, RESULT_SETTINGS);
            return true;
        }
        if(id == R.id.action_getReports){
            Toast.makeText(this,"Get Reports Clicked",Toast.LENGTH_SHORT).show();
            billGen.generateReport(dbHandler.getProducts());
            return true;
        }
        if(id == R.id.action_add_bills){
            Toast.makeText(this,"Add Bills Clicked",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this,AddBills.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_SETTINGS){
            Toast.makeText(this,"Setting Result",Toast.LENGTH_SHORT).show();
            setRates();
            dispTotal();
        }
    }


    /**
     * Handlers...!
     */
    private class Handler implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        MainActivity activity = null;

        public Handler(MainActivity activity){
            this.activity = activity;
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == btnMakeBill.getId()){
                //Toast.makeText(getBaseContext(),"Bill Generated",Toast.LENGTH_SHORT).show();
                if(cbxGhee.isChecked()){
                    productList.add(ghee);
                }
                if(cbxButter.isChecked()){
                    productList.add(butter);
                }
                if(productList.size()>0){
                    dbHandler.addToDB(productList);
                    billGen.generateBill(productList);
                }
                else{
                    Toast.makeText(getBaseContext(),"Please select atleast a Product.!", Toast.LENGTH_SHORT).show();
                }
            }
            productList.clear();
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(buttonView.getId() == cbxGhee.getId()){
                if(cbxGhee.isChecked()){
                    gheeSpinner.setEnabled(true);
                    ghee.setPrice(doCalc(ghee.getQty(), ghee.getRate()));
                    dispTotal();
                }
                else{
                    gheeSpinner.setEnabled(false);
                    ghee.setPrice(0.0);
                    dispTotal();
                }
            }
            if(buttonView.getId() == cbxButter.getId()){
                if(cbxButter.isChecked()){
                    butterSpinner.setEnabled(true);
                    butter.setPrice(doCalc(butter.getQty(), butter.getRate()));
                    dispTotal();
                }
                else{
                    butterSpinner.setEnabled(false);
                    butter.setPrice(0.0);
                    dispTotal();
                }
            }
        }
    }

    private class SpinnerHandler implements Spinner.OnItemSelectedListener{
        private String selectedItem =null;

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.i("ButterS:",butterSpinner.getId()+" "+position+" "+id+" GheeS: "+gheeSpinner.getId());
            Log.i("parentID: ",parent.getId()+" viewID: "+view.getId());

            selectedItem = spinnerQtys[position];
            if(parent.getId() == gheeSpinner.getId()){
                ghee.setQty(getQty(selectedItem));
                ghee.setPrice(doCalc(ghee.getQty(), ghee.getRate()));
                dispTotal();
            }
            if(parent.getId() == butterSpinner.getId()){
                butter.setQty(getQty(selectedItem));
                butter.setPrice(doCalc(butter.getQty(), butter.getRate()));
                dispTotal();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    /**
     * Private Methods....!
     */
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
        txtTotalCost.setText(df.format(total));
    }

    private double doCalc(double measure,double cost){
        return ((measure/1000) * cost);
    }

    private double getQty(String position){
        double qty = 0.0;
        switch(position)
        {
            case "250 Grams":   qty = 250.0;
                break;

            case "500 Grams":   qty = 500.0;
                break;
            case "750 Grams":   qty = 750.0;
                break;
            case "1 KG":   qty = 1000.0;
                break;
            case "2 KG":   qty = 2000.0;
                break;
            case "3 KG":   qty = 3000.0;
                break;
            case "4 KG":   qty = 4000.0;
                break;
            case "5 KG":   qty = 5000.0;
                break;
        }
        return qty;
    }
}