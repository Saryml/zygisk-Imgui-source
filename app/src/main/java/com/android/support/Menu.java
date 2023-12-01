//Please don't replace listeners with lambda!

package com.android.support;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.io.InputStreamReader;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.widget.RelativeLayout.ALIGN_PARENT_LEFT;
import static android.widget.RelativeLayout.ALIGN_PARENT_RIGHT;

import org.xml.sax.ErrorHandler;

public class Menu {
    //********** Here you can easly change the menu appearance **********//

    //region Variable
    public static final String TAG = "Mod_Menu"; //Tag for logcat

    int TEXT_COLOR = Color.parseColor("#82CAFD");
    int TEXT_COLOR_2 = Color.parseColor("#FFFFFF");
    int BTN_COLOR = Color.parseColor("#1C262D");
    int MENU_BG_COLOR = Color.parseColor("#EE1C2A35"); //#AARRGGBB
    int MENU_FEATURE_BG_COLOR = Color.parseColor("#DD141C22"); //#AARRGGBB
    int MENU_WIDTH = 350;
    int MENU_HEIGHT = 140;
    int POS_X = 0;
    int POS_Y = 100;

    float MENU_CORNER = 4f;
    int ICON_SIZE = 30; //Change both width and height of image
    float ICON_ALPHA = 0.2f; //Transparent
    int ToggleON = Color.GREEN;
    int ToggleOFF = Color.RED;
    int BtnON = Color.parseColor("#1b5e20");
    int BtnOFF = Color.parseColor("#7f0000");
    int CategoryBG = Color.parseColor("#2F3D4C");
    int SeekBarColor = Color.parseColor("#FFCCCCCC");
    int SeekBarProgressColor = Color.parseColor("#2F3D4C");
    int CheckBoxColor = Color.parseColor("#80CBC4");
    int RadioColor = Color.parseColor("#2F3D4C");
    String NumberTxtColor = "#41c300";
    //********************************************************************//

    GradientDrawable gdMenuBody, gdAnimation = new GradientDrawable();
    RelativeLayout mCollapsed, mRootContainer;
    LinearLayout mExpanded, mods, mSettings, mCollapse;
    LinearLayout.LayoutParams scrlLLExpanded, scrlLL;
    WindowManager mWindowManager;
    WindowManager.LayoutParams vmParams;
    ImageView startimage;
    FrameLayout rootFrame;
    ScrollView scrollView;
    boolean stopChecking, overlayRequired;
    Context getContext;

    //initialize methods from the native library
    native void Init(Context context, TextView title, TextView subTitle);

    native String Icon();

    native String IconWebViewData();

    native String[] GetFeatureList();

    native String[] SettingsList();

    native boolean IsGameLibLoaded();
    
    native String Title();
    
    private InputStream open;
    
    public static native String Kapan();


