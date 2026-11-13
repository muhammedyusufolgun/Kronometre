package com.olgun.kronometre;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class MainActivity extends Activity {
 private boolean settingsOpened=false;
 private boolean started=false;
 @Override public void onCreate(Bundle state){super.onCreate(state);if(state!=null)settingsOpened=state.getBoolean("settingsOpened",false);}
 @Override protected void onResume(){super.onResume();
 if(started)return;
 if(Settings.canDrawOverlays(this)){
  started=true;
  try{startForegroundService(new Intent(this,OverlayService.class));finish();}
  catch(Exception e){started=false;Toast.makeText(this,"Kronometre başlatılamadı: "+e.getMessage(),Toast.LENGTH_LONG).show();}
 }else if(!settingsOpened){
  new AlertDialog.Builder(this).setTitle("Bir defalık izin gerekiyor")
   .setMessage("Kronometrenin diğer uygulamaların üzerinde görünmesi için 'Diğer uygulamaların üzerinde göster' iznini açın. İzin verildiğinde kronometre otomatik başlayacaktır.")
   .setCancelable(false).setPositiveButton("İzin ver",(d,w)->{
    settingsOpened=true;
    Intent i=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName()));
    startActivity(i);
   }).setNegativeButton("Vazgeç",(d,w)->finish()).show();
 }else{
  settingsOpened=false;
  new AlertDialog.Builder(this).setTitle("İzin gerekli")
   .setMessage("Ekran üstü kronometreyi açmak için izni etkinleştirin.")
   .setPositiveButton("İzin alanına git",(d,w)->{settingsOpened=true;startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName())));})
   .setNegativeButton("Kapat",(d,w)->finish()).show();
 }
 }
 @Override protected void onSaveInstanceState(Bundle out){super.onSaveInstanceState(out);out.putBoolean("settingsOpened",settingsOpened);}
}