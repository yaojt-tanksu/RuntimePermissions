package com.example.android.system.runtimepermissions;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RuntimePermissionsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, null);

        if (Build.VERSION.SDK_INT < 23) {
            /*
            联系人权限本来已经在AndroidManifest申请了，但是并不适用于m及以上的版本，
            需要用到运行时，并且需要用到的地方都要做一个判断。
            如果手机SDK版本小于23，隐藏掉显示联系人的按钮
            */
            root.findViewById(R.id.button_contacts).setVisibility(View.GONE);
        }
        return root;
    }
}
