/*
* Copyright 2015 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.system.runtimepermissions;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.example.android.common.logger.Log;
import com.example.android.common.logger.LogFragment;
import com.example.android.common.logger.LogWrapper;
import com.example.android.common.logger.MessageOnlyLogFilter;
import com.example.android.system.runtimepermissions.camera.CameraPreviewFragment;
import com.example.android.system.runtimepermissions.contacts.ContactsFragment;

import common.activities.SampleActivityBase;

/**
 * Launcher Activity that demonstrates the use of runtime permissions for Android M.
 * It contains a summary sample description, sample log and a Fragment that calls callbacks on this
 * Activity to illustrate parts of the runtime permissions API.
 * <p/>
 * This Activity requests permissions to access the camera ({@link android.Manifest.permission#CAMERA})
 * when the 'Show Camera' button is clicked to display the camera preview.
 * Contacts permissions (({@link android.Manifest.permission#READ_CONTACTS} and ({@link
 * android.Manifest.permission#WRITE_CONTACTS})) are requested when the 'Show and Add Contacts'
 * button is
 * clicked to display the first contact in the contacts database and to add a dummy contact
 * directly to it. Permissions are verified and requested through compat helpers in the support v4
 * library, in this Activity using {@link ActivityCompat}.
 * First, permissions are checked if they have already been granted through {@link
 * ActivityCompat#checkSelfPermission(Context, String)}.
 * If permissions have not been granted, they are requested through
 * {@link ActivityCompat#requestPermissions(Activity, String[], int)} and the return value checked
 * in
 * a callback to the {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}
 * interface.
 * <p/>
 * Before requesting permissions, {@link ActivityCompat#shouldShowRequestPermissionRationale(Activity,
 * String)}
 * should be called to provide the user with additional context for the use of permissions if they
 * have been denied previously.
 * <p/>
 * If this sample is executed on a device running a platform version below M, all permissions
 * declared
 * in the Android manifest file are always granted at install time and cannot be requested at run
 * time.
 * <p/>
 * This sample targets the M platform and must therefore request permissions at runtime. Change the
 * targetSdk in the file 'Application/build.gradle' to 22 to run the application in compatibility
 * mode.
 * Now, if a permission has been disable by the system through the application settings, disabled
 * APIs provide compatibility data.
 * For example the camera cannot be opened or an empty list of contacts is returned. No special
 * action is required in this case.
 * <p/>
 * (This class is based on the MainActivity used in the SimpleFragment sample template.)
 */
public class MainActivity1 extends SampleActivityBase
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String TAG = "MainActivitySampleActivityBase";

    /**
     * Id to identify a camera permission request.
     */
    private static final int REQUEST_CAMERA = 0;

    /**
     * Id to identify a contacts permission request.
     */
    private static final int REQUEST_CONTACTS = 1;

    /**
     * Permissions required to read and write contacts. Used by the {@link ContactsFragment}.
     */
    private static String[] PERMISSIONS_CONTACT = {Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS};

    // Whether the Log Fragment is currently shown.
    private boolean mLogShown;

    /**
     * Root of the layout of this Activity.
     */
    private View mLayout;

    /**
     * Called when the 'show camera' button is clicked.
     * Callback is defined in resource layout definition.
     */
    public void showCamera(View view) {
        Log.i(TAG, "Show camera button pressed. Checking permission.");
        // BEGIN_INCLUDE(camera_permission)
        // Check if the Camera permission is already available.

//        requestCamera();
//        requestCamera1();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.

            requestCameraPermission();
            Log.d(TAG, "requestCamera showCamera if");


        } else {
            Log.d(TAG, "requestCamera showCamera else");
            // Camera permissions is already available, show the camera preview.
            Log.i(TAG,
                    "CAMERA permission has already been granted. Displaying camera preview.");
            showCameraPreview();
        }
        // END_INCLUDE(camera_permission)

    }


    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    int count = 1;

    /**
     * ContextCompat.checkSelfPermission()
     * 被授权函数返回PERMISSION_GRANTED，否则返回PERMISSION_DENIED ，在所有版本都是如此。
     * ActivityCompat.requestPermissions()
     * 这个方法在M之前版本调用，OnRequestPermissionsResultCallback 直接被调用，带着正确的 PERMISSION_GRANTED或者 PERMISSION_DENIED 。
     * ActivityCompat.shouldShowRequestPermissionRationale()
     * 在M之前版本调用，永远返回false。
     */
    private void requestCamera() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        Log.d(TAG, "requestCamera hasWriteContactsPermission:" + hasWriteContactsPermission);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.d(TAG, "requestCamera SDK_INT <= Build.VERSION_CODES.M:" + Build.VERSION.SDK_INT);
            return;
        }

        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "requestCamera hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED");
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                count++;
                Log.d(TAG, "requestCamera !ActivityCompat.shouldShowRequestPermissionRationale");

