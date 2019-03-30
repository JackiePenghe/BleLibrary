# BleSample
BleSample

RecyclerView Adapter use BRVAH 


由于更新维护会经常修改代码，下方的配置以及依极有可能未及时更新，有需要可下载代码查看

配置：(Configure)

1.直接将library依赖到项目中（Download and copy library "blelibrary" to your project）

2.gradle配置依赖(gradle dependency)
support版本
```xml
compile 'com.sscl.blelibrary:support:0.0.1'
```
androidx版本
```xml
compile 'com.sscl.blelibrary:x:0.0.1'
```

###  权限配置：(Configuration permissions)
```xml
<!--蓝牙权限(Declare the Bluetooth permission(s) in your application manifest file)-->
   <uses-permission android:name="android.permission.BLUETOOTH" />
   <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
<!--BLE权限(If you want to declare that your app is available to BLE-capable devices only, include the following in your app's manifest)-->
    <uses-feature
       android:name="android.hardware.bluetooth_le"
        android:required="true" />
<!-- 5.0以上的手机可能会需要这个权限(If your Android version is above 21 (including 21(Android 5.0)),maybe need declare this permission) -->
<uses-feature android:name="android.hardware.location.gps" />
<!-- 6.0的手机需要定位权限权限 (If your Android version is above 23 (including 23(Android 6.0)),must declare this permission) -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
```
### 判断设备本身是否支持BLE：(Determine whether the device itself supports Bluetooth Low Energy)
```java
if(!BleManager.isSupportBle()){
  Log.w(TAG,"device not supprot BLE");
  return;
}
//TODO continue
```
### BLE扫描：(Search for BLE devices)
```java
    /**
     * 初始化扫描器(Initialization scanner)
     */
    private void initBleScanner() {

        //获取扫描器单例（get Blescanner instance）
        bleScanner = BleManager.getBleScannerInstance(DeviceListActivity.this);
        //你也可以直接创建一个扫描器实例，但是我并不推荐(You can also create a scanner instance directly, but I don't recommend it.)
        //bleScanner = BleManager.newBleScanner(DeviceListActivity.this);
        
        //如果手机不支持蓝牙的话，这里得到的是null,所以需要进行判空(If the phone doesn't support Bluetooth, here is null.So, we need to judge whether it's null here. )
        if(bleScanner == null){
            Tool.toastL(DeviceListActivity.this,R.string.ble_not_supported);
            return;
        }
        /*
         * 打开扫描器，并设置相关回调(open sacanner,and set some callback)
         * @param scanResults                  扫描到的设备结果存放列表(this list used to store the scanned equipment result)
         * @param onScanFindOneNewDeviceListener 发现一个新设备的回调(this callback will be called when found a new device)
         * @param scanPeriod                   扫描持续时间，单位：毫秒(The duration of the scan. Unit:ms)
         * @param scanContinueFlag             是否在扫描完成后立即进行下一次扫描的标志(A sign determines whether to start next scan after completing the scan.)
         *                                     为true表示一直扫描，永远不会调用BleInterface.OnScanCompleteListener，(if true,means that scanning will never stop unless stopScan () is invoked. And the callback "onScanCompleteListener" will never be called.)
         *                                     为false，在时间到了之后回调BleInterface.OnScanCompleteListener，然后结束.(If false,scanner will stop after "scanPeriod" ms,and call onScanCompleteListener.finish a scan)
         * @param onScanCompleteListener       扫描完成的回调(this callback will be called when san finished)
         * @return true表示打开成功(if true,means scanner open succeed)
         */
        bleScanner.open(scanList, onScanFindOneNewDeviceListener, 10000, false, onScanCompleteListener);

        //设置其他回调(you can also set other callbacks)
        bleScanner.setOnScanFindOneDeviceListener(onScanFindOneDeviceListener);
        //开始扫描（start scan）
        blescanner.startScan();
    }
```


