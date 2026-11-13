package com.olgun.kronometre;
import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.content.SharedPreferences;
import android.widget.*;
import java.util.Locale;

public class OverlayService extends Service {
 private WindowManager wm;private LinearLayout bar;private TextView clock;private Button toggle;
 private long elapsed=0,started=0;private boolean running=false;
 private Handler handler=new Handler(Looper.getMainLooper());

 private final Runnable ticker=new Runnable(){public void run(){update();if(running)handler.postDelayed(this,10);}};
 private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
 private GradientDrawable bg(String color){GradientDrawable d=new GradientDrawable();d.setColor(Color.parseColor(color));d.setCornerRadius(dp(4));return d;}
 @Override public void onCreate(){super.onCreate();NotificationManager nm=getSystemService(NotificationManager.class);nm.createNotificationChannel(new NotificationChannel("stopwatch","Kronometre",NotificationManager.IMPORTANCE_LOW));
 Intent open=new Intent(this,MainActivity.class);PendingIntent pi=PendingIntent.getActivity(this,0,open,PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
 Notification n=new Notification.Builder(this,"stopwatch").setContentTitle("Kronometre").setContentText("Ekran üstü kronometre çalışıyor").setSmallIcon(android.R.drawable.ic_media_play).setContentIntent(pi).setOngoing(true).build();
 startForeground(1,n);wm=(WindowManager)getSystemService(WINDOW_SERVICE);if(Settings.canDrawOverlays(this))showBar();else stopSelf();}
 private Button button(String text,String color){Button b=new Button(this);b.setText(text);b.setTextColor(Color.BLACK);b.setAllCaps(false);b.setTextSize(12);b.setPadding(dp(3),0,dp(3),0);b.setMinHeight(0);b.setMinimumHeight(0);b.setMinWidth(0);b.setMinimumWidth(0);b.setBackground(bg(color));return b;}
 private void showBar(){
 bar=new LinearLayout(this);bar.setOrientation(LinearLayout.HORIZONTAL);bar.setGravity(Gravity.CENTER_VERTICAL);bar.setPadding(dp(3),dp(1),dp(3),dp(1));
 clock=new TextView(this);clock.setTextColor(Color.BLACK);clock.setTextSize(15);clock.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);clock.setSingleLine(true);clock.setGravity(Gravity.CENTER);clock.setBackground(bg("#BDE9FF"));
 bar.addView(clock,new LinearLayout.LayoutParams(0,dp(30),2.3f));
 toggle=button("Başlat","#B9E9B4");LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(30),1);p.leftMargin=dp(3);bar.addView(toggle,p);
 Button reset=button("Sıfırla","#FFBFC0");LinearLayout.LayoutParams q=new LinearLayout.LayoutParams(0,dp(30),1);q.leftMargin=dp(3);bar.addView(reset,q);
 Button close=button("Kapat","#FFB347");LinearLayout.LayoutParams c=new LinearLayout.LayoutParams(0,dp(30),1);c.leftMargin=dp(3);bar.addView(close,c);
 toggle.setOnClickListener(v->{if(running){elapsed+=SystemClock.elapsedRealtime()-started;running=false;handler.removeCallbacks(ticker);toggle.setText("Devam Et");}else{started=SystemClock.elapsedRealtime();running=true;toggle.setText("Duraklat");handler.removeCallbacks(ticker);handler.post(ticker);}update();});
 reset.setOnClickListener(v->{running=false;elapsed=0;handler.removeCallbacks(ticker);toggle.setText("Başlat");update();});
 close.setOnClickListener(v->{running=false;handler.removeCallbacks(ticker);stopSelf();});
 WindowManager.LayoutParams w=new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,PixelFormat.TRANSLUCENT);
 w.gravity=Gravity.TOP;w.y=dp(26);wm.addView(bar,w);update();
 }
 private void update(){if(clock==null)return;long ms=elapsed+(running?SystemClock.elapsedRealtime()-started:0);long s=ms/1000;clock.setText(String.format(Locale.ROOT,"%02d:%02d:%02d.%02d",s/3600,(s/60)%60,s%60,(ms%1000)/10));}
 @Override public void onConfigurationChanged(Configuration c){super.onConfigurationChanged(c);if(bar!=null){WindowManager.LayoutParams p=(WindowManager.LayoutParams)bar.getLayoutParams();p.y=dp(26);wm.updateViewLayout(bar,p);}}
 @Override public int onStartCommand(Intent i,int flags,int id){return START_STICKY;}
 @Override public void onDestroy(){handler.removeCallbacks(ticker);if(bar!=null)wm.removeView(bar);super.onDestroy();}
 @Override public IBinder onBind(Intent i){return null;}
}