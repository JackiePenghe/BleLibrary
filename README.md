

# BleSample
BleSample

这个库我自己已经使用了很长一段时间了，我自己也经常维护。现在感觉已经比较稳定，所以就大胆拿出来分享一下，希望能帮助到正在准备进行BLE开发或进入BLE开发不久的安卓程序员们。

这个库最开始我只是为了自己做调试的时候使用，后来写着写着就把它写成了一个库，于是就慢慢的维护了起来

由于个人原因，此库已迁移到 码云  [传送门](https://gitee.com/sscl/BleLibrary)

RecyclerView Adapter use BRVAH [quick jump](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)

RecyclerView 适配器使用 BRVAH [传送门](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)



由于更新维护会经常修改代码，下方的配置以及依赖有可能未及时更新，有需要可下载代码查看

配置：(Configure)

1.直接将library依赖到项目

2.gradle配置依赖
support版本，最高支持到API28,已经完全停止维护，并不再更新
```xml
implementation 'com.sscl.blelibrary:support:0.0.9'
```
androidx版本，可支持到最新版API，目前最高API30（截至2020-07-31）
```xml
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

```xml
dependencies {
        implementation 'com.github.JackiePenghe:BaseLibrary:version'
}
```
一下所有示例均以androidx版本为准：

###  权限配置：
权限默认已经集成在依赖库中了，但是此处还是列出来做参考
```xml
<!--蓝牙权限-->
   <uses-permission android:name="android.permission.BLUETOOTH" />
   <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
<!--BLE权限-->
    <uses-feature
       android:name="android.hardware.bluetooth_le"
        android:required="true" />
<!-- 5.0以上的手机可能会需要这个权限 -->
<uses-feature android:name="android.hardware.location.gps" />
<!-- 6.0及以上的手机需要定位权限权限 -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>

    <application>
        <!--    这个服务用于BLE标准单个连接    -->
        <service
            android:name=".BluetoothLeService"
            android:enabled="true"
            android:exported="false" />
        <!--    这个服务用于BLE多设备连接    -->
        <service
            android:name=".BluetoothMultiService"
            android:enabled="true"
            android:exported="false" />
    </application>
```
### 初始化
在程序的Application类中初始化
```java

public class MyApplication extends Application {
    @Override
    public void onCreate() {
         super.onCreate();
         //初始化
        BleManager.init(MyApplication.this);
        //开启调试信息打印
        com.sscl.blelibrary.DebugUtil.setDebugFlag(true);
    }
}
```

### 判断设备本身是否支持BLE：
```java
if(!BleManager.isSupportBle()){
  //如果设备不支持BLE，在这里执行操作
  return;
}
//如果设备支持BLE，继续往下操作
```
### BLE扫描：
```java
     /**
     * 初始化扫描器
     */
    private void initBleScanner() {
        //当安卓系统在Android O及以上时，可以判断系统是否支持BLE5.0（在此之前，手机上的BLE版本一般为4.0）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (BleManager.isLeCodedPhySupported()) {
                ToastUtil.toastLong(this, R.string.le_coded_supported);
            }
        }
        //创建扫描器实例
        bleScanner = BleManager.getBleScannerInstance();
        //如果手机不支持蓝牙的话，这里得到的是null,所以需要进行判空
        if (bleScanner == null) {
            ToastUtil.toastLong(DeviceListActivity.this, R.string.ble_not_supported);
            return;
        }
        //设置扫描周期，扫描会在自动在一段时间后自动停止
        bleScanner.setScanPeriod(10000);
        //设置是否一直持续扫描，true表示一直扫描，false表示在扫描结束后不再进行扫描
        bleScanner.setAutoStartNextScan(true);
        //设置BLE扫描模式，此处为默认值
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bleScanner.setBleScanMode(BleScanMode.LOW_LATENCY);
        }
        //设置BLE匹配模式，此处为默认值
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bleScanner.setBleMatchMode(BleMatchMode.AGGRESSIVE);
        }
        //设置BLE的回调类型和匹配数量
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bleScanner.setBleCallbackType(BleCallbackType.CALLBACK_TYPE_ALL_MATCHES);
            bleScanner.setBleNumOfMatches(BleNumOfMatches.MATCH_NUM_MAX_ADVERTISEMENT);
        }
         //设置BLE的模式和扫描信道（扫描信道一般只有在扫描BLE5.0的设备不可见时才会使用到）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bleScanner.setLegacy(false);
            bleScanner.setScanPhy(ScanPhy.PHY_LE_CODED);
        }
        //设置相关回调
        bleScanner.setOnBleScanStateChangedListener(onBleScanStateChangedListener);
        //初始化BLE扫描器
        bleScanner.init();
    }