//                showMessageOKCancel("test: You need to allow access to Contacts",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},
//                                        REQUEST_CAMERA);
//                                Log.d(TAG, "requestCamera  ActivityCompat.requestPermissions");
//                            }
//                        });


                //TODO
//                PackageManager pm = getPackageManager();
//
//                PackageInfo info = null;
//                try {
//                    info = pm.getPackageInfo(getPackageName(), 0);
//                } catch (PackageManager.NameNotFoundException e) {
//                    e.printStackTrace();
//                    Log.d(TAG, "requestCamera  NameNotFoundException");
//                }
//                Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
//                i.setClassName("com.android.settings", "com.miui.securitycenter.permission.AppPermissionsEditor");
//                i.putExtra("extra_package_uid", info.applicationInfo.uid);
//                try {
//                    startActivity(i);
//                } catch (Exception e) {
//                    Toast.makeText(this, "只有MIUI才可以设置哦", Toast.LENGTH_SHORT).show();
//                }



                //TODO
                Intent intent = new Intent();
                intent.setAction("android.intent.action.MAIN");
                intent.setClassName("com.android.settings", "com.android.settings.ManageApplications");
                startActivity(intent);


            } else {
                count++;
                Log.d(TAG, "requestCamera  ==ActivityCompat.shouldShowRequestPermissionRationale");
                // Camera permission has not been granted yet. Request it directly.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA);
            }
//            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_CONTACTS},
//                    REQUEST_CODE_ASK_PERMISSIONS);
//            return;
        } else {
            count++;
            Log.d(TAG, "requestCamera hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED");
            // Camera permissions is already available, show the camera preview.
            Log.i(TAG,
                    "CAMERA permission has already been granted. Displaying camera preview.");
            showCameraPreview();
        }
//        insertDummyContact();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestCamera1() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.CAMERA);
        Log.d(TAG, "requestCamera hasWriteContactsPermission:" + hasWriteContactsPermission);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.d(TAG, "requestCamera SDK_INT <= Build.VERSION_CODES.M:" + Build.VERSION.SDK_INT);
            return;
        }

        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "requestCamera hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED");
            Log.d(TAG, "requestCamera shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)：" +
                    shouldShowRequestPermissionRationale(Manifest.permission.CAMERA));
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) && count > 2) {
                count++;
                Log.d(TAG, "requestCamera !ActivityCompat.shouldShowRequestPermissionRationale");
//                showMessageOKCancel("test: You need to allow access to Contacts",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                requestPermissions(new String[]{Manifest.permission.CAMERA},
//                                        REQUEST_CAMERA);
//                                Log.d(TAG, "requestCamera  ActivityCompat.requestPermissions");
//                            }
//                        });


                //TODO
                PackageManager pm = getPackageManager();

                PackageInfo info = null;
                try {
                    info = pm.getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    Log.d(TAG, "requestCamera  NameNotFoundException");
                }
                Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
                i.setClassName("com.android.settings", "com.miui.securitycenter.permission.AppPermissionsEditor");
                i.putExtra("extra_package_uid", info.applicationInfo.uid);
                try {
                    startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(this, "只有MIUI才可以设置哦", Toast.LENGTH_SHORT).show();
                }


            } else {
                count++;
                Log.d(TAG, "requestCamera  ==ActivityCompat.shouldShowRequestPermissionRationale");
                // Camera permission has not been granted yet. Request it directly.
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA);
            }
