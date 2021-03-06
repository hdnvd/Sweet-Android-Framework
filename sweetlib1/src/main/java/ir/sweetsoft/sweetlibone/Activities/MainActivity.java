package ir.sweetsoft.sweetlibone.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import common.BaseFragmentActivity;
import ir.sweetsoft.sweetlibone.R;
import layout.AboutDeveloperFragment;
import layout.AboutUsFragment;
import layout.AdminMenuFragment;
import layout.DoctorMenuFragment;
import layout.LoginFragment;
import layout.MenuFragment;
import layout.PurchaseFragment;
import layout.SelectSpecialityGroupFragment;
import layout.SignupFragment;
import layout.SignupMenuFragment;
import ocms.SpecialityFragment;

public class MainActivity extends BaseFragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , LoginFragment.OnFragmentInteractionListener
        , SignupFragment.OnFragmentInteractionListener {

    //Shared Configurations
    public String SharedConf_VideoURL ="";
    public String SharedConf_VideoFileName ="";
    public boolean SharedConf_showAllCourses =false;
    public long SharedConf_ItemID =-1;
    public int SharedConf_FileMode =0;
    public int SharedConf_CourseID;
    public int SharedConf_year;
    public int SharedConf_month;
    public int SharedConf_day;
    public long SharedConf_doctorid;
    public int SharedConf_presencetype;
    public int SharedConf_specialityGroup;
    public long SharedConf_speciality;
    //EOF Shared Configurations

    private static final String MEDIA = "media";
    private static final int LOCAL_AUDIO = 1;
    private static final int LOCAL_VIDEO = 4;
    private TextView titleBar;
    public WebView mainWebView;
    private boolean SharedConf_hasWebView=true;
    private boolean SharedConf_hasUserManagement=true;

    public void setSharedConf_hasCustomActionBar(boolean sharedConf_hasCustomActionBar) {
        SharedConf_hasCustomActionBar = sharedConf_hasCustomActionBar;
    }

    private boolean SharedConf_hasCustomActionBar=true;
    private int LayoutID=-1;
    private NavigationView navigationView;

    protected NavigationView getNavigationView() {
        return navigationView;
    }

    public void setTitleText(String Text)
    {
        titleBar.setText(Text);
    }
    public void OpenInPlayer(String VideoPath)
    {

        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
        intent.putExtra(MEDIA, LOCAL_VIDEO);
        intent.putExtra("path", VideoPath);
        startActivity(intent);
    }
    public void setNavigationDrawerLockState(int State)
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(State);
        ImageView imgmenu=(ImageView)findViewById(R.id.imgmenu);
        if(State==DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        {
            imgmenu.setVisibility(View.GONE);
        }
        else
        {

            imgmenu.setVisibility(View.VISIBLE);
        }
    }

    protected void setSharedConf_hasWebView(boolean sharedConf_hasWebView) {
        SharedConf_hasWebView = sharedConf_hasWebView;
    }

    protected void setSharedConf_hasUserManagement(boolean sharedConf_hasUserManagement) {
        SharedConf_hasUserManagement = sharedConf_hasUserManagement;
    }

    public void setLayoutID(int layoutID) {
        LayoutID = layoutID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(LayoutID<0)
            LayoutID=ir.sweetsoft.sweetlibone.R.layout.activity_main;

        setContentView(LayoutID);
        if(SharedConf_hasWebView)
        {
            mainWebView = (WebView)findViewById(R.id.webview);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mainWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                mainWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            WebSettings webSettings = mainWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            mainWebView.setVerticalScrollBarEnabled(false);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setPluginState(WebSettings.PluginState.ON);
            mainWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }
            });
        }


        if(shouldAskPermission())
            requestFileAccessPermission(1147);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Typeface face= Typeface.createFromAsset(getAssets(),"fonts/IRANSansMobile.ttf");
        if(SharedConf_hasCustomActionBar)
        {

            titleBar=(TextView)findViewById(R.id.pagetitle);
            titleBar.setTypeface(face);
            ImageView imgmenu=(ImageView)findViewById(R.id.imgmenu);
            imgmenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawer.openDrawer(navigationView);
                }
            });
        }

        TextView settingstitle=(TextView)navigationView.getHeaderView(0).findViewById(R.id.settingstitle);
        settingstitle.setTypeface(face);
        if(SharedConf_hasUserManagement)
        {
            String UserName= getSharedPreferences(Constants.GENERAL_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.USERNAME,"0");
            if(UserName.equals("0"))
                showFragment(SignupMenuFragment.class,false);
            else
            {
                String UserName2= getSharedPreferences(Constants.GENERAL_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.USERNAME,"0");
                String Password2= getSharedPreferences(Constants.GENERAL_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.PASSWORD,"0");
                int ROLE2= getSharedPreferences(Constants.GENERAL_PREFERENCES, Context.MODE_PRIVATE).getInt(Constants.ROLE,0);
                String postData="username="+UserName2+"&password="+Password2+"&action=login_Click";
                String URL=Constants.LOGIN_URL;
//            mainWebView.setVisibility(View.VISIBLE);
                mainWebView.postUrl(URL,postData.getBytes());
                mainWebView.loadUrl("about:blank");
                routeToIndex();
            }
        }

    }

    public int getMainContentID()
    {
        return R.id.MainContent;
    }
    public void routeToIndex()
    {
        String UserName= getSharedPreferences(Constants.GENERAL_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.USERNAME,"0");
        String Password= getSharedPreferences(Constants.GENERAL_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.PASSWORD,"0");
        Log.d("Route","Routing to Index");
        int ROLE= getSharedPreferences(Constants.GENERAL_PREFERENCES, Context.MODE_PRIVATE).getInt(Constants.ROLE,0);
        if(ROLE==3)
            showFragment(DoctorMenuFragment.class);
        else if(ROLE==2)
            showFragment(AdminMenuFragment.class);
        else
            showFragment(MenuFragment.class);

        setNavigationDrawerLockState(DrawerLayout.LOCK_MODE_UNLOCKED);
    }
    @Override
    public void onBackPressed() {
        if (SharedConf_hasWebView && mainWebView.canGoBack()) {
            mainWebView.goBack();
        }
        else if(!FragmentCallList.empty()){
            showFragment(FragmentCallList.pop(),false);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("dsdd",String.valueOf(id));
        if(SharedConf_hasWebView)
            mainWebView.setVisibility(View.GONE);
        if (id == R.id.nav_buy) {
            showFragment(PurchaseFragment.class);
        } else if (id == R.id.nav_mainmenu) {
            routeToIndex();
        }else if (id == R.id.nav_contactus) {
            showFragment(AboutUsFragment.class);
        }else if (id == R.id.nav_aboutdeveloper) {
            showFragment(AboutDeveloperFragment.class);
        }else if (id == R.id.nav_logout) {
            LocalUserInfo.LogOut(this);
            showFragment(SignupMenuFragment.class,false);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}