```


### BLE扫描进阶设置
#### 设置过滤条件
```java
    /**
     * 设置扫描器的过滤条件
     */
    private void setScanFilters() {
       //全地址匹配
       bleScanner.addFilterFullAddress("AA:BB:CC:DD:EE:FF");
       //全名称匹配
       bleScanner.addFilterFullName("Test Name");
       //地址前部一致时匹配
       bleScanner.addFilterStartsAddress("AA:BB");
       //名称前部一致时匹配
       bleScanner.addFilterStartsName("Te");
       //设备的蓝牙广播中包含某个服务的UUID时匹配
       bleScanner.addFilterUuid("00001800-0000-1000-8000-00805f9b34fb");
    }
    
    /**
     * 移除扫描器的过滤条件
     */
    private void setScanFilters() {
       bleScanner.removeFilterxxx();
    }
```
### 注销：
一定要记得在activity被销毁之前，注销扫描器

```java
    /**
     * 在activity被销毁的时候关闭扫描器
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭扫描器
        bleScanner.close();
        //释放资源
        BleManager.releaseBleScannerInstance();
    }
```
在程序完全退出的时候，一定要执行这一句，释放所有占用的内存
```
BleManager.releaseAll();
```

### BLE设备的连接：

```java

     /**
     * 初始化连接工具
     */
    private void initBleConnector() {
        //创建BLE连接器实例
        bleConnector = BleManager.getBleConnectorInstance();
        //如果手机不支持蓝牙的话，这里得到的是null,所以需要进行判空
        if (bleConnector == null) {
            ToastUtil.toastLong(ConnectActivity.this, R.string.ble_not_supported);
            return;
        }
        bleConnector.setConnectTimeOut(10000);
        setConnectListener();
    }

    /**
     * 设置相关的监听回调
     */
    private void setConnectListener() {
        //设置 连接 相关的回调
        bleConnector.setOnBleConnectStateChangedListener(onBleConnectStateChangedListener);
        //设置发送分包数据时，每一包数据之间的延时
        bleConnector.setSendLargeDataPackageDelayTime(500);
        //设置发送分包数据时，每一包数据发送超时的时间
        bleConnector.setSendLargeDataTimeOut(10000);
    }
```
#### 注意：请不要在“onConnectedListener”回调中判断为设备连接成功。有些(异常)情况下，在连接上设备之后会立刻断开。所以一定要在“onServicesDiscoveredListener”中判断为设备已经连接成功。

发起连接请求

```
    /**
     * 发起连接
     */
    private void startConnect() {
        if (bleConnector.connect(bluetoothDevice)) {
            DebugUtil.warnOut("发起连接，正在连接");
        } else {
            DebugUtil.warnOut("发起连接失败");
        }

         //connect方法有很多重载参数，请根据实际情况自行判断场景并调用相关的重载方法
    }
```

在连接成功之后，可以获取远端设备的服务列表，这一步操作在封装时已经自动操作了，一般情况下无需再手动调用
```java
//手动获取设备的服务列表，通常情况下不需要执行
List<BluetoothGattService> deviceServices = bleConnector.getServices();
```

对目标进行数据的传输

向设备写入数据
```java

