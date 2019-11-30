package com.ahmetc.subconsciousmanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.ahmetc.subconsciousmanager.Adapters.CategoriesAdapter;
import com.ahmetc.subconsciousmanager.Database.AffirmationsDao;
import com.ahmetc.subconsciousmanager.Database.CategoriesDao;
import com.ahmetc.subconsciousmanager.Database.DatabaseCopyHelper;
import com.ahmetc.subconsciousmanager.Database.DatabaseHelper;
import com.ahmetc.subconsciousmanager.Models.Affirmations;
import com.ahmetc.subconsciousmanager.Models.Categories;
import com.ahmetc.subconsciousmanager.Receivers.ReminderReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.seismic.ShakeDetector;
import android.speech.RecognizerIntent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ShakeDetector.Listener {
    private ImageView settingsView, favView, infoView, categoryView, shakeView;
    private TextView contentText, categoryText;
    private FloatingActionButton micFab;
    private ConstraintLayout mainLayout;
    private Dialog dialog;
    private DatabaseHelper dbh;
    private AffirmationsDao affirmationsDao;
    private ArrayList<Affirmations> affirmationsArrayList = new ArrayList<>();
    private ArrayList<Categories> categoriesArrayList = new ArrayList<>();
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private SensorManager sensorManager;
    private Animation animShake;
    private Animation animShakeText;
    private Affirmations affirmation;
    Vibrator vibrator;
    private boolean blockFav = true;
    private int category_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        shakeIcon();
        micFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });
        shakeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shakeView.clearAnimation();
                Snackbar.make(mainLayout,"Shake the phone for move to the next",
                        Snackbar.LENGTH_LONG).show();
            }
        });
        settingsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.settings_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                final TimePicker timePicker = dialog.findViewById(R.id.timePicker);
                final Switch reminderSwitch = dialog.findViewById(R.id.reminderSwitch);
                if(preferences.getBoolean("reminderActive",true)) {
                    reminderSwitch.setChecked(true);
                    timePicker.setCurrentHour(preferences.getInt("hour",timePicker.getCurrentHour()));
                    timePicker.setCurrentMinute(preferences.getInt("minute",timePicker.getCurrentMinute()));
                    timePicker.setEnabled(true);
                }
                else {
                    timePicker.setEnabled(false);
                }
                reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) timePicker.setEnabled(true);
                        else timePicker.setEnabled(false);
                    }
                });
                Button settingsSave = dialog.findViewById(R.id.settingsSave);
                settingsSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(MainActivity.this, ReminderReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                        if(reminderSwitch.isChecked()) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                            calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                            calendar.set(Calendar.SECOND,0);
                            editor = preferences.edit();
                            editor.putInt("hour",timePicker.getCurrentHour());
                            editor.putInt("minute", timePicker.getCurrentMinute());
                            editor.putBoolean("reminderActive", true);
                            editor.apply();
                            long repeatTime = 100 * 60 * 60 * 24;
                            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                                    calendar.getTimeInMillis(),repeatTime,pendingIntent);
                            Toast.makeText(MainActivity.this, "Reminder has been activated!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        else {
                            if (alarmMgr!= null) {
                                alarmMgr.cancel(pendingIntent);
                                editor = preferences.edit();
                                editor.remove("hour");
                                editor.remove("minute");
                                editor.putBoolean("reminderActive", false);
                                editor.apply();
                                Toast.makeText(MainActivity.this, "Reminder has been cancelled!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        dialog.dismiss();
                    }
                });
                Button settingsPreview = dialog.findViewById(R.id.settingsPreview);
                settingsPreview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NotificationCompat.Builder builder;
                        final String title = "Your therapist is ready";
                        String contentMessage = affirmationsDao.getAfm(dbh,category_id);
                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            String kanalId = "kanalId";
                            String kanalAd = "kanalAd";
                            String kanalTanim = "kanalTanim";
                            int kanalOnceligi = NotificationManager.IMPORTANCE_HIGH; // Yüksek öncelikli olarak göster
                            NotificationChannel channel = notificationManager.getNotificationChannel("kanalId");
                            if (channel == null) {
                                channel = new NotificationChannel(kanalId, kanalAd, kanalOnceligi);
                                channel.setDescription(kanalTanim);
                                notificationManager.createNotificationChannel(channel);
                            }
                            builder = new NotificationCompat.Builder(MainActivity.this, kanalId);
                            builder.setContentTitle(title);
                            builder.setContentText(contentMessage);
                            builder.setOngoing(true);
                            builder.setPriority(Notification.PRIORITY_MAX);
                            builder.setAutoCancel(true);
                            builder.setSmallIcon(R.mipmap.icon);
                            builder.setVibrate(new long[] { 1000, 1000});
                            builder.setColorized(true);
                            builder.setColor(getResources().getColor(R.color.colorPrimary));
                            builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                            builder.setAutoCancel(true);

                        } else {
                            builder = new NotificationCompat.Builder(MainActivity.this);
                            builder.setContentTitle(title);
                            builder.setContentText(contentMessage);
                            builder.setVibrate(new long[] { 1000, 1000});
                            builder.setColorized(true);
                            builder.setPriority(Notification.PRIORITY_MAX);
                            builder.setColor(getResources().getColor(R.color.colorPrimary));
                            builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                            builder.setSmallIcon(R.mipmap.icon);
                            builder.setAutoCancel(true);
                        }

                        if(contentMessage != null) {
                            notificationManager.notify(1, builder.build());
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        favView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(affirmation.getIsFav() == 0) {
                    affirmationsDao.setFavAfm(dbh,affirmation.getAffirmation_id(),1);
                    affirmation.setIsFav(1);
                    favView.setImageResource(R.drawable.heart_checked);
                }
                else {
                    affirmationsDao.setFavAfm(dbh,affirmation.getAffirmation_id(),0);
                    affirmation.setIsFav(0);
                    favView.setImageResource(R.drawable.heart);
                }
            }
        });
        categoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.cat_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                RecyclerView categoryRv = dialog.findViewById(R.id.categoryRv);
                categoryRv.setHasFixedSize(true);
                categoryRv.setItemAnimator(new DefaultItemAnimator());
                categoryRv.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
                categoryRv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                CategoriesAdapter categoriesAdapter = new CategoriesAdapter(
                        MainActivity.this, categoriesArrayList,dialog);
                categoryRv.setAdapter(categoriesAdapter);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        category_id = preferences.getInt("category_id",0);
                        getAfm();
                        setUiAfm();
                    }
                });
                dialog.show();
            }
        });
    }
    private void init() {
        kopyala();
        dbh = new DatabaseHelper(this);
        settingsView = findViewById(R.id.settingsView);
        favView = findViewById(R.id.favView);
        favView.setEnabled(false);
        infoView = findViewById(R.id.infoView);
        categoryView = findViewById(R.id.categoryView);
        shakeView = findViewById(R.id.shakeView);
        contentText = findViewById(R.id.contentText);
        micFab = findViewById(R.id.micFab);
        mainLayout = findViewById(R.id.mainLayout);
        preferences = getSharedPreferences("english",MODE_PRIVATE);
        categoriesArrayList = new CategoriesDao().getCategories(dbh);
        category_id = preferences.getInt("category_id",0);
        animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
        animShakeText = AnimationUtils.loadAnimation(this, R.anim.shake_text);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);
        getAfm();
    }
    private void kopyala() {
        DatabaseCopyHelper databaseCopyHelper = new DatabaseCopyHelper(this);
        try {
            databaseCopyHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        databaseCopyHelper.openDataBase();
    }
    private void getAfm() {
        affirmationsDao = new AffirmationsDao();
        if(category_id == 0) {
            affirmationsArrayList = affirmationsDao.getAllAfm(dbh);
        }
        else if(category_id == 99) {
            affirmationsArrayList = affirmationsDao.getFavAfm(dbh);
        }
        else {
            affirmationsArrayList = affirmationsDao.getCustomAfm(dbh,category_id);
        }
    }
    private void setUiAfm() {
        if(affirmationsArrayList.isEmpty()) {
            contentText.setText("No affirmation about this category. Please select another category.");
            favView.setEnabled(false);
        }
        else {
            favView.setEnabled(true);
            Random random = new Random();
            affirmation = affirmationsArrayList.get(random.nextInt(affirmationsArrayList.size()));
            contentText.setText(affirmation.getAffirmation_text());
            if(affirmation.getIsFav() == 0) favView.setImageResource(R.drawable.heart);
            else favView.setImageResource(R.drawable.heart_checked);
            shakeView.clearAnimation();
        }
    }
    private void shakeIcon() {
        shakeView.startAnimation(animShake);
    }

    @Override
    public void hearShake() {
        vibrator.vibrate(300);
        contentText.startAnimation(animShakeText);
        setUiAfm();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1015: {
                if(resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = affirmation.getAffirmation_text();
                    text = text.replace(",","");
                    text = text.replace("!","");
                    text = text.replace(".","");
                    text = text.replace("I am","I'm");
                    Log.e("AA",text + " : " + result.get(0));
                    if(result.get(0).equalsIgnoreCase(text)) {
                        shakeView.startAnimation(animShake);
                        Snackbar.make(mainLayout,"Congratulations! Shake the phone for move to the next",
                                Snackbar.LENGTH_LONG).show();
                        vibrator.vibrate(400);
                        Toast.makeText(this, "Shake the phone!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    }
    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please say '" + affirmation.getAffirmation_text()+"'");
        try {
            startActivityForResult(intent, 1015);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(this, "Phone is not supporting", Toast.LENGTH_SHORT).show();
        }
    }
}