### BLE扫描进阶设置(需要API21支持)(BLE scanner advanced settings(API 21 supported))
#### 设置过滤条件(Configure filters)
```java
    /**
     * 设置扫描器的过滤条件(set scan filters)
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setScanFilters() {
        //声明过滤集合,可同时设置多组过滤条件(Declaring a filter list to set multiple set of filtering conditions at the same time)
        ArrayList<ScanFilter> scanFilters = new ArrayList<>();
        //声明服务UUID(Declaring service uuid)
        String serviceUUID = "C3E6FEA0-E966-1000-8000-BE99C223DF6A";
        ScanFilter scanFilter = new ScanFilter.Builder()
                //设置过滤设备地址(Device address filtering setting)
                .setDeviceAddress("00:02:5B:00:15:AA")
                //设置过滤设备名称(Device name filtering setting)
                .setDeviceName("Y11-")
                //根据厂商自定义的广播id和广播内容过滤(Device manufacturer data filtering setting)
                .setManufacturerData(2, new byte[]{0, 2})
                //根据服务数据进行过滤(Device service uuid filtering setting)
                .setServiceUuid(new ParcelUuid(UUID.fromString(serviceUUID)))
                //构建(build filter)
                .build();
        //添加一个过滤到过滤集合中(add a filter to filter list)
        scanFilters.add(scanFilter);
        //设置过滤条件(set scanner filters)
        bleScanner.setScanFilters(scanFilters);
    }
```
#### 设置扫描参数(Configure scan paramters)
```java
     /**
     * 设置扫描器的扫描参数(set scan settings)
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setScanSettings() {
        ScanSettings scanSettings = new ScanSettings.Builder()
                //设置回调触发方式（需要API23及以上）(set callback type(API 23 supported))
//                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                //如果只有传统（我猜测是经典蓝牙，并不确定）的广播，是否回调callback函数(需要API26及以上)
                // Set whether only legacy advertisments should be returned in scan results.
                //Legacy advertisements include advertisements as specified by the
                //Bluetooth core specification 4.2 and below. This is true by default
                //for compatibility with older apps.
                //true if only legacy advertisements will be returned
//                    .setLegacy(false)
                //设置扫描匹配方式（需要API23及以上）(set match mode(API 23 supported))
//                    .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                //设置扫描匹配次数（需要API23及以上）(set num of matches(API 23 supported))
//                    .setNumOfMatches(2)
                //在扫描过程中设置物理层(需要API23及以上)(set phy(API 23 supported))
//                    .setPhy(BluetoothDevice.PHY_LE_1M)
                //设置报告延迟时间(set report delay)
                .setReportDelay(100)
                //设置扫描模式(set scan mode(default mode:ScanSettings.SCAN_MODE_LOW_LATENCY))
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                //构建
                .build();
        //设置扫描参数(set scan settings)
        bleScanner.setScanSettings(scanSettings);
    }
```
### 注销：(close and release memory)
一定要记得在activity被销毁之前，注销扫描器(You must close the scanner before the activity is destroyed)

```java
    /**
     * 在activity被销毁的时候关闭扫描器
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭扫描器(close scanner)
        bleScanner.close();
        BleManager.releaseBleScanner();
    }
```
在程序完全退出的时候，一定要执行这一句（When the program exits, it is necessary to release memory）
```
BleManager.releaseAll();
```

### BLE设备的连接：（BLE Connection）

在进行连接之前，一定要检查是否在AndroidManifest中配置已一个必须的服务！(Connections depend on this service)

```xml 
 <service
            android:name="com.jackiepenghe.blelibrary.BluetoothLeService"
            android:enabled="true"
            android:exported="false" />

``` 
接下来就是Java代码了（The next is the Java code）

