package com.xyw.platescan.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyw.platescan.R;


/**
 * Created by 31429 on 2017/11/16.
 */

public class ExpandableAdapter extends BaseExpandableListAdapter {
    private static String[] groupData = new String[]{"驾驶人信息", "车辆信息", "货物信息"};
    private static int[] groupIcon = new int[]{R.drawable.driver, R.drawable.vehicle, R.drawable.card};
    private static String[][] childTitle = new String[][]{
            {"驾驶人姓名", "身份证号", "联系方式"},
            {"通行证编号", "通行证类型", "车牌号码", "车辆种类", "车辆类型"},
            {"开始时间", "结束时间", "通行路线", "通行区域", "同行目的", "货物名称", "货物质量", "装卸占道"}
    };
    private String[][] childData;
    private Context mContext;

    /**
     * @param data 传入需要显示的二维数组
     */
    public ExpandableAdapter(String[][] data, Context context) {
        childData = data;
        mContext = context;
    }

    /**
     * @return 父项目的数量
     */
    @Override
    public int getGroupCount() {
        return groupData.length;
    }

    /**
     * @param groupPosition 父项目的位置
     * @return 选定父项下子项目数量
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return childData[groupPosition].length;
    }

    /**
     * @param groupPosition 父项目位置
     * @return 父项目名称
     */
    @Override
    public Object getGroup(int groupPosition) {
        return groupData[groupPosition];
    }

    /**
     * @param groupPosition 父项目位置
     * @param childPosition 子项目位置
     * @return 选定子项目的值
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childData[groupPosition][childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view;
        GroupHolder groupHolder;
        if (convertView != null) {
            view = convertView;
            groupHolder = (GroupHolder) view.getTag();
        } else {
            view = View.inflate(mContext, R.layout.expandable_group_item, null);
            groupHolder = new GroupHolder();
            groupHolder.groupIcon = view.findViewById(R.id.icon_group);
            groupHolder.groupTitle = view.findViewById(R.id.title_group);
            view.setTag(groupHolder);
        }
        groupHolder.groupTitle.setText(groupData[groupPosition]);
        groupHolder.groupIcon.setImageResource(groupIcon[groupPosition]);
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view;
        ChildHolder childHolder;
        if (convertView != null) {
            view = convertView;
            childHolder = (ChildHolder) view.getTag();
        } else {
            view = View.inflate(mContext, R.layout.expandable_child_item, null);
            childHolder = new ChildHolder();
            childHolder.text = view.findViewById(R.id.child_text);
            childHolder.title = view.findViewById(R.id.child_name);
            view.setTag(childHolder);
        }
        childHolder.title.setText(childTitle[groupPosition][childPosition]);
        childHolder.text.setText(childData[groupPosition][childPosition]);
        return view;
    }

    static class GroupHolder {
        ImageView groupIcon;
        TextView groupTitle;
    }

    static class ChildHolder {
        TextView title, text;
    }
}
