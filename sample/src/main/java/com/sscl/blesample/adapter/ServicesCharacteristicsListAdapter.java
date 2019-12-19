package com.sscl.blesample.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.sscl.baselibrary.utils.BaseManager;
import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.blesample.R;
import com.sscl.blesample.adapter.entity.services_characteristics_list_entity.CharacteristicUuidItem;
import com.sscl.blesample.adapter.entity.services_characteristics_list_entity.ServiceUuidItem;

import java.util.List;


/**
 * @author jacke
 * @date 2018/1/22 0022
 */

public class ServicesCharacteristicsListAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public static final int TYPE_SERVICE_UUID = 0;
    public static final int TYPE_CHARACTERISTIC_UUID = 1;
    public static final int LEVEL_SERVICE_UUID = 1;

    private OnCharacteristicClickListener onCharacteristicClickListener;
    private OnServiceClickListener onServiceClickListener;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ServicesCharacteristicsListAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_SERVICE_UUID, R.layout.item_expandable_service_uuid);
        addItemType(TYPE_CHARACTERISTIC_UUID, R.layout.item_expandable_characteristic_uuid);
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param holder A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(final BaseViewHolder holder, final MultiItemEntity item) {
        switch (holder.getItemViewType()) {
            case TYPE_SERVICE_UUID:
                final ServiceUuidItem serviceUuidItem = (ServiceUuidItem) item;
                holder.setText(android.R.id.text1, serviceUuidItem.getName())
                        .setText(android.R.id.text2, serviceUuidItem.getUuid())
                        .setImageResource(R.id.expanded, serviceUuidItem.isExpanded() ? R.drawable.arrow_b : R.drawable.arrow_r);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int adapterPosition = holder.getAdapterPosition();
                        final int childCount;
                        if (serviceUuidItem.isExpanded()) {
                            childCount = collapse(adapterPosition);
                        } else {
                            childCount = expand(adapterPosition);
                            if (childCount <= 0) {
                                ToastUtil.toastLong(mContext, R.string.nothing_to_expand);
                            }
                        }
                        BaseManager.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                if (onServiceClickListener != null) {
                                    onServiceClickListener.onServiceClick(serviceUuidItem.getUuid(), holder.getLayoutPosition(), childCount);
                                }
                            }
                        });
                    }
                });
                break;
            case TYPE_CHARACTERISTIC_UUID:
                final CharacteristicUuidItem characteristicUuidItem = (CharacteristicUuidItem) item;
                holder.setText(android.R.id.text1, characteristicUuidItem.getName())
                        .setText(android.R.id.text2, characteristicUuidItem.getUuid())
                        .setText(R.id.properties, getProperties(characteristicUuidItem.isCanRead(), characteristicUuidItem.isCanWrite(), characteristicUuidItem.isCanNotify()));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BaseManager.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                if (onCharacteristicClickListener != null) {
                                    int parentPosition = getParentPosition(item);
                                    ServiceUuidItem serviceUuidItem1 = (ServiceUuidItem) getItem(parentPosition);
                                    if (serviceUuidItem1 != null) {
                                        onCharacteristicClickListener.onCharacteristicClick(serviceUuidItem1.getUuid(), characteristicUuidItem.getUuid());
                                    }
                                }
                            }
                        });
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     * Expand an expandable item
     *
     * @param position     position of the item
     * @param animate      expand items with animation
     * @param shouldNotify notify the RecyclerView to rebind items, <strong>false</strong> if you want to do it
     *                     yourself.
     * @return the number of items that have been added.
     */
    @Override
    public int expand(int position, boolean animate, boolean shouldNotify) {
        return super.expand(position, animate, shouldNotify);
    }

    private String getProperties(boolean canRead, boolean canWrite, boolean canNotify) {
        if (!canRead && !canWrite && !canNotify) {
            return mContext.getString(R.string.null_);
        }
        StringBuilder stringBuilder = new StringBuilder();

        if (canRead) {
            stringBuilder.append(mContext.getString(R.string.can_read));
        }
        if (canWrite) {
            stringBuilder.append(mContext.getString(R.string.can_write));
        }
        if (canNotify) {
            stringBuilder.append(mContext.getString(R.string.can_notify));
        }
        return stringBuilder.toString();
    }

    /**
     * 设置点击监听
     *
     * @param onCharacteristicClickListener 特征值点击监听
     */
    public void setOnCharacteristicClickListener(OnCharacteristicClickListener onCharacteristicClickListener) {
        this.onCharacteristicClickListener = onCharacteristicClickListener;
    }

    public void setOnServiceClickListener(OnServiceClickListener onServiceClickListener) {
        this.onServiceClickListener = onServiceClickListener;
    }

    /**
     * 接口 监听特征UUID被点击时的事件
     */
    public interface OnCharacteristicClickListener {
        /**
         * 监听特征UUID被点击时的事件
         *
         * @param serviceUuid        服务UUID
         * @param characteristicUuid 特征UUID
         */
        void onCharacteristicClick(String serviceUuid, String characteristicUuid);
    }

    /**
     * 接口 监听特征UUID被点击时的事件
     */
    public interface OnServiceClickListener {
        /**
         * 监听特征UUID被点击时的事件
         *
         * @param serviceUuid     服务UUID
         * @param position        当前布局的位置
         * @param adapterPosition 整个列表中所有选项的位置
         */
        void onServiceClick(String serviceUuid, int position, int adapterPosition);
    }
}
