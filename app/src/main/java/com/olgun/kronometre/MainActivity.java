package com.olgun.kronometre;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends Activity {
 private TextView clock;
 private Button start;
 private long elapsed=0L, startedAt=0L;
 private boolean running=false;
 private final Handler handler=new Handler(Looper.getMainLooper());
 private final Runnable ticker=new Runnable(){public void run(){updateClock();if(running)handler.postDelayed(this,100);}};
 private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
 private GradientDrawable bg(String hex){GradientDrawable d=new GradientDrawable();d.setColor(Color.parseColor(hex));d.setCornerRadius(dp(5));return d;}
 @Override public void onCreate(Bundle b){super.onCreate(b);if(b!=null){elapsed=b.getLong("elapsed");running=b.getBoolean("running");if(running)startedAt=SystemClock.elapsedRealtime();}buildUi();if(running)handler.post(ticker);}
 private Button button(String label,String color){Button b=new Button(this);b.setText(label);b.setTextColor(Color.BLACK);b.setTextSize(14);b.setAllCaps(false);b.setMinHeight(0);b.setMinimumHeight(0);b.setMinWidth(0);b.setMinimumWidth(0);b.setPadding(dp(3),0,dp(3),0);b.setBackground(bg(color));return b;}
 private void buildUi(){
 LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(Color.WHITE);
 LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(4),dp(2),dp(4),dp(2));
 clock=new TextView(this);clock.setTextColor(Color.BLACK);clock.setTextSize(20);clock.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);clock.setGravity(Gravity.CENTER);clock.setSingleLine(true);clock.setBackground(bg("#BDE9FF"));
 row.addView(clock,new LinearLayout.LayoutParams(0,dp(34),1.5f));
 start=button(running?"Duraklat":elapsed>0?"Devam Et":"Başlat","#B9E9B4");
 LinearLayout.LayoutParams a=new LinearLayout.LayoutParams(0,dp(34),1f);a.leftMargin=dp(4);row.addView(start,a);
 Button reset=button("Sıfırla","#FFBFC0");
 LinearLayout.LayoutParams b=new LinearLayout.LayoutParams(0,dp(34),1f);b.leftMargin=dp(4);row.addView(reset,b);
 start.setOnClickListener(v->{if(running){elapsed+=SystemClock.elapsedRealtime()-startedAt;running=false;handler.removeCallbacks(ticker);start.setText("Devam Et");}else{startedAt=SystemClock.elapsedRealtime();running=true;start.setText("Duraklat");handler.removeCallbacks(ticker);handler.post(ticker);}updateClock();});
 reset.setOnClickListener(v->{running=false;elapsed=0;handler.removeCallbacks(ticker);start.setText("Başlat");updateClock();});
 root.addView(row,new LinearLayout.LayoutParams(-1,-2));setContentView(root);updateClock();
 }
 private void updateClock(){if(clock==null)return;long ms=elapsed+(running?SystemClock.elapsedRealtime()-startedAt:0);long sec=ms/1000;clock.setText(String.format(Locale.ROOT,"%02d:%02d:%02d",sec/3600,(sec/60)%60,sec%60));}
 @Override protected void onSaveInstanceState(Bundle out){super.onSaveInstanceState(out);out.putLong("elapsed",elapsed+(running?SystemClock.elapsedRealtime()-startedAt:0));out.putBoolean("running",running);}
 @Override protected void onDestroy(){handler.removeCallbacks(ticker);super.onDestroy();}
}