//发送数据时，需要知道目标设备的服务UUID，特征UUID，传输的value是一个数组，一般情况下长度不能大于20
boolean succeed = bleConnector.writeData(serviceUUID,characteristicUUID,value);

//写入是否成功一般需要在回调中判断
```

从设备读取数据
```java
//
boolean succeed = bleConnector.readData(serviceUUID,characteristicUUID);

//读取的结果在回调中获取
```

上面的发送与获取数据的方法返回的都是boolean类型，true代表命令执行中没有出错,false代表操作失败(其实bleConnector的函数基本上都是返回boolean类型的)

通知

打开通知：
```java
 boolean succeed = bleConnector.enableNotification(serviceUUID, characteristicUUID, true);
```

关闭通知
```java
 boolean succeed = bleConnector.enableNotification(serviceUUID, characteristicUUID, false);
```

注意：只有开启了通知，才会触发通知的回调

销毁

在Activity返回的时候，调用close方法。推荐在此处屏蔽super.onBackpressed()方法。此处仅作推荐与参考。请根据实际情况使用
```java
    @Override
    public void onBackPressed() {
        boolean closeResult = bleConnector.close();
        if(!closeResult){
          super.onBackPressed();
        }
    }
```

然后在回调中销毁activity
```java
 OnBleConnectStateChangedListener onBleConnectStateChangedListener = new OnBleConnectStateChangedListener() {
   ......
   ......

        @Override
        public void onCloseComplete() {
            //结束当前Activity
            finish();
        }
 }