//            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_CONTACTS},
//                    REQUEST_CODE_ASK_PERMISSIONS);
//            return;
        } else {
            count++;
            Log.d(TAG, "requestCamera hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED");
            // Camera permissions is already available, show the camera preview.
            Log.i(TAG,
                    "CAMERA permission has already been granted. Displaying camera preview.");
            showCameraPreview();
        }
//        insertDummyContact();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity1.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    /**
     * Requests the Camera permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestCameraPermission() {
        Log.i(TAG, "CAMERA permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.d(TAG,
                    "Displaying camera permission rationale to provide additional context.");
            Snackbar.make(mLayout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity1.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA);
                            Log.d(TAG,
                                    "Displaying camera permission rationale onClick");
                        }
                    })
                    .show();
        } else {
            Log.i(TAG,
                    "Displaying camera permission else");
            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
        // END_INCLUDE(camera_permission_request)
    }

    /**
     * Called when the 'show camera' button is clicked.
     * Callback is defined in resource layout definition.
     */
    public void showContacts(View v) {
        Log.i(TAG, "Show contacts button pressed. Checking permissions.");

        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            Log.i(TAG, "Contact permissions has NOT been granted. Requesting permissions.");
            requestContactsPermissions();

        } else {

            // Contact permissions have been granted. Show the contacts fragment.
            Log.i(TAG,
                    "Contact permissions have already been granted. Displaying contact details.");
            showContactDetails();
        }
    }


    /**
     * Requests the Contacts permissions.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestContactsPermissions() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_CONTACTS)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i(TAG,
                    "Displaying contacts permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(mLayout, R.string.permission_contacts_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(MainActivity1.this, PERMISSIONS_CONTACT,
                                            REQUEST_CONTACTS);
                        }
                    })
                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSIONS_CONTACT, REQUEST_CONTACTS);
        }
        // END_INCLUDE(contacts_permission_request)
    }


    /**
     * Display the {@link CameraPreviewFragment} in the content area if the required Camera
     * permission has been granted.
     */
    private void showCameraPreview() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sample_content_fragment, CameraPreviewFragment.newInstance())
                .addToBackStack("contacts")
                .commit();
    }

    /**
     * Display the {@link ContactsFragment} in the content area if the required contacts
     * permissions
     * have been granted.
     */
    private void showContactDetails() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sample_content_fragment, ContactsFragment.newInstance())
                .addToBackStack("contacts")
                .commit();
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for Camera permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "CAMERA permission has now been granted. Showing preview.");
                Snackbar.make(mLayout, R.string.permision_available_camera,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "CAMERA permission was NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT).show();

            }
            // END_INCLUDE(permission_result)

        } else if (requestCode == REQUEST_CONTACTS) {
            Log.i(TAG, "Received response for contact permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (Permissions.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                Snackbar.make(mLayout, R.string.permision_available_contacts,
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                Log.i(TAG, "Contacts permissions were NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /* Note: Methods and definitions below are only used to provide the UI for this sample and are
    not relevant for the execution of the runtime permissions API. */


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
     * Create a chain of targets that will receive log data
     */
    @Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());
    }

    public void onBackClick(View view) {
        getSupportFragmentManager().popBackStack();
    }

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

        // This method sets up our custom logger, which will print all log messages to the device
        // screen, as well as to adb logcat.
        initializeLogging();
    }
}
