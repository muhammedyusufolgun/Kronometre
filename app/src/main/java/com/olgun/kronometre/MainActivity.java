package com.olgun.kronometre;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
 private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
 private GradientDrawable bg(String c){GradientDrawable d=new GradientDrawable();d.setColor(Color.parseColor(c));d.setCornerRadius(dp(4));return d;}
 @Override public void onCreate(Bundle b){super.onCreate(b);
 LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(Color.WHITE);root.setPadding(dp(4),dp(2),dp(4),0);
 TextView info=new TextView(this);info.setText("Kronometre diğer uygulamaların üzerinde, durum çubuğunun altında gösterilir.");info.setTextColor(Color.BLACK);info.setTextSize(14);root.addView(info);
 Button show=new Button(this);show.setAllCaps(false);show.setText("Kronometreyi Göster");show.setTextColor(Color.BLACK);show.setBackground(bg("#B9E9B4"));root.addView(show);
 Button hide=new Button(this);hide.setAllCaps(false);hide.setText("Kronometreyi Kapat");hide.setTextColor(Color.BLACK);hide.setBackground(bg("#FFBFC0"));root.addView(hide);
 show.setOnClickListener(v->{if(!Settings.canDrawOverlays(this)){Intent i=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName()));startActivity(i);}else{startForegroundService(new Intent(this,OverlayService.class));finish();}});
 hide.setOnClickListener(v->stopService(new Intent(this,OverlayService.class)));
 setContentView(root);
 if(Settings.canDrawOverlays(this)){startForegroundService(new Intent(this,OverlayService.class));finish();}
 }
 @Override protected void onResume(){super.onResume();}
}