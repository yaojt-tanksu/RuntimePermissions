package com.example.android.system.runtimepermissions.contacts;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.common.logger.Log;
import com.example.android.system.runtimepermissions.R;

import java.util.ArrayList;

/**
 * 联系人实验页面
 */
public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "Contacts";
    private TextView mMessageText = null;

    private static String DUMMY_CONTACT_NAME = "yaojt-tanksu";

    /**
     * 查询条件
     */
    private static final String[] PROJECTION = {ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
    /**
     * 升序排列
     */
    private static final String ORDER = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC";


    /**
     * 构造函数
     */
    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

        mMessageText = (TextView) rootView.findViewById(R.id.contact_message);

        Button button = (Button) rootView.findViewById(R.id.contact_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertDummyContact();
            }
        });

        button = (Button) rootView.findViewById(R.id.contact_load);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadContact();
            }
        });
        return rootView;
    }

    /**
     * 显示第一位联系人
     */
    private void loadContact() {
        getLoaderManager().restartLoader(7, null, this);
    }

    /**
     * 初始化一个CursorLoader对象
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), ContactsContract.Contacts.CONTENT_URI, PROJECTION,
                null, null, ORDER);
    }


    /**
     *
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor != null) {
            final int totalCount = cursor.getCount();
            if (totalCount > 0) {
                cursor.moveToFirst();
                String name = cursor
                        .getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                mMessageText.setText(
                        getResources().getString(R.string.contacts_string, totalCount, name));
                Log.d(TAG, "第一位联系人名称：" + name);
                Log.d(TAG, "联系人总数：" + totalCount);
            } else {
                Log.d(TAG, "没有联系人！");
                mMessageText.setText(R.string.contacts_empty);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMessageText.setText(R.string.contacts_empty);
    }

    /**
     * 插入联系人
     */
    private void insertDummyContact() {
        // 插入联系人的实例
        ArrayList<ContentProviderOperation> operations = new ArrayList<>(2);

        // 设置新的联系人
        ContentProviderOperation.Builder op =
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null);
        operations.add(op.build());

        // 设置名字
        op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        DUMMY_CONTACT_NAME);
        operations.add(op.build());

        // 提交事务
        ContentResolver resolver = getActivity().getContentResolver();
        try {
            ContentProviderResult[] results = resolver.applyBatch(ContactsContract.AUTHORITY, operations);
            for (ContentProviderResult result :
                    results) {
                Log.d("-=-=-=>>", result.uri.toString());
            }
        } catch (RemoteException e) {
            Log.d(TAG, "Could not add a new contact: " + e.getMessage());
        } catch (OperationApplicationException e) {
            Log.d(TAG, "Could not add a new contact: " + e.getMessage());
        }
    }

}
