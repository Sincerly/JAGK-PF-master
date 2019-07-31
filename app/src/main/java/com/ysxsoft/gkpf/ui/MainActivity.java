package com.ysxsoft.gkpf.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupPosition;
import com.ysxsoft.gkpf.R;
import com.ysxsoft.gkpf.api.ApiManager;
import com.ysxsoft.gkpf.api.IMessageCallback;
import com.ysxsoft.gkpf.api.MessageCallbackMap;
import com.ysxsoft.gkpf.bean.MyCell;
import com.ysxsoft.gkpf.bean.response.FileResponse;
import com.ysxsoft.gkpf.bean.response.LogoutResponse;
import com.ysxsoft.gkpf.config.AppConfig;
import com.ysxsoft.gkpf.utils.FileUtils;
import com.ysxsoft.gkpf.utils.JsonUtils;
import com.ysxsoft.gkpf.utils.Logutils;
import com.ysxsoft.gkpf.utils.PoiExcelReadUtils;
import com.ysxsoft.gkpf.utils.JxlExcelReadUtils;
import com.ysxsoft.gkpf.utils.SystemUtils;
import com.ysxsoft.gkpf.utils.ToastUtils;
import com.ysxsoft.gkpf.view.MainLeftPopupView;
import com.ysxsoft.gkpf.ui.adapter.LeftAdapter;
import com.ysxsoft.gkpf.view.PingFenPopupView;
import com.ysxsoft.gkpf.view.SetupPopupView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_ASK_NOTIFY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_CACHE_REPLY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_DIFFJUDGMENTS_REPLY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_EXAMEND_NOTIFY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_FILESEND;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_INSTRUCTION_NOTIFY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_LOGINOUT_REPLY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_REPLACE_NOTIFY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_TASKLISTSTATE_NOTIFY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_MANUALSCORE_UPLOADSCORE_REPLY;
import static com.ysxsoft.gkpf.api.ApiManager.MSG_TOTALGRADEFORM_REPLY;

public class MainActivity extends BaseActivity implements IMessageCallback {

    @BindView(R.id.rv_activity_main_left)
    RecyclerView rvActivityMainLeft;
    @BindView(R.id.base_view)
    AbsoluteLayout baseView;

    private LeftAdapter leftAdapter;

    private MyCell xuHaoView = null;      //序号行位置
    private MyCell huiZongView = null;    //汇总行位置
    private MyCell peiFenView = null;    //配分列
    private MyCell deFenView = null;     //得分列
    HashMap<String, TextView> peiFenViews = new HashMap<>();    //配分控件
    HashMap<String, TextView> deFenViews = new HashMap<>();     //得分控件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initLeftData();
        new Thread() {
            @Override
            public void run() {
                super.run();
//                initRightDataJxl();
            }
        }.start();