```java


    /**
     * 初始化连接工具(Initialization connector)
     */
    private void initBleConnector() {
        //获取BLE连接器单例(get Connector instance)
        bleConnector = BleManager.getBleConnectorInstance(ConnectActivity.this);
        //创建一个新的连接器实例（create a new connector instance）
        //bleConnector = BleManager.getBleConnectorInstance(ConnectActivity.this);
       //如果手机不支持蓝牙的话，这里得到的是null,所以需要进行判空(If the phone doesn't support Bluetooth, here is null.So, we need to judge whether it's null here. )
        if (bleConnector == null) {
            Tool.toastL(ConnectActivity.this, R.string.ble_not_supported);
            return;
        }
        //设置连接设备成功的回调(set callback that called when connected remote device)
        bleConnector.setOnConnectedListener(onConnectedListener);
        //设置连接之后，服务发现完成的回调(set callback that called when discover remote device services finished)
        bleConnector.setOnServicesDiscoveredListener(onServicesDiscoveredListener);
        //设置连接被断开的回调(set callback that called when disconnected remote device)
        bleConnector.setOnDisconnectedListener(onDisconnectedListener);
        //设置 读取到设备的数据时的回调(set callback that called when read data from remote device)
        bleConnector.setOnCharacteristicReadListener(onCharacteristicReadListener);
        //设置 获取设备的RSSI的回调(set callback that called when get remote device rssi)
        bleConnector.setOnReadRemoteRssiListener(onReadRemoteRssiListener);     
        //设置 Mtu参数被更改时的回调
        bleConnector.setOnMtuChangedListener(onMtuChangedListener);
    }
```
#### 注意：请不要在“onConnectedListener”回调中判断为设备连接成功。有些情况下，在连接上设备之后会立刻断开。所以一定要在“onServicesDiscoveredListener”中判断为设备已经连接成功。
#### notice:If you want do something about BLE after connected remote device, please do it in callback "onServicesDiscoveredListener" Instead of callback "onConnectedListener".

发起连接请求（Initiating a connection request ）

```
private void startConnect() {
        //设置要连接的设备，并发起连接(set remote device,and start connect)
        if (bleConnector.checkAndSetDevice(bluetoothDevice)) {
           if (bleConnector.startConnect()) {
                LogUtil.w("开始连接(connecting)");    
            } else {
                LogUtil.w("连接失败(connect failed)");              
            }
            
            /*
            //发起连接时传入true代表断链后自动重连(If you set true here,system will be reconnect while remote device unexpected disconnected)
            if (bleConnector.startConnect(true)) {
                LogUtil.w("开始连接(connecting)");    
            } else {
                LogUtil.w("连接失败(connect failed)");              
            }*/
        }
    }
```

在连接成功之后，可以获取远端设备的服务列表(If Callback BleInterface.OnServicesDiscoveredListener was triggered,you can get remote device services now)
```java
List<BluetoothGattService> deviceServices = bleConnector.getServices();
```

对目标进行数据的传输(Data sending and reading)

发送数据(send data)
```java
boolean succeed = bleConnector.writeData(serviceUUID,characteristicUUID,value);
```

获取数据(read data)
```java
boolean succeed = bleConnector.readData(serviceUUID,characteristicUUID);
```

上面的发送与获取数据的方法返回的都是boolean类型，代表命令执行成功或失败(其实bleConnector的函数基本上都是返回boolean类型的)
(The above method of sending and reading data is returned by the boolean type.And the returned value mean whether the execution of the command is successful or not)

获取到的数据在回调中查看(When the data is read, this callback will be triggered)
```java
bleConnector.setOnCharacteristicReadListener(onCharacteristicReadListener);
```

还有通知(Notification)

打开通知：(open notification)
```java
bleConnector.enableNotification(serviceUUID,characteristicUUID,true);
```

关闭通知(close notification)
```java
bleConnector.enableNotification(serviceUUID,characteristicUUID,false);
```

接收到远端设备通知的回调(After receiving the notification from the remote device, this callback is triggered)
```java
bleConnector.setOnReceiveNotificationListener(onReceiveNotificationListener);
```

还有一些其他的回调，可以自己下载源码或者查看API文档，根据实际需求使用（There are some other callbacks that you can download by yourself or look at API documents ,and use them.）

销毁(close)

