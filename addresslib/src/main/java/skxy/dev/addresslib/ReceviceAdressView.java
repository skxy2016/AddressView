package skxy.dev.addresslib;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import skxy.dev.addresslib.adapter.ProvinceAdapter;
import skxy.dev.addresslib.bean.AddressBean;


/**
 * ClassName ReceviceAdressView
 * Created by skxy on 2016/8/30.
 * DES 自定义的收货地址视图
 */
public class ReceviceAdressView extends LinearLayout implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Context mContext;
    private ImageView mAddressView;
    private View mInflatView;
    private ListView mListView;
    private List<String> mTitleDatas;
    private Dialog mDialog;
    private List<AddressBean.CityBean> mCityDatas;
    private List<AddressBean> mProvinceDatas;
    private TextView mAddressTv;
    private List<String> mCurrentDatas = new ArrayList<>();//存放当前的数据
    private ProvinceAdapter maddressAdapter;
    private TextView mTvContent;
    private ImageView ivCloase;
    private ProgressBar mPb;


    //设置地址图标不可见
    public void addressIvToggle(boolean isShow) {
        if (isShow) {
            mAddressView.setVisibility(View.GONE);
        } else {
            mAddressView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置省市区地址内容    */
    public void setAddress(String content) {
        mAddressTv.setText(content);
    }

    /**
     * 获取省市区数据
     *
     * @param
     */

    public String getAddress() {
       return sb.toString().replaceAll(">","");
    }


    public ReceviceAdressView(Context context) {
        super(context);
    }

    public ReceviceAdressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView(context);

        initData();
        initEvent();

    }

    /**
     * 初始化数据
     * 外界触发加载
     */

    public void initData() {
        //标题
        mTitleDatas = new ArrayList<>();
        //省级
        mProvinceDatas = new ArrayList<>();
        //市级
        mCityDatas = new ArrayList<>();

    }


    private void initEvent() {
        this.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        ivCloase.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

    }

    //初始化视图
    private void initView(Context context) {
        View view = View.inflate(context, R.layout.receive_address_view, this);
        mAddressTv = (TextView) view.findViewById(R.id.receive_tv_address);
        mAddressView = (ImageView) view.findViewById(R.id.receive_iv_address);

        //listView
        mInflatView = View.inflate(mContext, R.layout.listview, null);
        mListView = (ListView) mInflatView.findViewById(R.id.receive_listview);
        mListView.setDividerHeight(0);
        mTvContent = (TextView) mInflatView.findViewById(R.id.dialog_tv_content);
        ivCloase = (ImageView) mInflatView.findViewById(R.id.dialog_iv_close);
        mPb = (ProgressBar) mInflatView.findViewById(R.id.receive_progress);

        maddressAdapter = new ProvinceAdapter(mContext);
        mListView.setAdapter(maddressAdapter);
    }


    @Override
    public void onClick(View view) {
        showDialog();
    }

    Handler handler = new Handler();

    /**
     * 显示弹出对话框
     */
    private void showDialog() {
        //初始化数据
        mTvContent.setText("请选择");
        sb.delete(0, sb.length());
        index = 0;
        mTitleDatas.clear();
        mCurrentDatas.clear();

        if (mDialog == null) {
            mDialog = new Dialog(mContext, R.style.dialog);
        }
        mDialog.setContentView(mInflatView);
        mDialog.setTitle("收货地址");

        //设置对其方式
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);

        Activity mAct = (Activity) mContext;
        WindowManager m = mAct.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth()); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);

        mDialog.show();

        //子线程请求数据
        mPb.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //子线程去加载数据
                loadDatas();
            }
        }).start();

    }

    private void loadDatas() {
        //context.getClass().getClassLoader().getResourceAsStream("assets/"+资源名);
        //读取本地Json字符串，解析字符串为对应的bean,赋值给对应集合
        if (mProvinceDatas.size() != 0) {
            //如果内存中有数据，就不需要再加载
            for (AddressBean datas : mProvinceDatas) {
                String province = datas.name;
                mCurrentDatas.add(province);
            }
        } else {
            String jsonString = getDatas();
            //解析
            resolveDatas(jsonString);
        }
        //主线程更新UI
        handler.post(new Runnable() {
            @Override
            public void run() {
                mPb.setVisibility(View.GONE);
                maddressAdapter.setDatas(mCurrentDatas);
            }
        });
    }

    private void resolveDatas(String jsonString) {
        if (jsonString != null) {
            Gson gson = new Gson();
            mProvinceDatas = gson.fromJson(jsonString, new TypeToken<List<AddressBean>>() {
            }.getType());
            for (AddressBean datas : mProvinceDatas) {
                //解析后先将省级名字存储到当前数据集合
                String province = datas.name;
                mCurrentDatas.add(province);
            }
        }

    }

    /**
     * 获取本地json数据
     */
    private String getDatas() {
        InputStream in = null;
        try {
            in = mContext.getClass().getClassLoader().getResourceAsStream("assets/" + "addresslist.json");
            ByteArrayOutputStream bao = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024*8];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                bao.write(buffer, 0, len);
            }

            return bao.toString();//转为string
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    //存储临时的地址
    StringBuilder sb = new StringBuilder();
    int index = 0;

    //listView条目点击事件
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String name = "";
        switch (index) {
            case 0://省级
                name = mCurrentDatas.get(i);
                AddressBean addressBean = mProvinceDatas.get(i);
                mCityDatas = addressBean.city;//市级对应的数据集合
                mCurrentDatas.clear();//为了重复使用集合，先将原有的数据清空
                for (AddressBean.CityBean cityBean : mCityDatas) {
                    mCurrentDatas.add(cityBean.name);
                }
                break;
            case 1://市级
                name = mCurrentDatas.get(i);
                AddressBean.CityBean cityBean = mCityDatas.get(i);
                List<String> area = cityBean.area;
                mCurrentDatas.clear();
                for (String s : area) {
                    mCurrentDatas.add(s);
                }
                break;
            case 2://地区
                name = mCurrentDatas.get(i);
                break;
        }

        mTitleDatas.add(name);
        //设置地址
        if (mTitleDatas.size() == 3) {
            sb.append(mTitleDatas.get(mTitleDatas.size() - 1));
            mTvContent.setText(sb.toString());//dialog中的地址标题
            mAddressTv.setText(sb.toString());//整个控件的地址
            mDialog.dismiss();
        } else {
            sb.append(mTitleDatas.get(mTitleDatas.size() - 1)).append(">");
            mTvContent.setText(sb.toString());
            mAddressTv.setText(sb.toString());//整个控件的地址
            maddressAdapter.setDatas(mCurrentDatas);
            mListView.setSelection(0);
        }
        index++;
    }
}
