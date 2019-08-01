package com.ysxsoft.gkpf.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chong.widget.verticalviewpager.DummyViewPager;
import com.chong.widget.verticalviewpager.VerticalVPOnTouchListener;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupPosition;
import com.ysxsoft.gkpf.R;
import com.ysxsoft.gkpf.bean.MyCell;
import com.ysxsoft.gkpf.ui.adapter.LeftAdapter;
import com.ysxsoft.gkpf.utils.JxlExcelReadUtils;
import com.ysxsoft.gkpf.utils.PoiExcelReadUtils;
import com.ysxsoft.gkpf.utils.ShareUtils;
import com.ysxsoft.gkpf.utils.SystemUtils;
import com.ysxsoft.gkpf.view.MainLeftPopupView;
import com.ysxsoft.gkpf.view.PingFenPopupView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.OnClick;

public class ContentFragment extends Fragment {

    RecyclerView baseRecycler;
    AbsoluteLayout baseView;
    TextView baseTips;

    private MyCell xuHaoView = null;      //序号行位置
    private MyCell huiZongView = null;    //汇总行位置
    private MyCell peiFenView = null;    //配分列
    private MyCell deFenView = null;     //得分列
    HashMap<String, TextView> peiFenViews = new HashMap<>();    //配分控件
    HashMap<String, TextView> deFenViews = new HashMap<>();     //得分控件
    HashMap<String, TextView> noPfViews = new HashMap<>();     //不需要评分控件

    private TextView pfZjTv;    //配分总计单元格
    private TextView dfZjTv;    //得分总计单元格

    public ContentFragment() {
    }

    public static Fragment newInstance(String fileName, int position, DummyViewPager dummyViewPager) {
        Bundle args = new Bundle();
        args.putString("file", fileName);
        args.putInt("position", position);
        args.putSerializable("viewpager", dummyViewPager);
        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        initView(view);
        new Thread() {
            @Override
            public void run() {
                super.run();
                initRightDataJxl(getFileName());
            }
        }.start();
        return view;
    }

    private void initView(View view) {
        baseRecycler = view.findViewById(R.id.content_list);
        baseRecycler.setOnTouchListener(new VerticalVPOnTouchListener((DummyViewPager) getArguments().getSerializable("viewpager")));//set the vertical scroll controller
        baseRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
        baseRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        LeftAdapter leftAdapter = new LeftAdapter(R.layout.activity_main_left_item);
        View headView = View.inflate(getActivity(), R.layout.fragment_content_head, null);
        baseTips = headView.findViewById(R.id.tv_tips);
        baseView = headView.findViewById(R.id.base_view);
        leftAdapter.addHeaderView(headView);
        baseRecycler.setAdapter(leftAdapter);
    }

    public String getFileName() {
        return getArguments().getString("file");
    }

    public int getPosition() {
        return getArguments().getInt("position");
    }