在准备销毁activity的时候，调用close方法。推荐在此处屏蔽super.onBackpressed()方法。(If you want to destroy Activity, please remember to close the connector before the destruction.It is recommended to close the connection in the onBackPressed or onPause method and shielded super.onBackpressed() method)
```java
    @Override
    public void onBackPressed() {
        boolean closeResult = bleConnector.close();
        if(!closeResult){
          super.onBackPressed();
        }
    }
```

然后在回调中销毁activity(Then destroy activity in this callback)
```java
BleConnector.OnCloseCompleteListener onCloseCompleteListener;
onCloseCompleteListener = new BleConnector.OnCloseCompleteListener() {
            @Override
            public void onCloseComplete() {
                //ThisActivity.super.onBackPressed();
                finish();
            }
        };
bleConnector.setOnCloseCompleteListener(onCloseCompleteListener);
```

在销毁Activity的时候，释放内存
```
@Override
public void onDestroy() {
  super.onDestroy();
  BleManager.realseBleConnector();
}
```
在程序完全退出的时候，一定要执行这一句（When the program exits, it is necessary to release memory）
```
BleManager.releaseAll();
```

### BLE设备的绑定(也可以说是配对)：(Bluetooth pairing)

```java
        /*
         * 调用绑定的方法（如果需要绑定)，否则请直接调用连接的方法
         * 注意：如果该设备不支持绑定，会直接回调绑定成功的回调，在绑定成功的回调中发起连接即可
         * 第一次绑定某一个设备会触发回调，之后再次绑定，可根据绑定时的函数的返回值来判断绑定状态，以进行下一步操作
         * (Start bindings
         * notice:If remote device not supoort pairing,BleConstants.DEVICE_BOND_BONDED will be returned.
         * The first binding of a device triggers a callback(BleInteface.OnDeviceBondStateChangedListener).Please look down. )
         */
        switch (bleConnector.startBound(address)) {
            case BleConstants.DEVICE_BOND_START_SUCCESS:
                LogUtil.w(TAG, "开始绑定(Start binding)");
                break;
            case BleConstants.DEVICE_BOND_START_FAILED:
                LogUtil.w(TAG, "发起绑定失败(Failed to initiate binding)");
                break;
            case BleConstants.DEVICE_BOND_BONDED:
                LogUtil.w(TAG, "此设备已经被绑定了(This device is already bound)");
                startConnect();
                break;
            case BleConstants.DEVICE_BOND_BONDING:
                LogUtil.w(TAG, "此设备正在绑定中(This device is binding)");
                break;
            case BleConstants.BLUETOOTH_ADAPTER_NULL:
                LogUtil.w(TAG, "没有蓝牙适配器存在(No Bluetooth adapter exists)");
                break;
            case BleConstants.BLUETOOTH_ADDRESS_INCORRECT:
                LogUtil.w(TAG, "蓝牙地址错误(Bluetooth address is wrong)");
                break;
            case BleConstants.BLUETOOTH_MANAGER_NULL:
                LogUtil.w(TAG, "没有蓝牙管理器存在(No Bluetooth manager exists)");
                break;
            default:
                LogUtil.w(TAG, "default");
                break;
        }
```
相关的回调是：(Related callback)
```java
  //设备的绑定(也可以说配对)状态改变后触发此回调(This callback is triggered after the binding status of the device changes)
        BleInterface.OnDeviceBondStateChangedListener onBondStateChangedListener = new BleInterface.OnDeviceBondStateChangedListener() {
            /**
             * 正在绑定设备( device is binding)
             */
            @Override
            public void onDeviceBinding() {

            }

            /**
             * 绑定完成(device is already bound(Bind success))
             */
            @Override
            public void onDeviceBonded() {
                //发起连接(Initiating a connection request)
                startConnect();
            }

            /**
             * 取消绑定或者绑定失败(Unbinding or binding failed)
             */
            @Override
            public void onDeviceBindNone() {

            }
        };
        //设置绑定的回调(set callback)
         bleConnector.setOnBondStateChangedListener(onBondStateChangedListener);
```
### 多连接(Multi-connection)