        MessageCallbackMap.reg("Main", this);
        //ApiManager.logout();//退出登录
        ApiManager.cache();//请求缓存
    }

    private void initView() {
        //左侧导航
        rvActivityMainLeft.setLayoutManager(new LinearLayoutManager(this));
        leftAdapter = new LeftAdapter(R.layout.activity_main_left_item);
        rvActivityMainLeft.setAdapter(leftAdapter);
    }

    private void initLeftData() {
        ArrayList<String> temp = new ArrayList<>();
        temp.add("");
        temp.add("");
        temp.add("");
        temp.add("");
        temp.add("");
        temp.add("");
        leftAdapter.setNewData(temp);
    }

    /**
     * TODO 读取Excel文件
     * 文件读取放到工作线程
     * 界面操作放到主线程
     */
    private void initRightDataJxl(String fileName) {
        try {

            HashMap<Integer, ArrayList<MyCell>> baseData = JxlExcelReadUtils.readExcelCell(this, fileName, 0);

            //UI操作放到主线程
            runOnUiThread(new Runnable() {
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayout itemBase = (LinearLayout) View.inflate(MainActivity.this, R.layout.activity_main_right_base_view, null);
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
                                            }
                                            peiFenViews.put("" + tempCell.getRow(), textView);
                                        }
                                        //分项得分列
                                        if (deFenView != null && tempCell.getCol() == deFenView.getCol()) {     //列数等于得分列
                                            deFenViews.put("" + tempCell.getRow(), textView);
                                        }
                                    }
                                }
                            }
                            if (tempCell.getValue().toString().equals("汇总")) {
                                TextView tv_button = itemBase.findViewById(R.id.tv_activity_main_right_hz);
                                tv_button.setVisibility(View.VISIBLE);
                            }
                            baseView.addView(itemBase);
                        }
                    });

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("文件解析异常");
        }
    }

    /**
     * TODO
     * Poi获取Excel元素，已弃用
     */
    private void initRightDataPoi() {
        try {
            HashMap<Integer, ArrayList<MyCell>> baseData = PoiExcelReadUtils.readExcelCell(this, "Excel模板(1).xls", 0);

            baseView.setMinimumHeight(PoiExcelReadUtils.getTableHeight(PoiExcelReadUtils.sheet));

            Iterator<Map.Entry<Integer, ArrayList<MyCell>>> iterator = baseData.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, ArrayList<MyCell>> entry = iterator.next();
                entry.getKey();
                ArrayList<MyCell> tempList = entry.getValue();
                for (int i = 0; i < tempList.size(); i++) {
                    if (!tempList.get(i).isCellRegion()) {      //判断单元格是否在合并单元格内，是否需要绘制
                        continue;
                    }
                    MyCell tempCell = tempList.get(i);
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

                    LinearLayout itemBase = (LinearLayout) View.inflate(this, R.layout.activity_main_right_base_view, null);
                    //大小
                    itemBase.setLayoutParams(new LinearLayout.LayoutParams(widthValue, heightValue));
                    //位置
                    itemBase.setX(tempCell.getIndexX());
                    itemBase.setY(tempCell.getIndexY());

                    if (tempCell.getValue().toString().contains("对错")) {
                        ImageView ivDui = itemBase.findViewById(R.id.iv_activity_main_right_item_item_dui);   //对
                        ImageView ivCuo = itemBase.findViewById(R.id.iv_activity_main_right_item_item_cuo);   //错
                        ivDui.setVisibility(View.VISIBLE);
                        ivCuo.setVisibility(View.VISIBLE);
                    }
                    if (tempCell.getValue().toString().contains("调整")) {
                        ImageView ivJia = itemBase.findViewById(R.id.iv_activity_main_right_item_item_jia);   //加
                        ImageView ivJian = itemBase.findViewById(R.id.iv_activity_main_right_item_item_jian); //减
                        ivJia.setVisibility(View.VISIBLE);
                        ivJian.setVisibility(View.VISIBLE);
                    }
                    if ((!tempCell.getValue().toString().contains("对错")) && (!tempCell.getValue().toString().contains("调整"))) {
                        TextView textView = itemBase.findViewById(R.id.tv_activity_main_right_content);
                        textView.setVisibility(View.VISIBLE);
                        //属性
                        textView.setText(tempCell.getValue().toString());
                        textView.setGravity(tempCell.getAlign());
                    }
                    baseView.addView(itemBase);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("文件解析异常");
        }
    }

    @OnClick(R.id.ll_activity_main_left)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_activity_main_left:
                new XPopup.Builder(this)
                        .popupPosition(PopupPosition.Left)//右边
                        .asCustom(new MainLeftPopupView(this))
                        .show();
                break;
        }
    }

    /**
     * TODO
     * 对号操作
     */
    private class DuiClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
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
        }
    }

    /**
     * TODO
     * 错号操作
     */
    private class CuoClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
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
                    new XPopup.Builder(MainActivity.this)
                            .atView(v)
                            .asCustom(new PingFenPopupView(MainActivity.this, myCellValue, new PingFenPopupView.ItemClickResult() {
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
                showToast("分数步进值为空");
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

    /**
     * TODO
     * 网络响应接收方法
     *
     * @param type
     * @param json
     * @param rawData
     */
    @Override
    public void onMessageResponse(short type, String json, byte[] rawData) {
        switch (type) {
            case MSG_MANUALSCORE_LOGINOUT_REPLY:
                //登出反馈
                Log.e("tag", "登出回复");
                LogoutResponse response = JsonUtils.parseByGson(json, LogoutResponse.class);
                if (response.isResult()) {
                    //退出成功
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(MainActivity.this, "登出成功！", 300);
                            finish();
                        }
                    });
                } else {
                    //退出失败
                }
                break;
            case MSG_MANUALSCORE_CACHE_REPLY:
                //缓存反馈
                Log.e("tag", "缓存反馈");
                break;
            case MSG_MANUALSCORE_FILESEND:
                //评分表文件发送
                Log.e("tag", "评分表文件发送");
                FileResponse fileResponse = JsonUtils.parseByGson(json, FileResponse.class);
                String fileContent = fileResponse.getFileContents();
                int length = fileContent.length();
                byte[] bytes = fileContent.getBytes();
                for (int i = 0; i < length; i++) {
                    bytes[i] = (byte) fileContent.charAt(i);
                }
                String fileName = fileResponse.getFileName() + ".xls";
                FileUtils.byte2File(bytes, AppConfig.BASE_PATH, fileName);
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        initRightDataJxl(fileName);
                    }
                }.start();
                //发送接收文件确认
                ApiManager.confirmFile(fileName);
                break;
            case MSG_MANUALSCORE_REPLACE_NOTIFY:
                //占位符替换数据通知
                Log.e("tag", "占位符替换数据通知");
                //{"flowName1":{"replaceholder1":"","replaceholder2":"","replaceholder3":""},"flowName2":{"replaceholder1":"","replaceholder2":"","replaceholder3":""},"groupId":1,"requestId":1}
                //flowName1动态key
//                ApiManager.confirmReplace("");
                break;
            case MSG_MANUALSCORE_TASKLISTSTATE_NOTIFY:
                //任务状态变化通知
                Log.e("tag", "任务状态变化通知");
//                ApiManager.confirmTaskState();
                break;
            case MSG_MANUALSCORE_ASK_NOTIFY:
                //问询通知
                Log.e("tag", "问询通知");
//                ApiManager.confirmAsk();
                break;
            case MSG_MANUALSCORE_UPLOADSCORE_REPLY:
                //评分上传反馈
//                Log.e("tag", "评分上传反馈");
                break;
            case MSG_MANUALSCORE_EXAMEND_NOTIFY:
                //考试结束通知
                Log.e("tag", "考试结束通知");
//                ApiManager.confirmExamAck();//考试结束接收确认
                break;
            case MSG_MANUALSCORE_DIFFJUDGMENTS_REPLY:
                //定位不同反馈
//                Log.e("tag", "定位不同反馈");
                break;
            case MSG_TOTALGRADEFORM_REPLY:
                //总成绩单反馈
//                Log.e("tag", "总成绩单反馈");
                break;
            case MSG_MANUALSCORE_INSTRUCTION_NOTIFY:
                //评分说明通知
                Log.e("tag", "评分说明通知");
//                ApiManager.confirmInstruction();//评分说明接收确认
                break;
        }
    }
}
