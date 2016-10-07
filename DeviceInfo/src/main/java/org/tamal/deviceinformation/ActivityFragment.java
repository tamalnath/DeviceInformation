package org.tamal.deviceinformation;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.ACTIVITY_SERVICE;

public class ActivityFragment extends BaseFragment {

    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter adapter = new Adapter();
        ActivityManager activityManager = (ActivityManager) getContext().getSystemService(ACTIVITY_SERVICE);
        Map<String, Object> map = Utils.findProperties(activityManager);
        ArrayList<ActivityManager.AppTask> appTasks = (ArrayList<ActivityManager.AppTask>) map.remove("AppTasks");
        final ArrayList<ActivityManager.RecentTaskInfo> recentTaskInfo = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (ActivityManager.AppTask appTask : appTasks) {
                recentTaskInfo.add(appTask.getTaskInfo());
            }
        } else {
            recentTaskInfo.addAll(activityManager.getRecentTasks(Integer.MAX_VALUE, ActivityManager.RECENT_WITH_EXCLUDED));
        }
        adapter.addButton(getString(R.string.recent_tasks), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DelegatorActivity.class);
                intent.putExtra(DelegatorActivity.ACTIVITY_TITLE_ID, R.string.recent_tasks);
                intent.putParcelableArrayListExtra(DelegatorActivity.PARCELABLES, recentTaskInfo);
                startActivity(intent);
            }
        });
        final ArrayList<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = (ArrayList<ActivityManager.RunningAppProcessInfo>) map.remove("RunningAppProcesses");
        if (runningAppProcessInfo != null) {
            adapter.addButton(getString(R.string.running_process), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), DelegatorActivity.class);
                    intent.putExtra(DelegatorActivity.ACTIVITY_TITLE_ID, R.string.running_process);
                    intent.putParcelableArrayListExtra(DelegatorActivity.PARCELABLES, runningAppProcessInfo);
                    startActivity(intent);
                }
            });
        }
        final ArrayList<ActivityManager.ProcessErrorStateInfo> processErrorStateInfo = (ArrayList<ActivityManager.ProcessErrorStateInfo>) map.remove("ProcessesInErrorState");
        if (processErrorStateInfo != null) {
            adapter.addButton(getString(R.string.process_error_state), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), DelegatorActivity.class);
                    intent.putExtra(DelegatorActivity.ACTIVITY_TITLE_ID, R.string.process_error_state);
                    intent.putParcelableArrayListExtra(DelegatorActivity.PARCELABLES, processErrorStateInfo);
                    startActivity(intent);
                }
            });
        }
        final ArrayList<ActivityManager.RunningServiceInfo> runningServiceInfo = new ArrayList<>(activityManager.getRunningServices(Integer.MAX_VALUE));
        adapter.addButton(getString(R.string.running_services), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DelegatorActivity.class);
                intent.putExtra(DelegatorActivity.ACTIVITY_TITLE_ID, R.string.running_services);
                intent.putParcelableArrayListExtra(DelegatorActivity.PARCELABLES, runningServiceInfo);
                startActivity(intent);
            }
        });
        final ArrayList<ActivityManager.RunningTaskInfo> runningTasks = new ArrayList<>(activityManager.getRunningTasks(Integer.MAX_VALUE));
        adapter.addButton(getString(R.string.running_tasks), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DelegatorActivity.class);
                intent.putExtra(DelegatorActivity.ACTIVITY_TITLE_ID, R.string.running_tasks);
                intent.putParcelableArrayListExtra(DelegatorActivity.PARCELABLES, runningTasks);
                startActivity(intent);
            }
        });

        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        map.putAll(Utils.findFields(memoryInfo));
        ConfigurationInfo configurationInfo = (ConfigurationInfo) map.remove("DeviceConfigurationInfo");
        map.putAll(Utils.findFields(configurationInfo));
        map.putAll(Utils.findProperties(configurationInfo));

        adapter.addMap(map);
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

}