首先要在AndroidManifest.xml添加一个服务(Multi-connection depend on this service)
```xml
<service android:name="com.jackiepenghe.blelibrary.BluetoothMultiService"
  android:enabled="true"
  android:exported="false"/>
```
获取多连接的连接器(Get multiple connectors)
```java
BleMultiConnector bleMultiConnectorWeakReference = BleManager.getBleMultiConnector(context);
```
连接多个设备(Connect multiple devices)
```java
    String device1Address = "00:02:5B:00:15:A4";
    String device2Address = "00:02:5B:00:15:A2";

    //使用默认的回调连接(Use the default callback to connect)
//  bleMultiConnector.connect(device1Address);
//  bleMultiConnector.connect(device2Address);

    //发起连接，并在意外断开后尝试自动重连
    //(Start the connection and automatically try to reconnect after the accidental disconnection.true indicates that you want to automatically try to reconnect after an unexpected connection failure.)
    bleMultiConnector.connect(device1Address,true);
    bleMultiConnector.connect(device2Address,true);

    //连接时传入对应的回调，方便对设备进行操作,通常使用这个方法(The corresponding callbacks are passed in to facilitate the operation of the device, usually using this method)
//  bleMultiConnector.connect(device1Address, device1BleCallback);
//  bleMultiConnector.connect(device2Address, device2BleCallback);


    //连接时传入对应的回调，方便进行操作,并且在连接断开之后自动尝试连接（系统会默认自动去连接该设备，这是系统自身的重连参数，推荐用这个参数进行重连）(The corresponding callback is passed in when the connection is made, and the connection is automatically tried after the accidental disconnection)
//  bleMultiConnector.connect(device1Address,device1BleCallback,true);
//  bleMultiConnector.connect(device2Address,device2BleCallback,true);
```
上方的callback是继承自BaseConnectCallback(The callback above is inherited from BaseConnectCallback)
```

public class Device1BleCallback extends BaseConnectCallback {
    /**
     * 蓝牙连接后无法正常进行服务发现调用此函数(This function is called when service discovery fails after Bluetooth connection)
     *
     * @param gatt BluetoothGatt
     */
    @Override
    public void onDiscoverServicesFailed(BluetoothGatt gatt) {
        
    }

    /**
     * 蓝牙GATT被关闭时调用此函数(This function is called when BluetoothGATT is closed)
     *
     * @param address 关闭GATT时，对应的设备地址
     */
    @Override
    public void onGattClosed(String address) {

    }

    /**
     * 当蓝牙客户端配置失败时调用此函式(This function is called when Bluetooth client configuration fails)
     *
     * @param gatt        蓝牙客户端(BluetoothGATT)
     * @param methodName  方法名(method name)
     * @param errorStatus 错误状态码(error status code )
     */
    @Override
    public void onBluetoothGattOptionsNotSuccess(BluetoothGatt gatt, String methodName, int errorStatus) {

    }
}

```
同时连接多个设备后，如果想要对单独某一个设备进行操作(After connecting multiple devices, if you want to operate on a single device)
```java
BleDeviceController bleDeviceController =  bleMultiConnectorWeakReference.getBleDeviceController(address);
bleDeviceController.writData(serviceUUID,characteristicUUID,data);
...
```
在程序退出时或者当前Activity销毁前close(Close the multi-connector before exiting or before the current activity is destroyed)
```java
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //最好是先清空一下缓存(It is best to empty the cache first)
        bleMultiConnectorWeakReference.refreshAllGattCache();
        //关闭所有gatt(Close all gatt)
        bleMultiConnectorWeakReference.closeAll();
    }
 
```

### 蓝牙广播（Android Bluetooth LE Peripheral）
这是在安卓5.0（API21）之后加入的库，用于蓝牙BLE广播，较常用与iBeacon数据广播。以下的用法都是在API21及以上的时候使用。（iBeacon此处我就不详细去说了，请看下方的用法即可）
(This is a library added after Android 5.0 (API 21) for Bluetooth BLE broadcasts.)