```

在销毁Activity的时候，释放内存
```
@Override
public void onDestroy() {
  super.onDestroy();
  BleManager.releaseBleConnectorInstance();
}
```
在程序完全退出的时候，一定要执行这一句
```
BleManager.releaseAll();
```

### BLE设备的绑定(也可以说是配对，要求API 19)：

```java

 int boundState = bleConnector.startBound(deviceAddress);
        /*
         * 绑定设备，除非必要，否则请直接调用连接的方法
         * 注意：如果该设备不支持绑定，会直接回调绑定成功的回调，在绑定成功的回调中发起连接即可
         * 第一次绑定某一个设备会触发回调，之后再次绑定，可根据绑定时的函数的返回值来判断绑定状态，以进行下一步操作
         */
        switch (boundState) {
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

### 多连接(Multi-connection)

获取多连接的连接器(Get multiple connectors)
```java
 bleMultiConnector = BleManager.getBleMultiConnectorInstance();
```
连接多个设备(Connect multiple devices)
```java
    String device1Address = "00:02:5B:00:15:A4";
    String device2Address = "00:02:5B:00:15:A2";

    //使用默认的回调连接
//  bleMultiConnector.connect(device1Address);
//  bleMultiConnector.connect(device2Address);

    //发起连接，并在意外断开后尝试自动重连可用这个方法
    bleMultiConnector.connect(device1Address,true);
    bleMultiConnector.connect(device2Address,true);

   //在发起连接时传入相关设备的回调
  //  bleMultiConnector.connect(device1Address, device5BleCallback);
  //  bleMultiConnector.connect(device2Address, device5BleCallback);
```
上方的callback是继承自BaseBleConnectCallback
```

import com.sscl.blelibrary.BaseBleConnectCallback;

public class Device1BleCallback extends BaseBleConnectCallback {
   ........
   ........
}

```
同时连接多个设备后，如果想要对单独某一个设备进行操作
```java
//获取某个设备的操作类
BleDeviceController bleDeviceController = bleMultiConnector.getBleDeviceController(address);
//执行操作
bleDeviceController.writData(serviceUUID,characteristicUUID,data);
...
```
和BleConnector类似，在Activity返回时彻底关闭多连接器
```java
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //关闭所有gatt
        bleMultiConnectorWeakReference.closeAll();
    }
 
```

### 蓝牙广播
这是在安卓5.0（API21）之后加入的库，用于蓝牙BLE广播，较常用与iBeacon数据广播。以下的用法都是在API21及以上的时候使用。（iBeacon此处我就不详细去说了，请看下方的用法即可）


获取蓝牙广播实例
```java
 private BleBroadCastor bleBroadCastor;
 //获取单例
 //bleBroadCastor =  BleManager.getBleAdvertiserInstance();
```
初始化
```java
    bleAdvertiser.init()
```
参数设置
```java
      //广播包中可填充的数据
      AdvertiseData advertiseData = new AdvertiseData(0x0000FFFF, bytes);
      //设置广播包数据
      bleAdvertiser.addAdvertiseDataAdvertiseRecord(advertiseData);
      //设置广播包中是否包含设备名
      bleAdvertiser.setAdvertiseDataIncludeDeviceName(true);
      //设置响应包中是否包含设备名
      bleAdvertiser.setScanResponseIncludeDeviceName(false);
      //设置广播包中是否包含功率
      bleAdvertiser.setAdvertiseDataIncludeTxPowerLevel(true);
      //设置广播模式
      bleAdvertiser.setBleAdvertiseMode(BleAdvertiseMode.LOW_LATENCY);
      //设置是否可被连接
      bleAdvertiser.setConnectable(false);
      //设置广播时间，为0代表不自动停止广播
      bleAdvertiser.setTimeOut(20000);
```
回调设置
```java
 bleAdvertiser.setOnBleAdvertiseStateChangedListener(defaultOnBleAdvertiseStateChangedListener);
```
当设置手机广播可被连接时，需要设置此回调来处理远端设备的通讯，设置不可连接时，可以不设置回调。（即便是可连接时，不设置此回调也不会有问题，但是这样会导致无法进行任何操作）
```java
  bleAdvertiser.setOnBluetoothGattServerCallbackListener(defaultOnBluetoothGattServerCallbackListener);
```
开始广播(start advertising)
```java
  if (bleBroadCastor != null) {
            boolean b = bleBroadCastor.startAdvertising();
            Tool.warnOut(TAG, "startAdvertising = " + b);
            if (b) {
                Tool.warnOut(TAG, "广播请求发起成功（是否真的成功，在回调中查看）");
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
            //停止广播
            bleBroadCastor.stopAdvertising();
            //关闭广播实例
            bleBroadCastor.close();
        }
    }
```

# 其他情况：
获取扫描实例，连接实例，广播实例时可以根据实际情况使用如下方法

```java
BleManager.newBleMultiConnector();
BleManager.newBleAdvertiser();
BleManager.newBleConnector();
BleManager.newBleScanner();
```
通过以上方式得到的对应实例并非单例，每执行一次都会得到一个新的实例。所以需要对这些实例进行单独的针对性的释放。
```java
  BleManager.releaseBleMultiConnector(bleMultiConnector);
  BleManager.releaseBleAdvertiser(bleAdvertiser);
  BleManager.releaseBleConnector(bleConnector);
  BleManager.releaseBleScanner(bleScanner);
```
当然，也可以直接全部释放
```java
 BleManager.releaseAll();
```

# 特别注意
## 连接与扫描
安卓手机因为系统各个厂家定制的原因，可能会有一些莫名其妙的问题。

如：
手机连接设备后，发现的设备UUID跟设备本身存在的UUID不一致。

这种问题通常可以通过重启蓝牙解决，但是也有那种顽固无比的手机。如：三星 Galaxy 3。这个手机必须要回复出厂设置才能正确发现UUID，原因是：系统记录了同一个设备地址的UUID。一旦连接的是同一个地址，UUID第一次发现之后，后续不论怎么更改设备的UUID，系统的缓存都是不会更新的。对于这种手机，只想说：别用BLE了。没救了
## 广播
对于绝大多数手机来说，广播的时候，手机的广播地址会不断的变化，且不同厂商对这个变化周期有不同的设置，所以这种广播一般不推荐启用可连接。仅用于广播数据却非常合适。如果有谁知道怎么关掉地址切换或设置地址不变的高手请请留下您的建议与方案。我会尽力完善

