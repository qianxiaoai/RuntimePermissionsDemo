# RuntimePermissionsDemo
A runtime permission demo based on Google RuntimePermissions Sample。
谷歌权限demo地址 : 
https://github.com/googlesamples/android-RuntimePermissions


* 因为工作需要，简单研究了一下Android6.0,动态权限申请，基于Google提供的demo，写了一个简单的demo。
* [原文地址：http://www.jianshu.com/p/a51593817825](http://www.jianshu.com/p/a51593817825)  

## 权限申请步骤

* 1 将targetSdkVersion 设置为23，注意，如果你讲targetSdkVersion设置为>=23，则必须Android谷歌的要求，动态的申请权限，如果你暂时不打算支持动态权限申请，则targetSdkVersion最大设置为22.

* 2  在AndroidManifest.xml中申请你需要的权限，包括普通权限和需要申请的特殊权限。

* 3 开始申请权限，此处分为3部。

（1）检查是否由此权限checkSelfPermission()，如果已经开启，则直接做你想做的。

（2）如果未开启，则判断是否需要向用户解释为何申请权限shouldShowRequestPermissionRationale。
（3）如果需要（即返回true），则可以弹出对话框提示用户申请权限原因，用户确认后申请权限requestPermissions()，如果不需要（即返回false），则直接申请权限requestPermissions()。
（这里是一部门代码，底部有比较完善的代码，整个demo可以在github中下载）。

* 备注：

（1）checkSelfPermission：检查是否拥有这个权限
 
（2）requestPermissions：请求权限，一般会弹出一个系统对话框，询问用户是否开启这个权限。
 
（3）shouldShowRequestPermissionRationale：Android原生系统中，如果第二次弹出权限申请的对话框，会出现“以后不再弹出”的提示框，如果用户勾选了，你再申请权限，则shouldShowRequestPermissionRationale返回true，意思是说要给用户一个 解释，告诉用户为什么要这个权限。然而，在实际开发中，需要注意的是，很多手机对原生系统做了修改，比如小米，小米4的6.0的shouldShowRequestPermissionRationale 就一直返回false，而且在申请权限时，如果用户选择了拒绝，则不会再弹出对话框了。。。。 所以说这个地方有坑，我的解决方法是，在回调里面处理，如果用户拒绝了这个权限，则打开本应用信息界面，由用户自己手动开启这个权限。
![单个权限申请.png](http://upload-images.jianshu.io/upload_images/1975505-8797c58a6bc35b84.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

（4）每个应用都有自己的权限管理界面，里面有本应用申请的权限以及各种状态，即使用户已经同意了你申请的权限，他也随时可以关闭

![权限管理界面.png](http://upload-images.jianshu.io/upload_images/1975505-c8bc0d8e5c95cead.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


         /**
             * Requests permission.
             *
             * @param activity
             * @param requestCode request code, e.g. if you need request CAMERA permission,parameters is PermissionUtils.CODE_CAMERA
             */
            public static void requestPermission(final Activity activity, final int requestCode, PermissionGrant permissionGrant) {
                if (activity == null) {
                    return;
                }
        
                Log.i(TAG, "requestPermission requestCode:" + requestCode);
                if (requestCode < 0 || requestCode >= requestPermissions.length) {
                    Log.w(TAG, "requestPermission illegal requestCode:" + requestCode);
                    return;
                }
        
                final String requestPermission = requestPermissions[requestCode];
        
                //如果是6.0以下的手机，ActivityCompat.checkSelfPermission()会始终等于PERMISSION_GRANTED，
            // 但是，如果用户关闭了你申请的权限(如下图，在安装的时候，将一些权限关闭了)，ActivityCompat.checkSelfPermission()则可能会导致程序崩溃(java.lang.RuntimeException: Unknown exception code: 1 msg null)，
            // 你可以使用try{}catch(){},处理异常，也可以判断系统版本，低于23就不申请权限，直接做你想做的。permissionGrant.onPermissionGranted(requestCode);
    //        if (Build.VERSION.SDK_INT < 23) {
    //            permissionGrant.onPermissionGranted(requestCode);
    //            return;
    //        }
        
                int checkSelfPermission;
                try {
                    checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
                } catch (RuntimeException e) {
                    Toast.makeText(activity, "please open this permission", Toast.LENGTH_SHORT)
                            .show();
                    Log.e(TAG, "RuntimeException:" + e.getMessage());
                    return;
                }
        
                if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "ActivityCompat.checkSelfPermission != PackageManager.PERMISSION_GRANTED");
        
        
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                        Log.i(TAG, "requestPermission shouldShowRequestPermissionRationale");
                        shouldShowRationale(activity, requestCode, requestPermission);
        
                    } else {
                        Log.d(TAG, "requestCameraPermission else");
                        ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
                    }
        
                } else {
                    Log.d(TAG, "ActivityCompat.checkSelfPermission ==== PackageManager.PERMISSION_GRANTED");
                    Toast.makeText(activity, "opened:" + requestPermissions[requestCode], Toast.LENGTH_SHORT).show();
                    permissionGrant.onPermissionGranted(requestCode);
                }
            }
            
![6.0以下系统的应用程序安装界面.png](http://upload-images.jianshu.io/upload_images/1975505-ec1877b089a98891.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
       

## 一次申请多个权限
其实和申请一个权限是一样的，只是requestPermissions(final @NonNull Activity activity,
            final @NonNull String[] permissions, final int requestCode),里面的permissions给的参数多些而已。

![申请多个权限.png](http://upload-images.jianshu.io/upload_images/1975505-010c521fdfc20714.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

     /**
         * 一次申请多个权限
         */
        public static void requestMultiPermissions(final Activity activity, PermissionGrant grant) {
    
            final List<String> permissionsList = getNoGrantedPermission(activity, false);
            final List<String> shouldRationalePermissionsList = getNoGrantedPermission(activity, true);
    
            //TODO checkSelfPermission
            if (permissionsList == null || shouldRationalePermissionsList == null) {
                return;
            }
            Log.d(TAG, "requestMultiPermissions permissionsList:" + permissionsList.size() + ",shouldRationalePermissionsList:" + shouldRationalePermissionsList.size());
    
            if (permissionsList.size() > 0) {
                ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]),
                        CODE_MULTI_PERMISSION);
                Log.d(TAG, "showMessageOKCancel requestPermissions");
    
            } else if (shouldRationalePermissionsList.size() > 0) {
                showMessageOKCancel(activity, "should open those permission",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity, shouldRationalePermissionsList.toArray(new String[shouldRationalePermissionsList.size()]),
                                        CODE_MULTI_PERMISSION);
                                Log.d(TAG, "showMessageOKCancel requestPermissions");
                            }
                        });
            } else {
                grant.onPermissionGranted(CODE_MULTI_PERMISSION);
            }
    
        }
        
        
* 关于权限请求结果的回调。Activity实现ActivityCompat.OnRequestPermissionsResultCallback接口，重写onRequestPermissionsResult方法。    

	       @Override
	        public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
	                                               @NonNull int[] grantResults) {
	            PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
	    
	        }

 