获取蓝牙广播实例(Get the Bluetooth broadCast instance)
```java
 private BleBroadCastor bleBroadCastor;
 //获取一个新的广播实例(get a new Bluetooth broadCast instance)
 //bleBroadCastor = BleManager.newBleBroadCastor(this);
 //获取单例(Get a singleton)
 bleBroadCastor = BleManager.getBleBroadCastor(this);
```
初始化(initialization)
```java
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//默认的初始化(default initialization)
//        bleBroadCastor.init();
            //服务UUID (service uuid)
            String serviceUUID = "C3E6FEA0-E966-1000-8000-BE99C223DF6A";
            //广播设置(broadcast settings)
            AdvertiseSettings advertiseSettings = new AdvertiseSettings.Builder()
                    //设置广播的模式(broadcast mode)
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                    //设置是否可连接(set connectable)
                    .setConnectable(true)
                    //设置广播时间（0为永不停止，直到回调stopAdvertising()）(set broadcast timeout)
                    .setTimeout(0)
                    //设置广播功率等级（等级越高，信号越强，也更加耗电）(set Tx power level)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                    //构建(build settings)
                    .build();
            //ParcelUuid
            ParcelUuid parcelUuid = new ParcelUuid(UUID.fromString(serviceUUID));
            //广播数据(advertise data )
            AdvertiseData advertiseData = new AdvertiseData.Builder()
                    //设置广播内容是否包含信号发送等级
                    .setIncludeTxPowerLevel(true)
                    //设置广播内容是否包含蓝牙名称（此名称为在手机蓝牙设置中的蓝牙名称）
                    .setIncludeDeviceName(true)
                    //设置厂商自定义广播数据
                    .addManufacturerData(2, new byte[]{2, 1})
                    //添加服务UUD
                    .addServiceUuid(parcelUuid)
                    //添加服务UUD与数据
                    .addServiceData(parcelUuid, new byte[]{2, 2, 5})
                    .build();
            //广播回调(callback for  advertise)
            AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
                /**
                 * Callback triggered in response to {@link BluetoothLeAdvertiser#startAdvertising} indicating
                 * that the advertising has been started successfully.
                 *
                 * @param settingsInEffect The actual settings used for advertising, which may be different from
                 *                         what has been requested.
                 */
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    Tool.warnOut(TAG, "onStartSuccess");
                    if (settingsInEffect != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Tool.warnOut(TAG, "onStartSuccess TxPowerLv=" + settingsInEffect.getTxPowerLevel() + " mode=" + settingsInEffect.getMode()
                                    + " timeout=" + settingsInEffect.getTimeout());
                        }
                    } else {
                        Tool.warnOut(TAG, "onStartSuccess, settingInEffect is null");
                    }
                    Tool.warnOut(TAG, "onStartSuccess settingsInEffect" + settingsInEffect);
                }

                /**
                 * Callback when advertising could not be started.
                 *
                 * @param errorCode Error code (see ADVERTISE_FAILED_* constants) for advertising start
                 *                  failures.
                 */
                @Override
                public void onStartFailure(int errorCode) {
                    Tool.warnOut(TAG, "onStartFailure");
                    if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE) {
                        Tool.errorOut(TAG, "Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
                    } else if (errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) {
                        Tool.errorOut(TAG, "Failed to start advertising because no advertising instance is available.");
                    } else if (errorCode == ADVERTISE_FAILED_ALREADY_STARTED) {
                        Tool.errorOut(TAG, "Failed to start advertising as the advertising is already started");
                    } else if (errorCode == ADVERTISE_FAILED_INTERNAL_ERROR) {
                        Tool.errorOut(TAG, "Operation failed due to an internal error");
                    } else if (errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED) {
                        Tool.errorOut(TAG, "This feature is not supported on this platform");
                    }
                }
            };
            //初始化(initialization)
            bleBroadCastor.init(advertiseSettings, advertiseData, advertiseData, advertiseCallback);
        }
 
```
当设置手机广播可被连接时，需要设置此回调来处理远端设备的通讯，设置不可连接时，可以不设置回调。（即便是可连接时，不设置此回调也不会有问题，但是这样会导致无法进行任何操作）
(This callback needs to be set to handle the communication of the remote device when setting up the cellphone broadcast. When the setting is not connectable, the callback may not be set.)
```java
BleInterface.OnBluetoothGattServerCallbackListener onBluetoothGattServerCallbackListener = new BleInterface.OnBluetoothGattServerCallbackListener() {
            /**
             * Callback indicating when a remote device has been connected or disconnected.
             *
             * @param device   Remote device that has been connected or disconnected.
             * @param status   Status of the connect or disconnect operation.
             * @param newState Returns the new connection state. Can be one of
             *                 {@link BluetoothProfile#STATE_DISCONNECTED} or
             *                 {@link BluetoothProfile#STATE_CONNECTED}
             */
            @Override
            public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {

            }

            /**
             * Indicates whether a local service has been added successfully.
             *
             * @param status  Returns {@link BluetoothGatt#GATT_SUCCESS} if the service
             *                was added successfully.
             * @param service The service that has been added
             */
            @Override
            public void onServiceAdded(int status, BluetoothGattService service) {

            }

            /**
             * A remote client has requested to read a local characteristic.
             * <p>
             * <p>An application must call {@link BluetoothGattServer#sendResponse}
             * to complete the request.
             *
             * @param device         The remote device that has requested the read operation
             * @param requestId      The Id of the request
             * @param offset         Offset into the value of the characteristic
             * @param characteristic Characteristic to be read
             */
            @Override
            public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {

            }

            /**
             * A remote client has requested to write to a local characteristic.
             * <p>
             * <p>An application must call {@link BluetoothGattServer#sendResponse}
             * to complete the request.
             *
             * @param device         The remote device that has requested the write operation
             * @param requestId      The Id of the request
             * @param characteristic Characteristic to be written to.
             * @param preparedWrite  true, if this write operation should be queued for
             *                       later execution.
             * @param responseNeeded true, if the remote device requires a response
             * @param offset         The offset given for the value
             * @param value          The value the client wants to assign to the characteristic
             */
            @Override
            public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {

            }

            /**
             * A remote client has requested to read a local descriptor.
             * <p>
             * <p>An application must call {@link BluetoothGattServer#sendResponse}
             * to complete the request.
             *
             * @param device     The remote device that has requested the read operation
             * @param requestId  The Id of the request
             * @param offset     Offset into the value of the characteristic
             * @param descriptor Descriptor to be read
             */
            @Override
            public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {

            }

            /**
             * A remote client has requested to write to a local descriptor.
             * <p>
             * <p>An application must call {@link BluetoothGattServer#sendResponse}
             * to complete the request.
             *
             * @param device         The remote device that has requested the write operation
             * @param requestId      The Id of the request
             * @param descriptor     Descriptor to be written to.
             * @param preparedWrite  true, if this write operation should be queued for
             *                       later execution.
             * @param responseNeeded true, if the remote device requires a response
             * @param offset         The offset given for the value
             * @param value          The value the client wants to assign to the descriptor
             */
            @Override
            public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {

            }

            /**
             * Execute all pending write operations for this device.
             * <p>
             * <p>An application must call {@link BluetoothGattServer#sendResponse}
             * to complete the request.
             *
             * @param device    The remote device that has requested the write operations
             * @param requestId The Id of the request
             * @param execute   Whether the pending writes should be executed (true) or
             */
            @Override
            public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {

            }

            /**
             * Callback invoked when a notification or indication has been sent to
             * a remote device.
             * <p>
             * <p>When multiple notifications are to be sent, an application must
             * wait for this callback to be received before sending additional
             * notifications.
             *
             * @param device The remote device the notification has been sent to
             * @param status {@link BluetoothGatt#GATT_SUCCESS} if the operation was successful
             */
            @Override
            public void onNotificationSent(BluetoothDevice device, int status) {

            }

            /**
             * Callback indicating the MTU for a given device connection has changed.
             * <p>
             * <p>This callback will be invoked if a remote client has requested to change
             * the MTU for a given connection.
             *
             * @param device The remote device that requested the MTU change
             * @param mtu    The new MTU size
             */
            @Override
            public void onMtuChanged(BluetoothDevice device, int mtu) {

            }

            /**
             * Callback triggered as result of {@link BluetoothGattServer#setPreferredPhy}, or as a result
             * of remote device changing the PHY.
             *
             * @param device The remote device
             * @param txPhy  the transmitter PHY in use. One of {@link BluetoothDevice#PHY_LE_1M},
             *               {@link BluetoothDevice#PHY_LE_2M}, and {@link BluetoothDevice#PHY_LE_CODED}
             * @param rxPhy  the receiver PHY in use. One of {@link BluetoothDevice#PHY_LE_1M},
             *               {@link BluetoothDevice#PHY_LE_2M}, and {@link BluetoothDevice#PHY_LE_CODED}
             * @param status Status of the PHY update operation.
             *               {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
             */
            @Override
            public void onPhyUpdate(BluetoothDevice device, int txPhy, int rxPhy, int status) {

            }

            /**
             * Callback triggered as result of {@link BluetoothGattServer#readPhy}
             *
             * @param device The remote device that requested the PHY read
             * @param txPhy  the transmitter PHY in use. One of {@link BluetoothDevice#PHY_LE_1M},
             *               {@link BluetoothDevice#PHY_LE_2M}, and {@link BluetoothDevice#PHY_LE_CODED}
             * @param rxPhy  the receiver PHY in use. One of {@link BluetoothDevice#PHY_LE_1M},
             *               {@link BluetoothDevice#PHY_LE_2M}, and {@link BluetoothDevice#PHY_LE_CODED}
             * @param status Status of the PHY read operation.
             *               {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
             */
            @Override
            public void onPhyRead(BluetoothDevice device, int txPhy, int rxPhy, int status) {

            }
        };
        bleBroadCastor.setOnBluetoothGattServerCallbackListener(onBluetoothGattServerCallbackListener);
```
开始广播(start advertising)
```java
  if (bleBroadCastor != null) {
            boolean b = bleBroadCastor.startAdvertising();
            Tool.warnOut(TAG, "startAdvertising = " + b);
            if (b) {
                Tool.warnOut(TAG, "广播请求发起成功（是否真的成功，在init传入的advertiseCallback回调中查看）");
            }else {
                Tool.warnOut(TAG, "广播请求发起失败（这是真的失败了，连请求都没有发起成功）");
            }
        }
```
停止广播并关闭实例(stop advertising and close broadcast instance)
```java
 /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (bleBroadCastor != null) {
            //停止广播(stop advertising )
            bleBroadCastor.stopAdvertising();
            //关闭广播实例(close broadcast instance)
            bleBroadCastor.close();
        }
    }
```
# 特别注意
## 连接与扫描
安卓手机因为系统各个厂家定制的原因，可能会有一些莫名其妙的问题。如：UUID发现后跟设备本身不一致等。这种问题通常可以通过重启蓝牙解决。但是也有那种顽固无比的手机。如：三星盖乐世3.这个手机必须要回复出厂设置才能正确发现UUID，原因是：系统记录了同一个设备地址的UUID。一旦连接的是同一个地址，UUID第一次发现之后，后续不论怎么更改设备的UUID，系统的缓存都是不会更新的。对于这种手机，只想说：别用BLE了。没救了
## 广播
对于手机来说，广播的时候，广播的地址会不断的变化，且不同厂商对这个变化周期有不同的设置，所以这种广播一般不推荐别人连接。仅用于广播数据却非常合适。如果有谁知道怎么关掉地址切换或设置地址不变的高手请请留下您的建议与方案。我会尽力完善

