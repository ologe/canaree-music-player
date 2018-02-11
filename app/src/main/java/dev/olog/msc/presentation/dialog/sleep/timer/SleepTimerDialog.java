package dev.olog.msc.presentation.dialog.sleep.timer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dev.olog.msc.R;
import dev.olog.msc.domain.interactor.prefs.SleepTimerUseCase;
import dev.olog.msc.utils.PendingIntents;
import dev.olog.shared_android.interfaces.MusicServiceClass;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Sleep timer is reset in App.onCreate() and MusicService.onDestroy()
 */
public class SleepTimerDialog extends TimePickerDialog {

    @Inject MusicServiceClass serviceClass;
    @Inject SleepTimerUseCase sleepTimerUseCase;

    private static String NEXT_SLEEP = "AppPreferencesDataStoreImpl.NEXT_SLEEP";
    private boolean isActive = false;

    public static SleepTimerDialog newInstance(){
        final SleepTimerDialog frag = new SleepTimerDialog();
        Bundle args = new Bundle();
        args.putInt(REFERENCE_KEY, 0);
        args.putInt(THEME_RES_ID_KEY, R.style.BetterPickersDialogFragment_Light);
        frag.setArguments(args);
        return frag;
    }

    public static void show(FragmentManager manager) {
        FragmentTransaction ft = manager.beginTransaction();
        final Fragment prev = manager.findFragmentByTag("hms_dialog");
        if (prev != null) {
            ft.remove(prev).commit();
            ft = manager.beginTransaction();
        }
        ft.addToBackStack(null);

        newInstance().show(ft, "hms_dialog");
    }

    public static void resetTimer(Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putLong(NEXT_SLEEP, -1L).apply();
    }

    private AlarmManager alarmManager;
    private Disposable timeDisposable = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        long nextSleep = sleepTimerUseCase.getLast();
        isActive = nextSleep != -1;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isActive){
            // counting down
            disableButtonUi();
            positiveButton.setText(R.string.sleep_timer_stop);
            long nextSleep = sleepTimerUseCase.getLast();
            setupCountdown(nextSleep);
        } else {
            // choosing time, can only dismiss after click
            enableButtonUi();
            positiveButton.setText(R.string.sleep_timer_positive);
        }

        positiveButton.setOnClickListener(v -> {
            if (isActive){
                // cancel previous timer and enable ui
                mPicker.reset();
                sleepTimerUseCase.reset();
                alarmManager.cancel(getSleepTimerPendingIntent());
                if (timeDisposable != null){
                    timeDisposable.dispose();
                    timeDisposable = null;
                }
                enableButtonUi();
                positiveButton.setText(R.string.sleep_timer_positive);
                isActive = false;
            } else {
                // set new timer and dismiss
                int hours = mPicker.getHours();
                int minutes = mPicker.getMinutes();
                int seconds = mPicker.getSeconds();
                long nextSleep = SystemClock.elapsedRealtime() + TimeUnit.HOURS.toMillis(hours) +
                        TimeUnit.MINUTES.toMillis(minutes) +
                        TimeUnit.SECONDS.toMillis(seconds);
                sleepTimerUseCase.set(nextSleep);
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextSleep, getSleepTimerPendingIntent());
                Toast.makeText(getActivity(), R.string.sleep_timer_set, Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        positiveButton.setOnClickListener(null);
        if (timeDisposable != null){
            timeDisposable.dispose();
            timeDisposable = null;
        }
    }

    private void disableButtonUi(){
        ImageButton deleteButton = mPicker.findViewById(R.id.delete);
        deleteButton.setEnabled(false);
        for (Button button : getButtons()) {
            button.setEnabled(false);
            button.setAlpha(.5f);
        }
    }

    private void enableButtonUi(){
        mPicker.updateDeleteButton();
        for (Button button : getButtons()) {
            button.setEnabled(true);
            button.setAlpha(1f);
        }
    }

    private List<Button> getButtons(){
        View v1 = mPicker.findViewById(R.id.first);
        View v2 = mPicker.findViewById(R.id.second);
        View v3 = mPicker.findViewById(R.id.third);
        View v4 = mPicker.findViewById(R.id.fourth);

        List<Button> list = new ArrayList<>();
        list.add(v4.findViewById(R.id.key_middle));

        list.add(v1.findViewById(R.id.key_left));
        list.add(v1.findViewById(R.id.key_middle));
        list.add(v1.findViewById(R.id.key_right));

        list.add(v2.findViewById(R.id.key_left));
        list.add(v2.findViewById(R.id.key_middle));
        list.add(v2.findViewById(R.id.key_right));

        list.add(v3.findViewById(R.id.key_left));
        list.add(v3.findViewById(R.id.key_middle));
        list.add(v3.findViewById(R.id.key_right));
        return list;
    }

    private void setupCountdown(long nextSleep){
        if (timeDisposable != null){
            timeDisposable.dispose();
        }
        setCountdownTime(nextSleep);
        timeDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> setCountdownTime(nextSleep), Throwable::printStackTrace);
    }

    private void setCountdownTime(long nextSleep){
        long current = nextSleep - SystemClock.elapsedRealtime();

        if (current <= 0L){
            timeDisposable.dispose();
            enableButtonUi();
            positiveButton.setText(R.string.sleep_timer_positive);
            return;
        }

        setTime(Math.max(0, extractHours(current)),
                Math.max(0, extractMinutes(current)),
                Math.max(0, extractSeconds(current)));
    }

    private PendingIntent getSleepTimerPendingIntent(){
        return PendingIntents.INSTANCE.stopServiceIntent(getContext(), serviceClass);
    }

    private int extractHours(long millis) {
        return (int) (millis / (1000 * 60 * 60));
    }

    private int extractMinutes(long millis){
        return (int) (millis / (1000 * 60) % 60);
    }

    private int extractSeconds(long millis){
        return (int) (millis / 1000 % 60);
    }

}
