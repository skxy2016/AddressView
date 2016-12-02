package skxy.dev.addresslib.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName ${CLASS_NAME}
 * Created by Administrator on 2016/9/1.
 * DES 省级适配器
 */
public class ProvinceAdapter extends BaseAdapter {
    List<String> mDatas = new ArrayList<>();
    Context mContext;

    public ProvinceAdapter(Context contcext) {
        this.mContext = contcext;
    }

    public void setDatas(List<String> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mDatas != null) {
            return mDatas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if (mDatas != null) {
            return mDatas.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            TextView textView = new TextView(mContext);
            view = textView;
            holder.mTextView = textView;
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        String name = mDatas.get(i);
        holder.mTextView.setText(name);

        return view;
    }

    static class ViewHolder {
        TextView mTextView;
    }
}