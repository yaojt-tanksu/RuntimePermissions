package com.example.android.system.runtimepermissions.camera;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.android.common.logger.Log;
import com.example.android.system.runtimepermissions.R;

/**
 * 显示相机预览页面
 */
public class CameraPreviewFragment extends Fragment {

    private static final String TAG = "CameraPreview";

    /* 相机的id，0=前置相机 */
    private static final int CAMERA_ID = 0;

    private CameraPreview mPreview;

    private Camera mCamera;

    public static CameraPreviewFragment newInstance() {
        return new CameraPreviewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mCamera = getCameraInstance(CAMERA_ID);
        Camera.CameraInfo cameraInfo = null;

        if (mCamera != null) {
            cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(CAMERA_ID, cameraInfo);
        }

        if (mCamera == null) {
            // 相机不可用，显示错误信息
            Toast.makeText(getActivity(), "相机不可用，请检查！", Toast.LENGTH_SHORT).show();
            return inflater.inflate(R.layout.fragment_camera_unavailable, null);
        }

        View root = inflater.inflate(R.layout.fragment_camera, null);

        // 根据屏幕的旋转，设置预览相机
        final int displayRotation = getActivity().getWindowManager().getDefaultDisplay()
                .getRotation();

        // 设置相机预览页面
        mPreview = new CameraPreview(getActivity(), mCamera, cameraInfo, displayRotation);
        FrameLayout preview = (FrameLayout) root.findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    /**
     * 拿到相机对象
     *
     * @param cameraId 相机id
     * @return 当相机不可用或者不存在时，返回null
     */
    public static Camera getCameraInstance(int cameraId) {
        Camera c = null;
        try {
            // 开启相机
            c = Camera.open(cameraId);
        } catch (Exception e) {
            // 相机不可用或者不存在
            Log.d(TAG, "Camera " + cameraId + " is not available: " + e.getMessage());
        }
        return c;
    }

    /**
     * 释放相机引用
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

}