    //Here we write the code for our Menu
    // Reference: https://www.androidhive.info/2016/11/android-floating-widget-like-facebook-chat-head/
    LinearLayout lin1,lin2,lin3,lin4,lin5,lin6;
    public Menu(Context context) {

        getContext = context;
        Preferences.context = context;
        rootFrame = new FrameLayout(context); // Global markup
        rootFrame.setOnTouchListener(onTouchListener());
        mRootContainer = new RelativeLayout(context); // Markup on which two markups of the icon and the menu itself will be placed
        mCollapsed = new RelativeLayout(context); // Markup of the icon (when the menu is minimized)
        mCollapsed.setVisibility(View.VISIBLE);
        mCollapsed.setAlpha(ICON_ALPHA);

        //********** The box of the mod menu **********
        mExpanded = new LinearLayout(context); // Menu markup (when the menu is expanded)
   /*  try {
context.open = getAssets().open("Photo.png");
} catch (IOException e) {
e.printStackTrace();
context.open = (InputStream) null; 
}*/
       mExpanded.setVisibility(View.GONE);
        mExpanded.setBackgroundColor(MENU_BG_COLOR);
       mExpanded.setOrientation(LinearLayout.VERTICAL);
        mExpanded.setPadding(3, 2, 5, 2);
       // mExpanded.setBackground(Drawable.createFromStream(context.open, (String) null));
       mExpanded.setLayoutParams(new LinearLayout.LayoutParams(dp(MENU_WIDTH), WRAP_CONTENT)); 

       
       android.graphics.drawable.GradientDrawable bg6 = new android.graphics.drawable.GradientDrawable();
		int bg6ADD[] = new int[]{ Color.parseColor("#FFDBDBDB"), Color.parseColor("#FFDBDBDB"), Color.parseColor("#FFDBDBDB") };
		bg6.setColors(bg6ADD);
        bg6.setCornerRadii(new float[]{10, 10, 10, 10, 10, 10, 10, 10});
        bg6.setStroke(3, Color.parseColor("#FF4F5154"));
       mExpanded.setBackground(bg6);
        //mExpanded.setBackground(gdMenuBody); //Apply GradientDrawable to it

        //********** The icon to open mod menu **********
        startimage = new ImageView(context);
        startimage.setLayoutParams(new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        int applyDimension = (int) TypedValue.applyDimension(1, ICON_SIZE, context.getResources().getDisplayMetrics()); //Icon size
        startimage.getLayoutParams().height = applyDimension;
        startimage.getLayoutParams().width = applyDimension;
        //startimage.requestLayout();
        startimage.setScaleType(ImageView.ScaleType.FIT_XY);
        byte[] decode = Base64.decode(Icon(), 0);
        startimage.setImageBitmap(BitmapFactory.decodeByteArray(decode, 0, decode.length));
        ((ViewGroup.MarginLayoutParams) startimage.getLayoutParams()).topMargin = convertDipToPixels(10);
        //Initialize event handlers for buttons, etc.
        startimage.setOnTouchListener(onTouchListener());
        startimage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mCollapsed.setVisibility(View.GONE);
                mExpanded.setVisibility(View.VISIBLE);
            }
        });

        //********** The icon in Webview to open mod menu **********
        WebView wView = new WebView(context); //Icon size width=\"50\" height=\"50\"
        wView.setLayoutParams(new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        int applyDimension2 = (int) TypedValue.applyDimension(1, ICON_SIZE, context.getResources().getDisplayMetrics()); //Icon size
        wView.getLayoutParams().height = applyDimension2;
        wView.getLayoutParams().width = applyDimension2;
        wView.loadData("<html>" +
                "<head></head>" +
                "<body style=\"margin: 0; padding: 0\">" +
                "<img src=\"" + IconWebViewData() + "\" width=\"" + ICON_SIZE + "\" height=\"" + ICON_SIZE + "\" >" +
                "</body>" +
                "</html>", "text/html", "utf-8");
        wView.setBackgroundColor(0x00000000); //Transparent
        wView.setAlpha(ICON_ALPHA);
        wView.getSettings().setAppCacheEnabled(true);
        wView.setOnTouchListener(onTouchListener());

        //********** Settings icon **********
        
    /*    
    TextView settings = new TextView(context); //Android 5 can't show ⚙, instead show other icon instead
		android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
		int bgADD[] = new int[]{ Color.parseColor("#FFA6A6A6"), Color.parseColor("#00000000"), Color.parseColor("#FFA6A6A6") };
		bg.setColors(bgADD);
        bg.setCornerRadii(new float[]{10, 10, 10, 10, 10, 10, 10, 10});
        bg.setStroke(2, Color.parseColor("#FF4F5154"));
        settings.setGravity(57);
        settings.setBackground(bg);
        settings.setText("Tele");
        settings.setPadding(30, 0, 30, 0);
        settings.setTextColor(Color.parseColor("#2F3D4C"));
        settings.setTextSize(10.0f);
        settings.setTypeface(Typeface.SERIF);
        RelativeLayout.LayoutParams lParamsHideBtn1 = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
		lParamsHideBtn1.addRule(RelativeLayout.CENTER_VERTICAL);
		//lParamsHideBtn1.addRule(RelativeLayout.CENTER_HORIZONTAL);
		lParamsHideBtn1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		settings.setLayoutParams(lParamsHideBtn1);
        settings.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    mCollapsed.setVisibility(View.VISIBLE);
                    mCollapsed.setAlpha(ICON_ALPHA);
                    mExpanded.setVisibility(View.GONE);
                }
            });
        settings.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View view) {
                    mCollapsed.setVisibility(View.VISIBLE);
                    mCollapsed.setAlpha(0);
                    mExpanded.setVisibility(View.GONE);
                    return true;
                }
            });*/
        
        lin1 = new LinearLayout(context);
        lin1.setOrientation(LinearLayout.VERTICAL);
        lin1.setVisibility(View.VISIBLE);
		lin2 = new LinearLayout(context);
        lin2.setOrientation(LinearLayout.VERTICAL);
		lin2.setVisibility(View.GONE);
		lin3 = new LinearLayout(context);
        lin3.setOrientation(LinearLayout.VERTICAL);
		lin3.setVisibility(View.GONE);
		lin4 = new LinearLayout(context);
        lin4.setOrientation(LinearLayout.VERTICAL);
        lin4.setVisibility(View.GONE);
		lin5 = new LinearLayout(context);
        lin5.setOrientation(LinearLayout.VERTICAL);
		lin5.setVisibility(View.GONE);
		lin6 = new LinearLayout(context);
        lin6.setOrientation(LinearLayout.VERTICAL);
		lin6.setVisibility(View.GONE);

        //********** Settings **********
        mSettings = new LinearLayout(context);
        mSettings.setOrientation(LinearLayout.VERTICAL);
        featureList(SettingsList(), mSettings);

        //********** Title **********
        RelativeLayout titleText = new RelativeLayout(context);
        titleText.setPadding(10, 5, 10, 5);
        titleText.setVerticalGravity(16);

        TextView title = new TextView(context);
        title.setTextColor(CategoryBG);
        title.setTextSize(18.0f);
        title.setGravity(Gravity.RIGHT);
        title.setTypeface(Typeface.SERIF);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        rl.addRule(RelativeLayout.CENTER_VERTICAL);
        title.setLayoutParams(rl);

        //********** Sub title **********
        TextView subTitle = new TextView(context);
        subTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        subTitle.setMarqueeRepeatLimit(-1);
        subTitle.setSingleLine(true);
        subTitle.setSelected(true);
        subTitle.setTextColor(TEXT_COLOR);
        subTitle.setTypeface(Typeface.SERIF);
        subTitle.setTextSize(10.0f);
        subTitle.setGravity(Gravity.LEFT);
        subTitle.setPadding(5, 0, 0, 5);

        //********** Mod menu feature list **********
        scrollView = new ScrollView(context);
        //Auto size. To set size manually, change the width and height example 500, 500
        scrlLL = new LinearLayout.LayoutParams(MATCH_PARENT, dp(MENU_HEIGHT));
        scrlLL.setMargins(20, 0, 20, 0);
        scrlLLExpanded = new LinearLayout.LayoutParams(mExpanded.getLayoutParams());
        scrlLLExpanded.weight = 1.0f;
        scrollView.setLayoutParams(Preferences.isExpanded ? scrlLLExpanded : scrlLL);
       // scrollView.setBackgroundColor(MENU_FEATURE_BG_COLOR);
        
    
        android.graphics.drawable.GradientDrawable gg2 = new android.graphics.drawable.GradientDrawable();
		int gg2ADD[] = new int[]{ Color.parseColor("#FFDBDBDB"), Color.parseColor("#FFDBDBDB"), Color.parseColor("#FFCBCBCB") };
		gg2.setColors(gg2ADD);
        gg2.setCornerRadii(new float[]{5, 5, 5, 5, 5, 5, 5, 5});
        gg2.setStroke(3, Color.parseColor("#FF4F5154"));
        scrollView.setPadding(0, 5, 0, 5);
        scrollView.setBackground(gg2);
       
        
        

        mods = new LinearLayout(context);
        mods.setOrientation(LinearLayout.VERTICAL);
        
        
       /* scrollView = new ScrollView(context);
        //Auto size. To set size manually, change the width and height example 500, 500
        scrlLL = new LinearLayout.LayoutParams(MATCH_PARENT, dp(MENU_HEIGHT));
        scrlLLExpanded = new LinearLayout.LayoutParams(mExpanded.getLayoutParams());
        scrlLLExpanded.weight = 1.0f;
        scrollView.setLayoutParams(Preferences.isExpanded ? scrlLLExpanded : scrlLL);
        scrollView.setBackgroundColor(MENU_FEATURE_BG_COLOR);
        mods = new LinearLayout(context);
        mods.setOrientation(LinearLayout.VERTICAL);*/

        //********** RelativeLayout for buttons **********
        RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setPadding(10, 10, 10, 10);
        relativeLayout.setVerticalGravity(Gravity.CENTER);

        //**********  Hide/Kill button **********
        RelativeLayout.LayoutParams lParamsHideBtn = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lParamsHideBtn.addRule(ALIGN_PARENT_LEFT);
        
        
        TextView hideBtn = new TextView(context); //Android 5 can't show ⚙, instead show other icon instead
		android.graphics.drawable.GradientDrawable bg1 = new android.graphics.drawable.GradientDrawable();
		int bg1ADD[] = new int[]{ Color.parseColor("#FFA6A6A6"), Color.parseColor("#00000000"), Color.parseColor("#FFA6A6A6") };
		bg1.setColors(bg1ADD);
        bg1.setCornerRadii(new float[]{10, 10, 10, 10, 10, 10, 10, 10});
        bg1.setStroke(2, Color.parseColor("#FF4F5154"));
        hideBtn.setGravity(57);
        hideBtn.setBackground(bg1);
        hideBtn.setText("Exit");
        hideBtn.setPadding(50, 0, 50, 0);
        hideBtn.setTextColor(Color.parseColor("#2F3D4C"));
        hideBtn.setTextSize(12.0f);
        hideBtn.setTypeface(Typeface.SERIF);
        hideBtn.setLayoutParams(lParamsHideBtn);
        hideBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mCollapsed.setVisibility(View.VISIBLE);
                mCollapsed.setAlpha(0);
                mExpanded.setVisibility(View.GONE);
                Toast.makeText(view.getContext(), "Icon hidden. Remember the hidden icon position", Toast.LENGTH_LONG).show();
            }
        });
        hideBtn.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                Toast.makeText(view.getContext(), "Menu killed", Toast.LENGTH_LONG).show();
                rootFrame.removeView(mRootContainer);
                mWindowManager.removeView(rootFrame);
                return false;
            }
        });

      /*  Button hideBtn = new Button(context);
        hideBtn.setLayoutParams(lParamsHideBtn);
        hideBtn.setBackgroundColor(Color.TRANSPARENT);
        hideBtn.setText("Exit");
        hideBtn.setTextColor(TEXT_COLOR);
        hideBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mCollapsed.setVisibility(View.VISIBLE);
                mCollapsed.setAlpha(0);
                mExpanded.setVisibility(View.GONE);
                Toast.makeText(view.getContext(), "Icon hidden. Remember the hidden icon position", Toast.LENGTH_LONG).show();
            }
        });
        hideBtn.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                Toast.makeText(view.getContext(), "Menu killed", Toast.LENGTH_LONG).show();
                rootFrame.removeView(mRootContainer);
                mWindowManager.removeView(rootFrame);
                return false;
            }
        });*/

        //********** Close button **********
        RelativeLayout.LayoutParams lParamsCloseBtn = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lParamsCloseBtn.addRule(ALIGN_PARENT_RIGHT);
        
        TextView closeBtn = new TextView(context); //Android 5 can't show ⚙, instead show other icon instead
		android.graphics.drawable.GradientDrawable bg11 = new android.graphics.drawable.GradientDrawable();
		int bg11ADD[] = new int[]{ Color.parseColor("#FFA6A6A6"), Color.parseColor("#00000000"), Color.parseColor("#FFA6A6A6") };
		bg11.setColors(bg11ADD);
        bg11.setCornerRadii(new float[]{10, 10, 10, 10, 10, 10, 10, 10});
        bg11.setStroke(2, Color.parseColor("#FF4F5154"));
        closeBtn.setGravity(57);
        closeBtn.setBackground(bg11);
        closeBtn.setText("Hide");
        closeBtn.setPadding(50, 0, 50, 0);
        closeBtn.setTextColor(Color.parseColor("#2F3D4C"));
        closeBtn.setTextSize(12.0f);
        closeBtn.setTypeface(Typeface.SERIF);
        closeBtn.setLayoutParams(lParamsCloseBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mCollapsed.setVisibility(View.VISIBLE);
                mCollapsed.setAlpha(ICON_ALPHA);
                mExpanded.setVisibility(View.GONE);
            }
        });
        
        LinearLayout BadCategory = new LinearLayout(context);
        BadCategory.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
		BadCategory.setVisibility(View.VISIBLE);
		BadCategory.setGravity(Gravity.CENTER);
		BadCategory.setPadding(10, 10, 10, 10);
        BadCategory.setOrientation(LinearLayout.HORIZONTAL);

		final  GradientDrawable Menu_Bad = new GradientDrawable();
		final  GradientDrawable Menu_Bad2 = new GradientDrawable();
		final  GradientDrawable Menu_Bad3 = new GradientDrawable();
		final  GradientDrawable Menu_Bad4 = new GradientDrawable();
		final  GradientDrawable Menu_Bad5 = new GradientDrawable();
		final  GradientDrawable Menu_Bad6 = new GradientDrawable();
		

		final TextView Menu_Aim = new TextView(context);
        final LinearLayout.LayoutParams Menu_Aim_Params = new LinearLayout.LayoutParams(dp(63), dp(25));
		Menu_Aim_Params.setMargins(4, 10, 4, 10);
		Menu_Aim.setLayoutParams(Menu_Aim_Params);
        Menu_Aim.setTextSize(8.0f);
        Menu_Aim.setTextColor(-1);
        Menu_Aim.setGravity(17);
		Menu_Aim.setTypeface(Typeface.SERIF);
        Menu_Aim.setText("ESP Menu");
        Menu_Aim.setTextColor(Color.parseColor("#FF000000"));
		
		Menu_Bad.setShape(GradientDrawable.RECTANGLE);
		Menu_Bad.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		int Menu_BadADD[] = new int[]{ Color.parseColor("#BF4585C6"), Color.parseColor("#BF4585C6"), Color.parseColor("#BF4585C6"), Color.parseColor("#CADDEEFF") };
		Menu_Bad.setColors(Menu_BadADD);
		Menu_Bad.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, 0, 0});
        Menu_Bad.setStroke(2, Color.parseColor("#FF4F5154"));
		Menu_Bad.setColor(Color.parseColor("#FFA6A6A6")); 
		Menu_Aim.setBackground(Menu_Bad);

		Menu_Aim.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
					Menu_Bad.setColor(Color.parseColor("#FF7C7C7C"));
					Menu_Bad2.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad3.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad4.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad5.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad6.setColor(Color.parseColor("#FFA6A6A6"));				
					lin1.setVisibility(View.VISIBLE);
					lin2.setVisibility(View.GONE);
                    lin3.setVisibility(View.GONE);
					lin4.setVisibility(View.GONE);
					lin5.setVisibility(View.GONE);
					lin6.setVisibility(View.GONE);				
                }
            });


		final TextView Menu_Esp = new TextView(context);        
        Menu_Esp.setLayoutParams(Menu_Aim_Params);

        Menu_Esp.setTextSize(8.0f);
        Menu_Esp.setTextColor(-1);
        Menu_Esp.setGravity(17);
		Menu_Esp.setTypeface(Typeface.SERIF);
        Menu_Esp.setText("Show Menu");
        Menu_Esp.setTextColor(Color.parseColor("#FF000000"));
        
        
        
		Menu_Bad2.setShape(GradientDrawable.RECTANGLE);
		Menu_Bad2.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		int Menu_Bad2ADD[] = new int[]{ Color.parseColor("#BF4585C6"), Color.parseColor("#BF4585C6"), Color.parseColor("#BF4585C6"), Color.parseColor("#CADDEEFF") };
		Menu_Bad2.setColors(Menu_Bad2ADD);
		Menu_Bad2.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, 0, 0});
        Menu_Bad2.setStroke(2, Color.parseColor("#FF4F5154"));
		Menu_Bad2.setColor(Color.parseColor("#FFA6A6A6"));
		Menu_Esp.setBackground(Menu_Bad2); 
		

		Menu_Esp.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
					Menu_Bad.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad2.setColor(Color.parseColor("#FF7C7C7C"));
					Menu_Bad3.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad4.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad5.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad6.setColor(Color.parseColor("#FFA6A6A6"));				
					lin1.setVisibility(View.GONE);
					lin2.setVisibility(View.VISIBLE);
					lin3.setVisibility(View.GONE);
					lin4.setVisibility(View.GONE);
                    lin5.setVisibility(View.GONE);
					lin6.setVisibility(View.GONE);
					


                }
            });

		final TextView Menu_Ext = new TextView(context);

        Menu_Ext.setLayoutParams(Menu_Aim_Params);

        Menu_Ext.setTextSize(8.0f);
        Menu_Ext.setTextColor(-1);
        Menu_Ext.setGravity(17);
		Menu_Ext.setTypeface(Typeface.SERIF);
        Menu_Ext.setText("Auto Skills");
        Menu_Ext.setTextColor(Color.parseColor("#FF000000"));
	
		Menu_Bad3.setShape(GradientDrawable.RECTANGLE);
		Menu_Bad3.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		int Menu_Bad3ADD[] = new int[]{ Color.parseColor("#BF4585C6"), Color.parseColor("#BF4585C6"), Color.parseColor("#BF4585C6"), Color.parseColor("#CADDEEFF") };
		Menu_Bad3.setColors(Menu_Bad3ADD);
		Menu_Bad3.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, 0, 0});
        Menu_Bad3.setStroke(2, Color.parseColor("#FF4F5154"));
		Menu_Bad3.setColor(Color.parseColor("#FFA6A6A6"));
		Menu_Ext.setBackground(Menu_Bad3); 
		

		Menu_Ext.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
					Menu_Bad.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad2.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad3.setColor(Color.parseColor("#FF7C7C7C"));
					Menu_Bad4.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad5.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad6.setColor(Color.parseColor("#FFA6A6A6"));				
					lin1.setVisibility(View.GONE);
					lin2.setVisibility(View.GONE);
					lin3.setVisibility(View.VISIBLE);
					lin4.setVisibility(View.GONE);
                    lin5.setVisibility(View.GONE);
					lin6.setVisibility(View.GONE);



                }
            });

		final TextView Menu_Fly = new TextView(context);
        Menu_Fly.setLayoutParams(Menu_Aim_Params);
        Menu_Fly.setTextSize(8.0f);
        Menu_Fly.setTextColor(-1);
        Menu_Fly.setGravity(17);
		Menu_Fly.setTypeface(Typeface.SERIF);
        Menu_Fly.setText("Auto Aim");
        Menu_Fly.setTextColor(Color.parseColor("#FF000000"));
	
		Menu_Bad4.setShape(GradientDrawable.RECTANGLE);
		Menu_Bad4.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		int Menu_Bad4ADD[] = new int[]{ Color.parseColor("#BF4585C6"), Color.parseColor("#BF4585C6"), Color.parseColor("#BF4585C6"), Color.parseColor("#CADDEEFF") };
		Menu_Bad4.setColors(Menu_Bad4ADD);
		Menu_Bad4.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, 0, 0});
        Menu_Bad4.setStroke(2, Color.parseColor("#FF4F5154"));
		Menu_Bad4.setColor(Color.parseColor("#FFA6A6A6"));
		Menu_Fly.setBackground(Menu_Bad4); 

		Menu_Fly.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
					Menu_Bad.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad2.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad3.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad4.setColor(Color.parseColor("#FF7C7C7C"));
					Menu_Bad5.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad6.setColor(Color.parseColor("#FFA6A6A6"));		
					lin1.setVisibility(View.GONE);
					lin2.setVisibility(View.GONE);
                    lin3.setVisibility(View.GONE);
					lin4.setVisibility(View.VISIBLE);
					lin5.setVisibility(View.GONE);
					lin6.setVisibility(View.GONE);

                }
            });

		final TextView Menu_Sex = new TextView(context);
        Menu_Sex.setLayoutParams(Menu_Aim_Params);
        Menu_Sex.setTextSize(8.0f);
        Menu_Sex.setTextColor(-1);
        Menu_Sex.setGravity(17);
		Menu_Sex.setTypeface(Typeface.SERIF);
        Menu_Sex.setText("Auto Retri");
        Menu_Sex.setTextColor(Color.parseColor("#FF000000"));
		
		
		Menu_Bad5.setShape(GradientDrawable.RECTANGLE);
		Menu_Bad5.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		int Menu_Bad5ADD[] = new int[]{ Color.parseColor("#BF4585C6"), Color.parseColor("#BF4585C6"), Color.parseColor("#BF4585C6"), Color.parseColor("#CADDEEFF") };
		Menu_Bad5.setColors(Menu_Bad5ADD);
		Menu_Bad5.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, 0, 0});
        Menu_Bad5.setStroke(2, Color.parseColor("#FF4F5154"));
		Menu_Bad5.setColor(Color.parseColor("#FFA6A6A6"));
		Menu_Sex.setBackground(Menu_Bad5); 

		Menu_Sex.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
					Menu_Bad.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad2.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad3.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad4.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad5.setColor(Color.parseColor("#FF7C7C7C"));
					Menu_Bad6.setColor(Color.parseColor("#FFA6A6A6"));			
					lin1.setVisibility(View.GONE);
					lin2.setVisibility(View.GONE);
                    lin3.setVisibility(View.GONE);
					lin4.setVisibility(View.GONE);
					lin5.setVisibility(View.VISIBLE);
					lin6.setVisibility(View.GONE);
					
					

                }

			});
		final TextView Menu_El = new TextView(context);
        Menu_El.setLayoutParams(Menu_Aim_Params);
        Menu_El.setTextSize(8.0f);
        Menu_El.setTextColor(-1);
        Menu_El.setGravity(17);
		Menu_El.setTypeface(Typeface.SERIF);
        Menu_El.setText("Other Menu");
        Menu_El.setTextColor(Color.parseColor("#FF000000"));


		Menu_Bad6.setShape(GradientDrawable.RECTANGLE);
		Menu_Bad6.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		int Menu_Bad6ADD[] = new int[]{ Color.parseColor("#BF4585C6"), Color.parseColor("#BF4585C6"), Color.parseColor("#BF4585C6"), Color.parseColor("#CADDEEFF") };
		Menu_Bad6.setColors(Menu_Bad6ADD);
		Menu_Bad6.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, 0, 0});
        Menu_Bad6.setStroke(2, Color.parseColor("#FF4F5154"));
		Menu_Bad6.setColor(Color.parseColor("#FFA6A6A6"));
		Menu_El.setBackground(Menu_Bad6);

		Menu_El.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
					Menu_Bad.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad2.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad3.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad4.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad5.setColor(Color.parseColor("#FFA6A6A6"));
					Menu_Bad6.setColor(Color.parseColor("#FF7C7C7C"));
					lin1.setVisibility(View.GONE);
					lin2.setVisibility(View.GONE);
                    lin3.setVisibility(View.GONE);
					lin4.setVisibility(View.GONE);
					lin5.setVisibility(View.GONE);
					lin6.setVisibility(View.VISIBLE);
					

                }

			});
			
			
		BadCategory.addView(Menu_Aim);
		BadCategory.addView(Menu_Esp);
		BadCategory.addView(Menu_Ext);
		BadCategory.addView(Menu_Fly);
		BadCategory.addView(Menu_Sex);
		//BadCategory.addView(Menu_El);

        //********** Adding view components **********
        mRootContainer.addView(mCollapsed);
        rootFrame.addView(mExpanded);
        if (IconWebViewData() != null) {
            mCollapsed.addView(wView);
        } else {
            mCollapsed.addView(startimage);
        }
        //titleText.addView(title);
       // titleText.addView(settings);
        mExpanded.addView(titleText);
        titleText.addView(subTitle);
        mExpanded.addView(BadCategory);
        scrollView.addView(mods);
        mExpanded.addView(scrollView);
        relativeLayout.addView(hideBtn);
        relativeLayout.addView(closeBtn);
        mExpanded.addView(relativeLayout);

        Init(context, title, subTitle);
    }

    public void ShowMenu() {
        rootFrame.addView(mRootContainer);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            boolean viewLoaded = false;

            @Override
            public void run() {
                //If the save preferences is enabled, it will check if game lib is loaded before starting menu
                //Comment the if-else code out except startService if you want to run the app and test preferences
                if (Preferences.loadPref && !IsGameLibLoaded() && !stopChecking) {
                    if (!viewLoaded) {
                        Category(mods, "Dui Evo+ Lib are online, please wait to be loaded");
                        Button(mods, -100, "Continue to play game");
                        viewLoaded = true;
                    }
                    handler.postDelayed(this, 600);
                } else {
                    mods.removeAllViews();
                    featureList(GetFeatureList(), mods);
                    mods.addView(lin1);
					mods.addView(lin2);
					mods.addView(lin3);
					mods.addView(lin4);
					mods.addView(lin5);
					mods.addView(lin6);
                }
            }
        }, 500);
    }

    @SuppressLint("WrongConstant")
    public void SetWindowManagerWindowService() {
        //Variable to check later if the phone supports Draw over other apps permission
        int iparams = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ? 2038 : 2002;
        vmParams = new WindowManager.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, iparams, 8, -3);
        //params = new WindowManager.LayoutParams(WindowManager.LayoutParams.LAST_APPLICATION_WINDOW, 8, -3);
        vmParams.gravity = 51;
        vmParams.x = POS_X;
        vmParams.y = POS_Y;

        mWindowManager = (WindowManager) getContext.getSystemService(getContext.WINDOW_SERVICE);
        mWindowManager.addView(rootFrame, vmParams);

        overlayRequired = true;
    }

    @SuppressLint("WrongConstant")
    public void SetWindowManagerActivity() {
        vmParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                POS_X,//initialX
                POS_Y,//initialy
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
                PixelFormat.TRANSPARENT
        );
        vmParams.gravity = 51;
        vmParams.x = POS_X;
        vmParams.y = POS_Y;

        mWindowManager = ((Activity) getContext).getWindowManager();
        mWindowManager.addView(rootFrame, vmParams);
    }

    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {
            final View collapsedView = mCollapsed;
            final View expandedView = mExpanded;
            private float initialTouchX, initialTouchY;
            private int initialX, initialY;

            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = vmParams.x;
                        initialY = vmParams.y;
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int rawX = (int) (motionEvent.getRawX() - initialTouchX);
                        int rawY = (int) (motionEvent.getRawY() - initialTouchY);
                        mExpanded.setAlpha(1f);
                        mCollapsed.setAlpha(1f);
                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (rawX < 10 && rawY < 10 && isViewCollapsed()) {
                            //When user clicks on the image view of the collapsed layout,
                            //visibility of the collapsed layout will be changed to "View.GONE"
                            //and expanded view will become visible.
                            try {
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            } catch (NullPointerException e) {

                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        mExpanded.setAlpha(0.5f);
                        mCollapsed.setAlpha(0.5f);
                        //Calculate the X and Y coordinates of the view.
                        vmParams.x = initialX + ((int) (motionEvent.getRawX() - initialTouchX));
                        vmParams.y = initialY + ((int) (motionEvent.getRawY() - initialTouchY));
                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(rootFrame, vmParams);
                        return true;
                    default:
                        return false;
                }
            }
        };
    }

    private void featureList(String[] listFT, LinearLayout linearLayout) {
        //Currently looks messy right now. Let me know if you have improvements
        int featNum, subFeat = 0;
        LinearLayout llBak = linearLayout;

        for (int i = 0; i < listFT.length; i++) {
            boolean switchedOn = false;
            //Log.i("featureList", listFT[i]);
            String feature = listFT[i];
            if (feature.contains("_True")) {
                switchedOn = true;
                feature = feature.replaceFirst("_True", "");
            }

            linearLayout = llBak;
            if (feature.contains("CollapseAdd_")) {
                //if (collapse != null)
                linearLayout = mCollapse;
                feature = feature.replaceFirst("CollapseAdd_", "");
            }
            String[] str = feature.split("_");

            //Assign feature number
            if (TextUtils.isDigitsOnly(str[0]) || str[0].matches("-[0-9]*")) {
                featNum = Integer.parseInt(str[0]);
                feature = feature.replaceFirst(str[0] + "_", "");
                subFeat++;
            } else {
                //Subtract feature number. We don't want to count ButtonLink, Category, RichTextView and RichWebView
                featNum = i - subFeat;
            }
            
            ///line 1
            String[] strSplit = feature.split("_");
            switch (strSplit[0]) {
                case "Toggle1":
                    Switch(lin1, featNum, strSplit[1], switchedOn);
                    break;
                case "SeekBar1":
                    SeekBar(lin1, featNum, strSplit[1], Integer.parseInt(strSplit[2]), Integer.parseInt(strSplit[3]));
                    break;
                case "Button1":
                    Button(lin1, featNum, strSplit[1]);
                    break;
                case "ButtonOnOff1":
                    ButtonOnOff(lin1, featNum, strSplit[1], switchedOn);
                    break;
                case "Spinner1":
                    TextView(lin1, strSplit[1]);
                    Spinner(lin1, featNum, strSplit[1], strSplit[2]);
                    break;
                case "InputText1":
                    InputText(lin1, featNum, strSplit[1]);
                    break;
                case "InputValue1":
                    if (strSplit.length == 3)
                        InputNum(lin1, featNum, strSplit[2], Integer.parseInt(strSplit[1]));
                    if (strSplit.length == 2)
                        InputNum(lin1, featNum, strSplit[1], 0);
                    break;
                case "CheckBox1":
                    CheckBox(lin1, featNum, strSplit[1], switchedOn);
                    break;
                case "RadioButton1":
                    RadioButton(lin1, featNum, strSplit[1], strSplit[2]);
                    break;
                case "Collapse1":
                    Collapse(lin1, strSplit[1], switchedOn);
                    subFeat++;
                    break;
                case "ButtonLink1":
                    subFeat++;
                    ButtonLink(lin1, strSplit[1], strSplit[2]);
                    break;
                case "Category1":
                    subFeat++;
                    Category(lin1, strSplit[1]);
                    break;
                case "RichTextView1":
                    subFeat++;
                    TextView(lin1, strSplit[1]);
                    break;
                case "RichWebView1":
                    subFeat++;
                    WebTextView(lin1, strSplit[1]);
                    break;
            }
            
            ///line 2
            switch (strSplit[0]) {
                case "Toggle2":
                    Switch(lin2, featNum, strSplit[1], switchedOn);
                    break;
                case "SeekBar2":
                    SeekBar(lin2, featNum, strSplit[1], Integer.parseInt(strSplit[2]), Integer.parseInt(strSplit[3]));
                    break;
                case "Button2":
                    Button(lin2, featNum, strSplit[1]);
                    break;
                case "ButtonOnOff2":
                    ButtonOnOff(lin2, featNum, strSplit[1], switchedOn);
                    break;
                case "Spinner2":
                    TextView(lin2, strSplit[1]);
                    Spinner(lin2, featNum, strSplit[1], strSplit[2]);
                    break;
                case "InputText2":
                    InputText(lin2, featNum, strSplit[1]);
                    break;
                case "InputValue2":
                    if (strSplit.length == 3)
                        InputNum(lin2, featNum, strSplit[2], Integer.parseInt(strSplit[1]));
                    if (strSplit.length == 2)
                        InputNum(lin2, featNum, strSplit[1], 0);
                    break;
                case "CheckBox2":
                    CheckBox(lin2, featNum, strSplit[1], switchedOn);
                    break;
                case "RadioButton2":
                    RadioButton(lin2, featNum, strSplit[1], strSplit[2]);
                    break;
                case "Collapse2":
                    Collapse(lin2, strSplit[1], switchedOn);
                    subFeat++;
                    break;
                case "ButtonLink2":
                    subFeat++;
                    ButtonLink(lin2, strSplit[1], strSplit[2]);
                    break;
                case "Category2":
                    subFeat++;
                    Category(lin2, strSplit[1]);
                    break;
                case "RichTextView2":
                    subFeat++;
                    TextView(lin2, strSplit[1]);
                    break;
                case "RichWebView2":
                    subFeat++;
                    WebTextView(lin2, strSplit[1]);
                    break;
            }
            ///line 3
            switch (strSplit[0]) {
                case "Toggle3":
                    Switch(lin3, featNum, strSplit[1], switchedOn);
                    break;
                case "SeekBar3":
                    SeekBar(lin3, featNum, strSplit[1], Integer.parseInt(strSplit[2]), Integer.parseInt(strSplit[3]));
                    break;
                case "Button3":
                    Button(lin3, featNum, strSplit[1]);
                    break;
                case "ButtonOnOff3":
                    ButtonOnOff(lin3, featNum, strSplit[1], switchedOn);
                    break;
                case "Spinner3":
                    TextView(lin3, strSplit[1]);
                    Spinner(lin3, featNum, strSplit[1], strSplit[2]);
                    break;
                case "InputText3":
                    InputText(lin3, featNum, strSplit[1]);
                    break;
                case "InputValue3":
                    if (strSplit.length == 3)
                        InputNum(lin3, featNum, strSplit[2], Integer.parseInt(strSplit[1]));
                    if (strSplit.length == 2)
                        InputNum(lin3, featNum, strSplit[1], 0);
                    break;
                case "CheckBox3":
                    CheckBox(lin3, featNum, strSplit[1], switchedOn);
                    break;
                case "RadioButton3":
                    RadioButton(lin3, featNum, strSplit[1], strSplit[2]);
                    break;
                case "Collapse3":
                    Collapse(lin3, strSplit[1], switchedOn);
                    subFeat++;
                    break;
                case "ButtonLink3":
                    subFeat++;
                    ButtonLink(lin3, strSplit[1], strSplit[2]);
                    break;
                case "Category3":
                    subFeat++;
                    Category(lin3, strSplit[1]);
                    break;
                case "RichTextView3":
                    subFeat++;
                    TextView(lin3, strSplit[1]);
                    break;
                case "RichWebView3":
                    subFeat++;
                    WebTextView(lin3, strSplit[1]);
                    break;
            }
            
            ///line 4
            switch (strSplit[0]) {
                case "Toggle4":
                    Switch(lin4, featNum, strSplit[1], switchedOn);
                    break;
                case "SeekBar4":
                    SeekBar(lin4, featNum, strSplit[1], Integer.parseInt(strSplit[2]), Integer.parseInt(strSplit[3]));
                    break;
                case "Button4":
                    Button(lin4, featNum, strSplit[1]);
                    break;
                case "ButtonOnOff4":
                    ButtonOnOff(lin4, featNum, strSplit[1], switchedOn);
                    break;
                case "Spinner4":
                    TextView(lin4, strSplit[1]);
                    Spinner(lin4, featNum, strSplit[1], strSplit[2]);
                    break;
                case "InputText4":
                    InputText(lin4, featNum, strSplit[1]);
                    break;
                case "InputValue4":
                    if (strSplit.length == 3)
                        InputNum(lin4, featNum, strSplit[2], Integer.parseInt(strSplit[1]));
                    if (strSplit.length == 2)
                        InputNum(lin4, featNum, strSplit[1], 0);
                    break;
                case "CheckBox4":
                    CheckBox(lin4, featNum, strSplit[1], switchedOn);
                    break;
                case "RadioButton4":
                    RadioButton(lin4, featNum, strSplit[1], strSplit[2]);
                    break;
                case "Collapse4":
                    Collapse(lin4, strSplit[1], switchedOn);
                    subFeat++;
                    break;
                case "ButtonLink4":
                    subFeat++;
                    ButtonLink(lin4, strSplit[1], strSplit[2]);
                    break;
                case "Category4":
                    subFeat++;
                    Category(lin4, strSplit[1]);
                    break;
                case "RichTextView4":
                    subFeat++;
                    TextView(lin4, strSplit[1]);
                    break;
                case "RichWebView4":
                    subFeat++;
                    WebTextView(lin4, strSplit[1]);
                    break;
            }
            
            ///line 5
          
            switch (strSplit[0]) {
                case "Toggle5":
                    Switch(lin5, featNum, strSplit[1], switchedOn);
                    break;
                case "SeekBar5":
                    SeekBar(lin5, featNum, strSplit[1], Integer.parseInt(strSplit[2]), Integer.parseInt(strSplit[3]));
                    break;
                case "Button5":
                    Button(lin5, featNum, strSplit[1]);
                    break;
                case "ButtonOnOff5":
                    ButtonOnOff(lin5, featNum, strSplit[1], switchedOn);
                    break;
                case "Spinner5":
                    TextView(lin5, strSplit[1]);
                    Spinner(lin5, featNum, strSplit[1], strSplit[2]);
                    break;
                case "InputText5":
                    InputText(lin5, featNum, strSplit[1]);
                    break;
                case "InputValue5":
                    if (strSplit.length == 3)
                        InputNum(lin5, featNum, strSplit[2], Integer.parseInt(strSplit[1]));
                    if (strSplit.length == 2)
                        InputNum(lin5, featNum, strSplit[1], 0);
                    break;
                case "CheckBox5":
                    CheckBox(lin5, featNum, strSplit[1], switchedOn);
                    break;
                case "RadioButton5":
                    RadioButton(lin5, featNum, strSplit[1], strSplit[2]);
                    break;
                case "Collapse5":
                    Collapse(lin5, strSplit[1], switchedOn);
                    subFeat++;
                    break;
                case "ButtonLink5":
                    subFeat++;
                    ButtonLink(lin5, strSplit[1], strSplit[2]);
                    break;
                case "Category5":
                    subFeat++;
                    Category(lin5, strSplit[1]);
                    break;
                case "RichTextView5":
                    subFeat++;
                    TextView(lin5, strSplit[1]);
                    break;
                case "RichWebView5":
                    subFeat++;
                    WebTextView(lin5, strSplit[1]);
                    break;
            }
            
            ///line 6
            switch (strSplit[0]) {
                case "Toggle6":
                    Switch(lin6, featNum, strSplit[1], switchedOn);
                    break;
                case "SeekBar6":
                    SeekBar(lin6, featNum, strSplit[1], Integer.parseInt(strSplit[2]), Integer.parseInt(strSplit[3]));
                    break;
                case "Button6":
                    Button(lin6, featNum, strSplit[1]);
                    break;
                case "ButtonOnOff6":
                    ButtonOnOff(lin6, featNum, strSplit[1], switchedOn);
                    break;
                case "Spinner6":
                    TextView(lin6, strSplit[1]);
                    Spinner(lin6, featNum, strSplit[1], strSplit[2]);
                    break;
                case "InputText6":
                    InputText(lin6, featNum, strSplit[1]);
                    break;
                case "InputValue6":
                    if (strSplit.length == 3)
                        InputNum(lin6, featNum, strSplit[2], Integer.parseInt(strSplit[1]));
                    if (strSplit.length == 2)
                        InputNum(lin6, featNum, strSplit[1], 0);
                    break;
                case "CheckBox6":
                    CheckBox(lin6, featNum, strSplit[1], switchedOn);
                    break;
                case "RadioButton6":
                    RadioButton(lin6, featNum, strSplit[1], strSplit[2]);
                    break;
                case "Collapse6":
                    Collapse(lin6, strSplit[1], switchedOn);
                    subFeat++;
                    break;
                case "ButtonLink6":
                    subFeat++;
                    ButtonLink(lin6, strSplit[1], strSplit[2]);
                    break;
                case "Category6":
                    subFeat++;
                    Category(lin6, strSplit[1]);
                    break;
                case "RichTextView6":
                    subFeat++;
                    TextView(lin6, strSplit[1]);
                    break;
                case "RichWebView6":
                    subFeat++;
                    WebTextView(lin6, strSplit[1]);
                    break;
            }
        }
    }
    
    private void Switch(LinearLayout linLayout, final int featNum, final String featName, boolean swiOn) {
        final Switch switchR = new Switch(getContext);
        ColorStateList buttonStates = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        Color.BLUE,
                        Color.parseColor("#FFBBBBBB"), // ON
                        Color.parseColor("#FF2F3D4C"), // OFF
                }
        );
        //Set colors of the switch. Comment out if you don't like it
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switchR.getThumbDrawable().setTintList(buttonStates);
          //  switchR.getTrackDrawable().setTintList(buttonStates);
        }
        ColorStateList buttonStatess = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        Color.BLUE,
                        Color.parseColor("#FF000000"), // ON
                        Color.parseColor("#FFDBDBDB"), // OFF
                }
        );
        //Set colors of the switch. Comment out if you don't like it
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //switchR.getThumbDrawable().setTintList(buttonStates);
            switchR.getTrackDrawable().setTintList(buttonStatess);
        }
      
      
       GradientDrawable draikeCircle = new GradientDrawable();
        draikeCircle.setShape(GradientDrawable.RECTANGLE);
       // draikeCircle.setColor(Color.parseColor("#FF7C7C7C"));
       // draikeCircle.setStroke(dp(), Color.parseColor("#FF4F5154"));
       // draikeCircle.setCornerRadius(5.0f);
        draikeCircle.setSize(dp(21), dp(1));
        draikeCircle.setStroke(2, Color.parseColor("#FFF1F1F1"));
        draikeCircle.setColor(buttonStates);
        
        
		
        
    /*    GradientDrawable siwam= new  GradientDrawable();
        siwam.setSize(50,50);
        siwam.setShape(1);    
       siwam.setStroke(6, Color.parseColor("#FFFFFFFF"));
        siwam.setCornerRadius(100);
       // siwam.setPadding(5,5,5,5);
        siwam.setColor(buttonStates);
        
        final GradientDrawable sk1 = new GradientDrawable();
        sk1.setSize(50,50);
        sk1.setShape(2);
        sk1.setStroke(2, Color.BLACK);
       // sk1.setPadding(5,5,5,5);
        //sk1.setColor(buttonStatess);
        sk1.setCornerRadius(100);
        */
        
        switchR.setThumbDrawable(draikeCircle);
       //switchR.setTrackDrawable(seekbarCircle2);
       // switchR.getThumbDrawable().setTintList(buttonStates);
       //switchR.getTrackDrawable().setTintList(buttonStatess);
       android.graphics.drawable.GradientDrawable yameteq = new android.graphics.drawable.GradientDrawable();
        yameteq.setColor(Color.parseColor("#FFA6A6A6"));
	    yameteq.setStroke(2, Color.parseColor("#FF4F5154"));
        yameteq.setCornerRadii(new float[]{15, 15, 15, 15, 15, 15, 15, 15});
        switchR.setBackground(yameteq);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        layoutParams.setMargins(10, 15, 10, 15);
        switchR.setLayoutParams(layoutParams);
        switchR.setTextColor(Color.parseColor("#2F3D4C"));
        switchR.setTypeface(Typeface.SERIF);
        switchR.setTextSize(8);
        switchR.setText(featName);
       // switchR.setShadowLayer(8,0,0,Color.parseColor("#FFFFFFFF"));
        switchR.setPadding(10, 10, 10, 10);
        switchR.setChecked(Preferences.loadPrefBool(featName, featNum, swiOn));
        switchR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean bool) {
                Preferences.changeFeatureBool(featName, featNum, bool);
                switch (featNum) {
                    case -1: //Save perferences
                        Preferences.with(switchR.getContext()).writeBoolean(-1, bool);
                        if (bool == false)
                            Preferences.with(switchR.getContext()).clear(); //Clear perferences if switched off
                        break;
                    case -3:
                        Preferences.isExpanded = bool;
                        scrollView.setLayoutParams(bool ? scrlLLExpanded : scrlLL);
                        break;
                }
            }
        });

        linLayout.addView(switchR);
    }

    private void SeekBar(LinearLayout linLayout, final int featNum, final String featName, final int min, int max) {
        int loadedProg = Preferences.loadPrefInt(featName, featNum);
        LinearLayout linearLayout = new LinearLayout(getContext);
        linearLayout.setPadding(5, 15, 5, 15);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);

        final TextView textView = new TextView(getContext);
        textView.setText(Html.fromHtml(featName + ": <font color='" + RadioColor + "'>" + min + "</font>"));
        textView.setTextColor(TEXT_COLOR_2);
        textView.setTypeface(Typeface.SERIF);
        
        

        SeekBar seekBar = new SeekBar(getContext);
        GradientDrawable seekbarCircle = new GradientDrawable();
        seekbarCircle.setShape(GradientDrawable.RECTANGLE);
        seekbarCircle.setColor(Color.parseColor("#FF7C7C7C"));
        seekbarCircle.setStroke(dp(0), Color.parseColor("#FF4F5154"));
        seekbarCircle.setCornerRadius(5.0f);
        seekbarCircle.setSize(dp(12), dp(2));
		seekBar.setBackground(null);
        
		
        seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#FFA6A6A6"), PorterDuff.Mode.SRC_IN);
        seekBar.setPadding(10, 15, 10, 15);
        seekBar.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        seekBar.setMax(max);
		seekBar.setScaleY(10f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            seekBar.setMin(min); //setMin for Oreo and above
        seekBar.setProgress((loadedProg == 0) ? min : loadedProg);
        seekBar.getThumb().setColorFilter(SeekBarColor, PorterDuff.Mode.SRC_ATOP);
        seekBar.getProgressDrawable().setColorFilter(SeekBarProgressColor, PorterDuff.Mode.SRC_ATOP);
        seekBar.setThumb(seekbarCircle);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                //if progress is greater than minimum, don't go below. Else, set progress
                seekBar.setProgress(i < min ? min : i);
                Preferences.changeFeatureInt(featName, featNum, i < min ? min : i);
                textView.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + (i < min ? min : i)));
            }
        });
        linearLayout.addView(textView);
        linearLayout.addView(seekBar);

        linLayout.addView(linearLayout);
    }

    private void Button(LinearLayout linLayout, final int featNum, final String featName) {
        final Button button = new Button(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(7, 5, 7, 5);
        button.setLayoutParams(layoutParams);
        button.setTextColor(TEXT_COLOR_2);
        button.setAllCaps(false); //Disable caps to support html
        button.setText(Html.fromHtml(featName));
        button.setBackgroundColor(BTN_COLOR);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (featNum) {

                    case -6:
                        scrollView.removeView(mSettings);
                        scrollView.addView(mods);
                        break;
                    case -100:
                        stopChecking = true;
                        break;
                }
                Preferences.changeFeatureInt(featName, featNum, 0);
            }
        });

        linLayout.addView(button);
    }

    private void ButtonLink(LinearLayout linLayout, final String featName, final String url) {
        final Button button = new Button(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(7, 5, 7, 5);
        button.setLayoutParams(layoutParams);
        button.setAllCaps(false); //Disable caps to support html
        button.setTextColor(TEXT_COLOR_2);
        button.setText(Html.fromHtml(featName));
        button.setBackgroundColor(BTN_COLOR);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(url));
                getContext.startActivity(intent);
            }
        });
        linLayout.addView(button);
    }

    private void ButtonOnOff(LinearLayout linLayout, final int featNum, String featName, boolean switchedOn) {
        final Button button = new Button(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(7, 5, 7, 5);
        button.setLayoutParams(layoutParams);
        button.setTextColor(TEXT_COLOR_2);
        button.setAllCaps(false); //Disable caps to support html

        final String finalfeatName = featName.replace("OnOff_", "");
        boolean isOn = Preferences.loadPrefBool(featName, featNum, switchedOn);
        if (isOn) {
            button.setText(Html.fromHtml(finalfeatName + ": ON"));
            button.setBackgroundColor(BtnON);
            isOn = false;
        } else {
            button.setText(Html.fromHtml(finalfeatName + ": OFF"));
            button.setBackgroundColor(BtnOFF);
            isOn = true;
        }
        final boolean finalIsOn = isOn;
        button.setOnClickListener(new View.OnClickListener() {
            boolean isOn = finalIsOn;

            public void onClick(View v) {
                Preferences.changeFeatureBool(finalfeatName, featNum, isOn);
                //Log.d(TAG, finalfeatName + " " + featNum + " " + isActive2);
                if (isOn) {
                    button.setText(Html.fromHtml(finalfeatName + ": ON"));
                    button.setBackgroundColor(BtnON);
                    isOn = false;
                } else {
                    button.setText(Html.fromHtml(finalfeatName + ": OFF"));
                    button.setBackgroundColor(BtnOFF);
                    isOn = true;
                }
            }
        });
        linLayout.addView(button);
    }

    private void Spinner(LinearLayout linLayout, final int featNum, final String featName, final String list) {
        Log.d(TAG, "spinner " + featNum + " " + featName + " " + list);
        final List<String> lists = new LinkedList<>(Arrays.asList(list.split(",")));

        // Create another LinearLayout as a workaround to use it as a background
        // to keep the down arrow symbol. No arrow symbol if setBackgroundColor set
        LinearLayout linearLayout2 = new LinearLayout(getContext);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        layoutParams2.setMargins(7, 2, 7, 2);
        linearLayout2.setOrientation(LinearLayout.VERTICAL);
        linearLayout2.setBackgroundColor(BTN_COLOR);
        linearLayout2.setLayoutParams(layoutParams2);

        final Spinner spinner = new Spinner(getContext, Spinner.MODE_DROPDOWN);
        spinner.setLayoutParams(layoutParams2);
        spinner.getBackground().setColorFilter(1, PorterDuff.Mode.SRC_ATOP); //trick to show white down arrow color
        //Creating the ArrayAdapter instance having the list
        ArrayAdapter aa = new ArrayAdapter(getContext, android.R.layout.simple_spinner_dropdown_item, lists);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner'
        spinner.setAdapter(aa);
        spinner.setSelection(Preferences.loadPrefInt(featName, featNum));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Preferences.changeFeatureInt(spinner.getSelectedItem().toString(), featNum, position);
                ((TextView) parentView.getChildAt(0)).setTextColor(TEXT_COLOR_2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        linearLayout2.addView(spinner);
        linLayout.addView(linearLayout2);
    }

    private void InputNum(LinearLayout linLayout, final int featNum, final String featName, final int maxValue) {
        LinearLayout linearLayout = new LinearLayout(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(7, 5, 7, 5);

        final Button button = new Button(getContext);
        int num = Preferences.loadPrefInt(featName, featNum);
        button.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + ((num == 0) ? 1 : num) + "</font>"));
        button.setAllCaps(false);
        button.setLayoutParams(layoutParams);
        button.setBackgroundColor(BTN_COLOR);
        button.setTextColor(TEXT_COLOR_2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertName = new AlertDialog.Builder(getContext);
                final EditText editText = new EditText(getContext);
                if (maxValue != 0)
                    editText.setHint("Max value: " + maxValue);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789-"));
                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(10);
                editText.setFilters(FilterArray);
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        InputMethodManager imm = (InputMethodManager) getContext.getSystemService(getContext.INPUT_METHOD_SERVICE);
                        if (hasFocus) {
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        } else {
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        }
                    }
                });
                editText.requestFocus();

                alertName.setTitle("Input number");
                alertName.setView(editText);
                LinearLayout layoutName = new LinearLayout(getContext);
                layoutName.setOrientation(LinearLayout.VERTICAL);
                layoutName.addView(editText); // displays the user input bar
                alertName.setView(layoutName);

                alertName.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int num;
                        try {
                            num = Integer.parseInt(TextUtils.isEmpty(editText.getText().toString()) ? "0" : editText.getText().toString());
                            if (maxValue != 0 && num >= maxValue)
                                num = maxValue;
                        } catch (NumberFormatException ex) {
                            if (maxValue != 0)
                                num = maxValue;
                            else
                                num = 2147483640;
                        }

                        button.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + num + "</font>"));
                        Preferences.changeFeatureInt(featName, featNum, num);

                        editText.setFocusable(false);
                    }
                });

                alertName.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // dialog.cancel(); // closes dialog
                        InputMethodManager imm = (InputMethodManager) getContext.getSystemService(getContext.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });

                if (overlayRequired) {
                    AlertDialog dialog = alertName.create(); // display the dialog
                    dialog.getWindow().setType(Build.VERSION.SDK_INT >= 26 ? 2038 : 2002);
                    dialog.show();
                } else {
                    alertName.show();
                }
            }
        });

        linearLayout.addView(button);
        linLayout.addView(linearLayout);
    }

    private void InputText(LinearLayout linLayout, final int featNum, final String featName) {
        LinearLayout linearLayout = new LinearLayout(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(7, 5, 7, 5);

        final Button button = new Button(getContext);

        String string = Preferences.loadPrefString(featName, featNum);
        button.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + string + "</font>"));

        button.setAllCaps(false);
        button.setLayoutParams(layoutParams);
        button.setBackgroundColor(BTN_COLOR);
        button.setTextColor(TEXT_COLOR_2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertName = new AlertDialog.Builder(getContext);

                final EditText editText = new EditText(getContext);
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        InputMethodManager imm = (InputMethodManager) getContext.getSystemService(getContext.INPUT_METHOD_SERVICE);
                        if (hasFocus) {
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        } else {
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        }
                    }
                });
                editText.requestFocus();

                alertName.setTitle("Input text");
                alertName.setView(editText);
                LinearLayout layoutName = new LinearLayout(getContext);
                layoutName.setOrientation(LinearLayout.VERTICAL);
                layoutName.addView(editText); // displays the user input bar
                alertName.setView(layoutName);

                alertName.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String str = editText.getText().toString();
                        button.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + str + "</font>"));
                        Preferences.changeFeatureString(featName, featNum, str);
                        editText.setFocusable(false);
                    }
                });

                alertName.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //dialog.cancel(); // closes dialog
                        InputMethodManager imm = (InputMethodManager) getContext.getSystemService(getContext.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });


                if (overlayRequired) {
                    AlertDialog dialog = alertName.create(); // display the dialog
                    dialog.getWindow().setType(Build.VERSION.SDK_INT >= 26 ? 2038 : 2002);
                    dialog.show();
                } else {
                    alertName.show();
                }
            }
        });

        linearLayout.addView(button);
        linLayout.addView(linearLayout);
    }

    private void CheckBox(LinearLayout linLayout, final int featNum, final String featName, boolean switchedOn) {
        final CheckBox checkBox = new CheckBox(getContext);
        checkBox.setText(featName);
        checkBox.setTextColor(TEXT_COLOR_2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            checkBox.setButtonTintList(ColorStateList.valueOf(CheckBoxColor));
        checkBox.setChecked(Preferences.loadPrefBool(featName, featNum, switchedOn));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox.isChecked()) {
                    Preferences.changeFeatureBool(featName, featNum, isChecked);
                } else {
                    Preferences.changeFeatureBool(featName, featNum, isChecked);
                }
            }
        });
        linLayout.addView(checkBox);
    }

    private void RadioButton(LinearLayout linLayout, final int featNum, String featName, final String list) {
        //Credit: LoraZalora
        final List<String> lists = new LinkedList<>(Arrays.asList(list.split(",")));

        final TextView textView = new TextView(getContext);
        textView.setText(featName + ":");
        textView.setTextColor(Color.parseColor("#FF000000"));
        textView.setTextSize(8);
        textView.setTypeface(Typeface.SERIF);

        final RadioGroup radioGroup = new RadioGroup(getContext);
        radioGroup.setPadding(20, 5, 10, 5);
        radioGroup.setOrientation(LinearLayout.VERTICAL);
        radioGroup.addView(textView);

        for (int i = 0; i < lists.size(); i++) {
            final RadioButton Radioo = new RadioButton(getContext);
            final String finalfeatName = featName, radioName = lists.get(i);
            View.OnClickListener first_radio_listener = new View.OnClickListener() {
                public void onClick(View v) {
                    textView.setText(Html.fromHtml(finalfeatName + ": <font color='" + NumberTxtColor + "'>" + radioName));
                    Preferences.changeFeatureInt(finalfeatName, featNum, radioGroup.indexOfChild(Radioo));
                }
            };
            System.out.println(lists.get(i));
            Radioo.setText(lists.get(i));
            Radioo.setTextColor(CategoryBG);
            Radioo.setTypeface(Typeface.SERIF);
            Radioo.setTextSize(8);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                Radioo.setButtonTintList(ColorStateList.valueOf(RadioColor));
            Radioo.setOnClickListener(first_radio_listener);
            radioGroup.addView(Radioo);
        }

        int index = Preferences.loadPrefInt(featName, featNum);
        if (index > 0) { //Preventing it to get an index less than 1. below 1 = null = crash
            textView.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + lists.get(index - 1)));
            ((RadioButton) radioGroup.getChildAt(index)).setChecked(true);
        }
        linLayout.addView(radioGroup);
    }

    private void Collapse(LinearLayout linLayout, final String text, final boolean expanded) {
        LinearLayout.LayoutParams layoutParamsLL = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParamsLL.setMargins(0, 5, 0, 0);

        LinearLayout collapse = new LinearLayout(getContext);
        collapse.setLayoutParams(layoutParamsLL);
        collapse.setVerticalGravity(16);
        collapse.setOrientation(LinearLayout.VERTICAL);

        final LinearLayout collapseSub = new LinearLayout(getContext);
        collapseSub.setVerticalGravity(16);
        collapseSub.setPadding(0, 5, 0, 5);
        collapseSub.setOrientation(LinearLayout.VERTICAL);
        collapseSub.setBackgroundColor(Color.parseColor("#222D38"));
        collapseSub.setVisibility(View.GONE);
        mCollapse = collapseSub;

        final TextView textView = new TextView(getContext);
        textView.setBackgroundColor(CategoryBG);
        textView.setText("▽ " + text + " ▽");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(TEXT_COLOR_2);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(0, 20, 0, 20);

        if (expanded) {
            collapseSub.setVisibility(View.VISIBLE);
            textView.setText("△ " + text + " △");
        }

        textView.setOnClickListener(new View.OnClickListener() {
            boolean isChecked = expanded;

            @Override
            public void onClick(View v) {

                boolean z = !isChecked;
                isChecked = z;
                if (z) {
                    collapseSub.setVisibility(View.VISIBLE);
                    textView.setText("△ " + text + " △");
                    return;
                }
                collapseSub.setVisibility(View.GONE);
                textView.setText("▽ " + text + " ▽");
            }
        });
        collapse.addView(textView);
        collapse.addView(collapseSub);
        linLayout.addView(collapse);
    }

    private void Category(LinearLayout linLayout, String text) {
        TextView textView = new TextView(getContext);
        textView.setBackgroundColor(CategoryBG);
        textView.setText(Html.fromHtml(text));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(TEXT_COLOR_2);
        textView.setTypeface(Typeface.SERIF);
        textView.setPadding(0, 5, 0, 5);
        linLayout.addView(textView);
    }

    private void TextView(LinearLayout linLayout, String text) {
        TextView textView = new TextView(getContext);
        textView.setText(Html.fromHtml(text));
        textView.setTextColor(TEXT_COLOR_2);
        textView.setPadding(10, 5, 10, 5);
        linLayout.addView(textView);
    }

    private void WebTextView(LinearLayout linLayout, String text) {
        WebView wView = new WebView(getContext);
        wView.loadData(text, "text/html", "utf-8");
        wView.setBackgroundColor(0x00000000); //Transparent
        wView.setPadding(0, 5, 0, 5);
        wView.getSettings().setAppCacheEnabled(false);
        linLayout.addView(wView);
    }

    private boolean isViewCollapsed() {
        return rootFrame == null || mCollapsed.getVisibility() == View.VISIBLE;
    }

    //For our image a little converter
    private int convertDipToPixels(int i) {
        return (int) ((((float) i) * getContext.getResources().getDisplayMetrics().density) + 0.5f);
    }

    private int dp(int i) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) i, getContext.getResources().getDisplayMetrics());
    }

    public void setVisibility(int view) {
        if (rootFrame != null) {
            rootFrame.setVisibility(view);
        }
    }

    public void onDestroy() {
        if (rootFrame != null) {
            mWindowManager.removeView(rootFrame);
        }
    }
}
