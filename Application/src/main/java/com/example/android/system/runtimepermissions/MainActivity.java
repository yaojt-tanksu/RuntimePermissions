package com.example.android.system.runtimepermissions;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ViewAnimator;

import com.example.android.common.logger.Log;
import com.example.android.common.logger.LogFragment;
import com.example.android.common.logger.LogWrapper;
import com.example.android.common.logger.MessageOnlyLogFilter;
import com.example.android.system.runtimepermissions.camera.CameraPreviewFragment;
import com.example.android.system.runtimepermissions.contacts.ContactsFragment;

import common.activities.SampleActivityBase;

public class MainActivity extends SampleActivityBase
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String TAG = "MainActivity";

    /* 相机请求码 */
    private static final int REQUEST_CAMERA = 0;

    /* 联系人请求码 */
    private static final int REQUEST_CONTACTS = 1;

    /* 请求读取联系人权限 */
    private static String[] PERMISSIONS_CONTACT = {Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS};

    // 标志log fragment是否显示
    private boolean mLogShown;

    /* 主页的布局，依靠动态加载进来 */
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.sample_main_layout);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            RuntimePermissionsFragment fragment = new RuntimePermissionsFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
        initializeLogging();
    }

    /**
     * 点击显示联系人按钮相应
     * <p>
     * 回调已经被定义好了
     */
    public void showCamera(View view) {
        Log.i(TAG, "检查权限是否被受理！");
        // 检查是否想要的权限申请是否弹框。如果是第一次申请，用户不通过，
        // 那么第二次申请的话，就要给用户说明为什么需要申请这个权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // 权限未被授予
            requestCameraPermission();
        } else {
            Log.i(TAG, "相机权限已经被受理，开始预览相机！");
            showCameraPreview();
        }
    }

    /**
     * 申请相机权限
     */
    private void requestCameraPermission() {
        Log.i(TAG, "相机权限未被授予，需要申请！");
        // 相机权限未被授予，需要申请！
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // 如果访问了，但是没有被授予权限，则需要告诉用户，使用此权限的好处
            Log.i(TAG, "申请权限说明！");
            Snackbar.make(mLayout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 这里重新申请权限
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA);
                        }
                    })
                    .show();
        } else {
            // 第一次申请，就直接申请
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
    }

    public void showContacts(View v) {
        // 判断权限是否拥有
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "读写联系人权限未被授予，需要申请！");
            // 读写联系人权限未被授予，需要申请！
            requestContactsPermissions();
        } else {
            // 权限已经被授予，显示细节页面！
            Log.i(TAG, "权限已经被授予，显示细节页面！");
            showContactDetails();
        }
    }

    /**
     * 申请联系人读取权限
     */
    private void requestContactsPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_CONTACTS)) {
            // 如果是第二次申请，需要向用户说明为何使用此权限，会带出一个不再询问的复选框！
            Log.i(TAG, "如果是第二次申请，需要向用户说明为何使用此权限，会带出一个不再询问的复选框！");

            Snackbar.make(mLayout, R.string.permission_contacts_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(MainActivity.this, PERMISSIONS_CONTACT,
                                            REQUEST_CONTACTS);
                        }
                    })
                    .show();
        } else {
            // 第一次申请此权限，直接申请
            ActivityCompat.requestPermissions(this, PERMISSIONS_CONTACT, REQUEST_CONTACTS);
        }
    }

    /**
     * 显示相机预览界面
     */
    private void showCameraPreview() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sample_content_fragment, CameraPreviewFragment.newInstance())
                .addToBackStack("contacts")
                .commit();
    }

    /**
     * 显示联系人页面
     */
    private void showContactDetails() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sample_content_fragment, ContactsFragment.newInstance())
                .addToBackStack("contacts")
                .commit();
    }


    /**
     * 申请权限的回调，
     *
     * @param requestCode  requestCode
     * @param permissions  permissions
     * @param grantResults grantResults 多个权限一起返回
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mLayout, R.string.permision_available_camera,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CONTACTS) {
            // 这里有个多权限的检查，需要检查每一个权限是否都被授权了
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // true，所有权限已经被授予
                Snackbar.make(mLayout, R.string.permision_available_contacts,
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                // false，并不是所有权限都被授予
                Snackbar.make(mLayout, R.string.permissions_not_granted, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
        logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
        logToggle.setTitle(mLogShown ? R.string.sample_hide_log : R.string.sample_show_log);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_toggle_log:
                mLogShown = !mLogShown;
                ViewAnimator output = (ViewAnimator) findViewById(R.id.sample_output);
                if (mLogShown) {
                    output.setDisplayedChild(1);
                } else {
                    output.setDisplayedChild(0);
                }
                supportInvalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化log日志
     */
    @Override
    public void initializeLogging() {
        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);

        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());
    }

    public void onBackClick(View view) {
        // 因为我们对fragment入栈处理，按返回键的时候，出栈处理
        getSupportFragmentManager().popBackStack();
    }

}
