package loozer.billgen;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Loozer on 4/23/2016.
 */
public class PDFGenerator {

    private static String PATH = Environment.getExternalStoragePublicDirectory("") + "";
    private Context context = null;
    private MainActivity activity = null;
    private String fileName = null;
    private String filePath = null;
    private int height = 0;
    private int width = 0;
    private Product p = null;
    private Date date = null;

    public PDFGenerator(Context context, MainActivity activity) {
        this.context = context;
        this.activity = activity;
        getPageSize();
    }

    public void generateBill(List<Product> orderList) {
        date = new Date();
        Log.i("Date : ",""+date.getYear());
        OutputStream outputStream = null;
        try {
            File fileDirectory = new File(PATH + "/BillGen/Bills/");
            fileDirectory.mkdirs();
            File file = new File(fileDirectory, getFileName());
            outputStream = new FileOutputStream(file);
            Document doc = getDocument();
            PdfWriter writer = PdfWriter.getInstance(doc, outputStream);
            writer.open();
            doc.open();

            Paragraph header = new Paragraph();
            header.add(getPageHeader());
            header.setAlignment(Element.ALIGN_JUSTIFIED);
            header.setAlignment(Element.ALIGN_CENTER);
            doc.add(header);

            Paragraph content = new Paragraph();
            content.add(getItems(orderList));
            doc.add(content);

            Paragraph footer = new Paragraph();
            footer.add(getFooter());
            footer.setAlignment(Element.ALIGN_JUSTIFIED);
            footer.setAlignment(Element.ALIGN_CENTER);
            doc.add(footer);

            doc.close();
            writer.close();
            outputStream.close();
            filePath = PATH + "/BillGen/Bills/" + fileName;
            Toast.makeText(context, "Your bill is Here \n" + filePath, Toast.LENGTH_SHORT).show();
            dbTest(orderList);
            print(activity,filePath);

        } catch (Exception ex) {
            Log.i("File Exception : ", ex.toString());
        }
    }

    private void print(MainActivity activity,String filePath){
        File f = new File(filePath);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(f), "application/pdf");
        activity.startActivity(i);
        Toast.makeText(activity,"Printer Service",Toast.LENGTH_SHORT).show();
    }

    public Document getDocument() {
        Rectangle rect = new Rectangle(width, height);
        Document doc = new Document(rect);
        return doc;
    }


    //Bill Header....!
    public Chunk getPageHeader() {
        Chunk c = new Chunk();
        Font f = new Font(FontFamily.TIMES_ROMAN,12);
        c.setFont(f);
        c.append("VADIVUDAI AMMAN STORE\n");
        c.append("No:6,T.P.Kovil Street,Triplicane,\n");
        c.append("Chennai-600005\n\n");
        return c;
    }

    //Bill items...!
    public Chunk getItems(List<Product> orderedProducts) {
        Chunk c = new Chunk();
        c.append("Date: "+getFormattedDate(date)+"\n");
        c.append("---------------------------------------------------------------------\n");
        c.append("No.   Product         Qty            Rs/kg           Price\n");
        c.append("---------------------------------------------------------------------\n");

        DecimalFormat df = new DecimalFormat("#0.00");
        double total = 0.0;
        int count = orderedProducts.size();
        for (int i = 0; i < count; i++) {
            p = orderedProducts.get(i);
            c.append((i + 1) + ".      " + p.getType() + "           " + p.getQty() +
                    "         " + p.getRate() + "            " + p.getPrice() + "\n");
            total += p.getPrice();
        }
        c.append("---------------------------------------------------------------------\n");
        c.append("\n                                                 Pay Total:    "+df.format(total));
        return c;
    }

    //Bill footer....!
    public Chunk getFooter() {
        Chunk c = new Chunk();
        c.append("\n---------------------------------------------------------------------\n");
        c.append("Thank You Visit Again\n");
        c.append("---------------------------------------------------------------------\n");
        return c;
    }

    //Bill page size.....!
    public void getPageSize() {
        /*DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;*/

        height = 500;
        width = 350;
    }

    //File Name generator....!
    public String getFileName() {
        fileName = (date.getYear() % 100) + "-" + (date.getMonth() + 1) + "-" + date.getDate() + "-" +
                date.getHours() + "." + date.getMinutes() + "." + date.getSeconds() + ".pdf";
        return fileName;
    }

    public void generateReport(List<Product> list){
        generateBill(list);
    }

    private void dbTest(List<Product> list){
        Log.i("ListSize: ", "" + list.size());
        if(list.size()!=0){
            for(int i=0;i<list.size();i++) {
                Product p = list.get(i);
                Log.i((i+1)+"","Name: "+p.getType().toString()
                +" Qty: "+p.getQty()+" Rate: "+p.getRate()+" Price: "+p.getPrice());
            }
        }
    }

    private String getFormattedDate(Date date){
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(date);
    }
}