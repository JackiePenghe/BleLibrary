package com.sscl.blesample.adapter.entity.services_characteristics_list_entity;


import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.sscl.blesample.adapter.ServicesCharacteristicsListAdapter;


/**
 * @author jacke
 * @date 2018/1/22 0022
 */

public class CharacteristicUuidItem implements MultiItemEntity {

    private String name;
    private String uuid;
    private boolean canRead;
    private boolean canWrite;
    private boolean canNotify;

    public CharacteristicUuidItem(String name, String uuid, boolean canRead, boolean canWrite, boolean canNotify) {
        this.uuid = uuid;
        this.name = name;
        this.canRead = canRead;
        this.canWrite = canWrite;
        this.canNotify = canNotify;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBluetoothGattCharacteristic(String uuid) {
        this.uuid = uuid;
    }

    public boolean isCanRead() {
        return canRead;
    }

    public void setCanRead(boolean canRead) {
        this.canRead = canRead;
    }

    public boolean isCanWrite() {
        return canWrite;
    }

    public void setCanWrite(boolean canWrite) {
        this.canWrite = canWrite;
    }

    public boolean isCanNotify() {
        return canNotify;
    }

    public void setCanNotify(boolean canNotify) {
        this.canNotify = canNotify;
    }

    @Override
    public int getItemType() {
        return ServicesCharacteristicsListAdapter.TYPE_CHARACTERISTIC_UUID;
    }
}