    /**
     * TODO 读取Excel文件
     * 文件读取放到工作线程
     * 界面操作放到主线程
     */
    private void initRightDataJxl(String fileName) {
        try {

            HashMap<Integer, ArrayList<MyCell>> baseData = JxlExcelReadUtils.readExcelCell(getActivity(), fileName, 0);

            //UI操作放到主线程
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    baseView.removeAllViews();
                    baseView.setMinimumHeight(JxlExcelReadUtils.getTableHeight(JxlExcelReadUtils.sheet));
                }
            });

            Iterator<Map.Entry<Integer, ArrayList<MyCell>>> iterator = baseData.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, ArrayList<MyCell>> entry = iterator.next();
                entry.getKey();
                ArrayList<MyCell> tempList = entry.getValue();
                for (int i = 0; i < tempList.size(); i++) {
                    if (!tempList.get(i).isCellRegion()) {      //判断单元格是否在合并单元格内，是否需要绘制
                        continue;
                    }
                    final MyCell tempCell = tempList.get(i);

                    /*******************记录关键点位置*******************/
                    if (tempCell.getValue().toString().equals("序号") && xuHaoView == null) {
                        xuHaoView = tempCell;
                    } else if (tempCell.getValue().toString().equals("合计") && huiZongView == null) {
                        huiZongView = tempCell;
                    } else if (tempCell.getValue().toString().equals("分项配分") && peiFenView == null) {
                        peiFenView = tempCell;
                    } else if (tempCell.getValue().toString().equals("分项得分") && deFenView == null) {
                        deFenView = tempCell;
                    }

                    /*******************单元格位置*******************/
                    int XValue = 0;     //单元格起始X位置
                    int YValue = 0;     //单元格起始Y位置
                    for (int col = 0; col < tempCell.getCol(); col++) {    //列数决定X位置
                        XValue += tempList.get(col).getCellWidth();
                    }
                    for (int row = 0; row < tempCell.getRow(); row++) {    //行数决定Y位置
                        YValue += baseData.get(row).get(i).getCellHeight();
                    }
                    tempCell.setIndexX(XValue);     //保存单元格X位置
                    tempCell.setIndexY(YValue);     //保存单元格Y位置
                    /*******************单元格位置*******************/

                    /*******************单元格大小*******************/
                    int widthValue = 0;
                    int heightValue = 0;
                    if (tempCell.getReginCol() > 0) {
                        for (int col = 0; col < tempCell.getReginCol(); col++) {    //宽度加列数
                            widthValue += tempList.get(tempCell.getCol() + col).getCellWidth();
                        }
                    } else {
                        widthValue = tempList.get(tempCell.getCol()).getCellWidth();
                    }
                    if (tempCell.getReginRow() > 0) {
                        for (int row = 0; row < tempCell.getReginRow(); row++) {    //高度加行数
                            heightValue += baseData.get(tempCell.getRow() + row).get(tempCell.getCol()).getCellHeight();
                        }
                    } else {
                        heightValue = baseData.get(tempCell.getRow()).get(tempCell.getCol()).getCellHeight();
                    }
                    tempCell.setViewWidth(widthValue);      //保存控件宽度
                    tempCell.setViewHeight(heightValue);    //保存控件高度
                    /*******************单元格大小*******************/
                    final int finalWidthValue = widthValue;
                    final int finalHeightValue = heightValue;

                    //UI操作放到主线程
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayout itemBase = (LinearLayout) View.inflate(getActivity(), R.layout.activity_main_right_base_view, null);
                            //大小
                            itemBase.setLayoutParams(new LinearLayout.LayoutParams(finalWidthValue, finalHeightValue));
                            //位置
                            itemBase.setX(tempCell.getIndexX());
                            itemBase.setY(tempCell.getIndexY());

                            if (tempCell.getValue().toString().contains("对错")) {
                                ImageView ivDui = itemBase.findViewById(R.id.iv_activity_main_right_item_item_dui);   //对
                                ImageView ivCuo = itemBase.findViewById(R.id.iv_activity_main_right_item_item_cuo);   //错
                                ivDui.setVisibility(View.VISIBLE);
                                ivCuo.setVisibility(View.VISIBLE);
                                ivDui.setTag(tempCell);
                                ivCuo.setTag(tempCell);
                                ivDui.setOnClickListener(new DuiClickListener());
                                ivCuo.setOnClickListener(new CuoClickListener());
                            }
                            if (tempCell.getValue().toString().contains("调整")) {
                                ImageView ivJia = itemBase.findViewById(R.id.iv_activity_main_right_item_item_jia);   //加
                                ImageView ivJian = itemBase.findViewById(R.id.iv_activity_main_right_item_item_jian); //减
                                ivJia.setVisibility(View.VISIBLE);
                                ivJian.setVisibility(View.VISIBLE);
                                ivJia.setTag(tempCell);
                                ivJian.setTag(tempCell);
                                ivJia.setOnClickListener(new TiaoZhengClickListener(1));
                                ivJian.setOnClickListener(new TiaoZhengClickListener(2));
                            }
                            if ((!tempCell.getValue().toString().contains("对错")) && (!tempCell.getValue().toString().contains("调整")) && (!tempCell.getValue().toString().equals("汇总"))) {
                                TextView textView = itemBase.findViewById(R.id.tv_activity_main_right_content);
                                textView.setVisibility(View.VISIBLE);
                                //属性
                                textView.setText(tempCell.getValue().toString());
                                textView.setGravity(tempCell.getAlign());
                                textView.setTextColor(tempCell.getFontColor());
                                textView.setTag(tempCell);
                                textView.setOnClickListener(new ExcelItemClickListener());
                                //记录关键列
                                if (xuHaoView != null && tempCell.getRow() > xuHaoView.getRow()) {  //行号大于序号行
                                    if (huiZongView == null || tempCell.getRow() < huiZongView.getRow()) {  //行号小于汇总行
                                        //分项配分列
                                        if (peiFenView != null && tempCell.getCol() == peiFenView.getCol()) {   //列数等于配分列
                                            //评判结果如果是负数，显示正数
                                            Object tempValue = SystemUtils.getValueType(tempCell.getValue().toString());
                                            if (tempValue instanceof Integer) {
                                                textView.setText("" + Math.abs(Integer.valueOf(tempCell.getValue().toString())));
                                            } else if (tempValue instanceof Double) {
                                                textView.setText("" + Math.abs(Double.valueOf(tempCell.getValue().toString())));
                                            } else {
                                                noPfViews.put("" + tempCell.getRow(), textView);
                                            }
                                            peiFenViews.put("" + tempCell.getRow(), textView);
                                        }
                                        //分项得分列
                                        if (deFenView != null && tempCell.getCol() == deFenView.getCol()) {     //列数等于得分列
                                            deFenViews.put("" + tempCell.getRow(), textView);
                                        }
                                    } else if (huiZongView != null && tempCell.getRow() == huiZongView.getRow()) {
                                        if (peiFenView != null && tempCell.getCol() == peiFenView.getCol()) {   //列数等于配分列
                                            pfZjTv = textView;
                                        }
                                        if (deFenView != null && tempCell.getCol() == deFenView.getCol()) {     //列数等于得分列
                                            dfZjTv = textView;
                                        }
                                    }
                                }
                            }
                            if (tempCell.getValue().toString().equals("汇总")) {
                                TextView tv_button = itemBase.findViewById(R.id.tv_activity_main_right_hz);
                                tv_button.setVisibility(View.VISIBLE);
                                tv_button.setTag(tempCell);
                                tv_button.setOnClickListener(new HuiZongClickListener());
                            }
                            baseView.addView(itemBase);
                        }
                    });

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ((BaseActivity) getActivity()).showToast("文件解析异常");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * TODO 汇总点击时间
     */
    private class HuiZongClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (pfZjTv == null) {
                ((BaseActivity) getActivity()).showToast("配分总计项未找到");
                return;
            }
            if (dfZjTv == null) {
                ((BaseActivity) getActivity()).showToast("得分总计项未找到");
                return;
            }
            MyCell pfZjCell = (MyCell) pfZjTv.getTag();
            MyCell dfZjCell = (MyCell) dfZjTv.getTag();
            double pfzj = 0;
            double dfzj = 0;
            for (int i = xuHaoView.getRow() + 1; i < huiZongView.getRow(); i++) {
                MyCell tempCell = (MyCell) peiFenViews.get("" + i).getTag();
                Object tempValue = SystemUtils.getValueType(tempCell.getValue().toString());
                if (!(tempValue instanceof String)) {
                    pfzj += tempValue instanceof Integer ? (int) tempValue : (double) tempValue;
                }
                MyCell tempDfCell = (MyCell) deFenViews.get("" + i).getTag();
                //若评判结果既没有选择【正确】，也没有选择【错误】，则点击汇总后，该项自动选择为正确
                if (TextUtils.isEmpty(tempDfCell.getValue().toString())) {
                    DuiClick(peiFenViews.get("" + i));
                }
                Object tempDfValue = SystemUtils.getValueType(tempDfCell.getValue().toString());
                if (!(tempDfValue instanceof String)) {
                    dfzj += tempDfValue instanceof Integer ? (int) tempDfValue : (double) tempDfValue;
                }
            }
            if (pfzj == (int) pfzj) {
                pfZjCell.setValue((int) pfzj);
            } else {
                pfZjCell.setValue(pfzj);
            }
            if (dfzj == (int) dfzj) {
                dfZjCell.setValue((int) dfzj);
            } else {
                dfZjCell.setValue(dfzj);
            }
            pfZjTv.setText(pfZjCell.getValue().toString());
            dfZjTv.setText(dfZjCell.getValue().toString());
        }
    }


    /**
     * TODO
     * 对号操作
     */
    private class DuiClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            DuiClick(v);
        }
    }

    /**
     * 通用对号操作
     *
     * @param v
     */
    private void DuiClick(View v) {
        MyCell myCell = (MyCell) v.getTag();
        TextView currRowPfText = peiFenViews.get("" + myCell.getRow());
        TextView currRowDfText = deFenViews.get("" + myCell.getRow());
        MyCell pfCell = (MyCell) currRowPfText.getTag();
        MyCell dfCell = (MyCell) currRowDfText.getTag();
        double tempValue = Double.valueOf(pfCell.getValue().toString());
        //评判结果是正数还是负数
        //负数，做对了，得0分，做错了，得负分
        if (tempValue < 0) {
            dfCell.setValue(0);
            currRowDfText.setText("0");
        } else {
            if (tempValue == (int) tempValue) {
                dfCell.setValue((int) tempValue);
                currRowDfText.setText("" + ((int) tempValue));
            } else {
                dfCell.setValue(tempValue);
                currRowDfText.setText("" + tempValue);
            }
        }
        //实时更新数据
        Iterator<Map.Entry<String, TextView>> iterator = noPfViews.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, TextView> entry = iterator.next();
            MyCell noPFCell = (MyCell) noPfViews.get(entry.getKey()).getTag();
            if (myCell.getRow() > noPFCell.getRow()) {

            }
        }

    }

    /**
     * TODO
     * 错号操作
     */
    private class CuoClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            CuoClick(v);
        }
    }

    private void CuoClick(View v) {
        MyCell myCell = (MyCell) v.getTag();
        TextView currRowPfText = peiFenViews.get("" + myCell.getRow());
        TextView currRowDfText = deFenViews.get("" + myCell.getRow());
        MyCell pfCell = (MyCell) currRowPfText.getTag();
        MyCell dfCell = (MyCell) currRowDfText.getTag();
        double tempValue = Double.valueOf(pfCell.getValue().toString());
        //评判结果是正数还是负数
        //负数，做对了，得0分，做错了，得负分
        if (tempValue < 0) {
            if (tempValue == (int) tempValue) {
                dfCell.setValue((int) tempValue);
                currRowDfText.setText("" + ((int) tempValue));
            } else {
                dfCell.setValue(tempValue);
                currRowDfText.setText("" + tempValue);
            }
        } else {
            dfCell.setValue(0);
            currRowDfText.setText("0");
        }
    }

    /**
     * TODO
     * 调整操作
     */
    private class TiaoZhengClickListener implements View.OnClickListener {

        private int type;   //1:加操作 2：减操作

        public TiaoZhengClickListener(int type) {
            this.type = type;
        }

        @Override
        public void onClick(View v) {
            MyCell myCell = (MyCell) v.getTag();
            TextView currRowPfText = peiFenViews.get("" + myCell.getRow());
            final TextView currRowDfText = deFenViews.get("" + myCell.getRow());
            final MyCell pfCell = (MyCell) currRowPfText.getTag();
            final MyCell dfCell = (MyCell) currRowDfText.getTag();
            //判断当前按钮是否可用
            if (!IsUnable(pfCell, dfCell, type)) {
                return;
            }
            String[] myCellSplit = myCell.getValue().toString().split("#");
            if (myCellSplit.length > 1) {
                final String[] myCellValue = myCellSplit[1].split(";");
                //如果只有一个扣分分值，点击直接扣分
                if (myCellValue.length > 0 && myCellValue.length == 1) {
                    if (type == 1) {
                        currRowDfText.setText(pingfenJia(pfCell, dfCell, myCellValue[0]));
                    } else {
                        currRowDfText.setText(pingfenJian(pfCell, dfCell, myCellValue[0]));
                    }
                } else {
                    //如果有多个扣分分值，点击加号或减号，在本图标下弹出下拉列表，进行分数选择
                    new XPopup.Builder(getActivity())
                            .atView(v)
                            .asCustom(new PingFenPopupView(getActivity(), myCellValue, new PingFenPopupView.ItemClickResult() {
                                @Override
                                public void resultValue(String value) {
                                    if (type == 1) {
                                        currRowDfText.setText(pingfenJia(pfCell, dfCell, value));
                                    } else {
                                        currRowDfText.setText(pingfenJian(pfCell, dfCell, value));
                                    }
                                }
                            }))
                            .show();
                }
            } else {
                ((BaseActivity) getActivity()).showToast("分数步进值为空");
            }
        }
    }

    /**
     * TODO
     * 判断当前按按钮是否可用
     *
     * @param pfCell 配分列单元格
     * @param dfCell 得分列单元格
     * @param type   操作类型：1：加，2：减
     * @return
     */
    private boolean IsUnable(MyCell pfCell, MyCell dfCell, int type) {
        double tempValue = Double.valueOf(pfCell.getValue().toString());
        if (tempValue < 0) {
            if (type == 1) {
                //【+】符号不可用（得分和配分相同时，不可用）
                if (dfCell.getValue().toString().equals("0")) {
                    return false;
                }
            } else if (type == 2) {
                //【-】符号不可用（如果是0）
                if (dfCell.getValue().toString().equals(pfCell.getValue().toString())) {
                    return false;
                }
            }
        } else {
            if (type == 1) {
                //【+】符号不可用（得分和配分相同时，不可用）
                if (dfCell.getValue().toString().equals(pfCell.getValue().toString())) {
                    return false;
                }
            } else if (type == 2) {
                //【-】符号不可用（如果是0）
                if (dfCell.getValue().toString().equals("0")) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * TODO
     * 加号运算
     *
     * @param pfCell 配分列单元格
     * @param dfCell 得分列单元格
     * @param value  参与运算的值
     * @return
     */
    private String pingfenJia(MyCell pfCell, MyCell dfCell, String value) {
        double tempValue = 0;
        if (!TextUtils.isEmpty(dfCell.getValue().toString())) {
            tempValue = Double.valueOf(dfCell.getValue().toString());
        }
        tempValue += Double.valueOf(value);
        //【+】符号不可用（得分和配分相同时，不可用）
        if (Double.valueOf(pfCell.getValue().toString()) < 0) {
            if (tempValue >= 0) {
                tempValue = 0;
            }
        } else {
            if (tempValue >= Double.valueOf(pfCell.getValue().toString())) {
                tempValue = Double.valueOf(pfCell.getValue().toString());
            }
        }
        if (tempValue == (int) tempValue) {
            dfCell.setValue((int) tempValue);
            return ("" + ((int) tempValue));
        } else {
            dfCell.setValue(tempValue);
            return ("" + tempValue);
        }
    }

    /**
     * TODO
     * 减号运算
     *
     * @param pfCell 配分列单元格
     * @param dfCell 得分列单元格
     * @param value  参与运算的值
     * @return
     */
    private String pingfenJian(MyCell pfCell, MyCell dfCell, String value) {
        double tempValue = 0;
        if (!TextUtils.isEmpty(dfCell.getValue().toString())) {
            tempValue = Double.valueOf(dfCell.getValue().toString());
        }
        tempValue -= Double.valueOf(value);

        if (Double.valueOf(pfCell.getValue().toString()) < 0) {
            if (tempValue <= Double.valueOf(pfCell.getValue().toString())) {
                tempValue = Double.valueOf(pfCell.getValue().toString());
            }
        } else {
            if (tempValue <= 0) {
                tempValue = 0;
            }
        }
        if (tempValue == (int) tempValue) {
            dfCell.setValue((int) tempValue);
            return ("" + ((int) tempValue));
        } else {
            dfCell.setValue(tempValue);
            return ("" + tempValue);
        }
    }

    /**
     * TODO
     * Excel文本点击事件
     */
    private class ExcelItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
//            MyCell myCell = (MyCell) v.getTag();
//            showToast(myCell.getValue().toString());
        }
    }

    public void refreshMain(int p, Object object, boolean isConfirm) {
        MainActivity activity = (MainActivity) getActivity();
        activity.uploadByPosition(getFileName(), p, object, isConfirm);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getPosition() == 0&& (!TextUtils.isEmpty(ShareUtils.getInstruction()))) {
            baseTips.setText(ShareUtils.getInstruction());
            baseTips.setVisibility(View.VISIBLE);
        }
    }
}
