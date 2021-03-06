package ir.sweetsoft.orderapp.ui.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.print.PageRange;
import android.print.PdfPrint;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.print.onPdfMakeComplatedListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import common.SweetFonts;
import ir.sweetsoft.orderapp.R;

public class PrintActivity extends AppCompatActivity {

    Button btnPrint;
    WebView webView;
    String PDFName;
    String PDFPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        String WebViewContent=getIntent().getStringExtra("html_data");
        PDFName=getIntent().getStringExtra("pdf_name");
        PDFPath=getIntent().getStringExtra("pdf_path");
        if(PDFName==null || PDFName.length()==0)
            PDFName="no_name.pdf";
        if(PDFPath==null || PDFPath.length()==0)
            PDFPath=Environment.getExternalStorageDirectory().getAbsolutePath();
        Typeface font= SweetFonts.getFont(this,SweetFonts.IranSans);

        btnPrint=(Button)findViewById(R.id.btnprint);
        btnPrint.setTypeface(font);
        webView=(WebView)findViewById(R.id.webview);
        String mime = "text/html";
        String encoding = "utf-8";
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // do your stuff here

            }
        });

        webView.loadDataWithBaseURL("file:///android_asset/", WebViewContent, mime, encoding, null);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                webViewToJPG();
//                webViewToPDF();
                createWebPrintJob(webView);
            }
        });
    }
    private void webViewToPDF()
    {
        File path = new File(PDFPath);
        PrintDocumentAdapter printAdapter;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            printAdapter = webView.createPrintDocumentAdapter("test");
        } else {
            printAdapter = webView.createPrintDocumentAdapter();
        }
        PrintAttributes attributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A5)
                .setResolution(new PrintAttributes.Resolution("id", PRINT_SERVICE, 300, 300))
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                .setMinMargins(new PrintAttributes.Margins(0, 0, 0, 0))
                .build();
        PdfPrint p=new PdfPrint(attributes);
        p.print(printAdapter, path, PDFName, new onPdfMakeComplatedListener() {
            @Override
            public void onComplate(PageRange[] pr) {

            }
        });
    }
    private void webViewToJPG()
    {
        webView.measure(View.MeasureSpec.makeMeasureSpec(
                View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        webView.layout(0, 0, webView.getMeasuredWidth(),
                webView.getMeasuredHeight());
        webView.setDrawingCacheEnabled(true);
        webView.buildDrawingCache();
        Bitmap bm = Bitmap.createBitmap(webView.getMeasuredWidth(),
                webView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas bigcanvas = new Canvas(bm);
        Paint paint = new Paint();
        int iHeight = bm.getHeight();
        bigcanvas.drawBitmap(bm, 0, iHeight, paint);
        webView.draw(bigcanvas);
        System.out.println("1111111111111111111111="
                + bigcanvas.getWidth());
        System.out.println("22222222222222222222222="
                + bigcanvas.getHeight());

        if (bm != null) {
            try {
                String path = Environment.getExternalStorageDirectory()
                        .toString();
                OutputStream fOut = null;
                File file = new File(path, "/aaaa.png");
                fOut = new FileOutputStream(file);

                bm.compress(Bitmap.CompressFormat.PNG, 50, fOut);
                fOut.flush();
                fOut.close();
                bm.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void createWebPrintJob(WebView webView) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            PrintManager printManager = (PrintManager) this
                    .getSystemService(Context.PRINT_SERVICE);

            PrintDocumentAdapter printAdapter =
                    null;
            printAdapter = webView.createPrintDocumentAdapter("MyDocument");
            String jobName = getString(R.string.app_name) + " Print Test";

            printManager.print(jobName, printAdapter,
                    new PrintAttributes.Builder().build());
        }
        else{
            Toast.makeText(PrintActivity.this, "Print job has been canceled! ", Toast.LENGTH_SHORT).show();
        }

    }
}
