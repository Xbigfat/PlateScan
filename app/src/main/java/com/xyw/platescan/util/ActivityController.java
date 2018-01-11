package com.xyw.platescan.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 单例活动管理器
 * Created by 31429 on 2018/1/11.
 */

public class ActivityController {

    private List<Activity> activitiesList;
    private static ActivityController controller;

    private ActivityController() {
        activitiesList = new ArrayList<>();
    }

    public static ActivityController getController() {
        if (controller == null) {
            synchronized (ActivityController.class) {
                if (controller == null) {
                    controller = new ActivityController();
                }
            }
        }
        return controller;
    }

    public void register(Activity activity) {
        activitiesList.add(activity);
    }

    public void remove(Activity activity) {
        activitiesList.remove(activity);
    }

    public void quit() {
        for (Activity activity : activitiesList) {
            activity.finish();
        }
    }

}
