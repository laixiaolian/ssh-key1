package com.example.chen.fzu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.chen.entity.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import http.HttpUtil;
import http.NetUtil;



public class Latest_Question extends Fragment {
    String result;
    Question question;
    public static int quelength;
    public static String que[][]=new String[10000][6];
    // Fragment管理对象
    private FragmentManager manager;
    private FragmentTransaction ft;

    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(android.os.Message message) {

            result = (String) message.obj;
            JSONArray ResultJson = JSONObject.parseArray(result);
            quelength=ResultJson.size();
            for (int i = 0; i < ResultJson.size(); i++) {
                JSONObject results = ResultJson.getJSONObject(i);
                String finalResult = results.getString("result");
                question = JSON.parseObject(finalResult, Question.class);
                System.out.println("结果是：" + finalResult);
                que[i][0] = question.getContent();//第几条问题的内容
                que[i][1]=  question.getReleaseDate();
               // que[i][1]= "2017-12-16";
                que[i][2]=String.valueOf(question.getStudentId());
                que[i][3]=String.valueOf(question.getCommentNum());
                que[i][4]=String.valueOf(question.getPraiseNum());
                System.out.println("内容：" + que[i][0]);
            }
           // ReleaseDate.setText("20150515");
//            for (int i = 0; i <quelength; i++) {
//                list.add(que[i][0]);
//
//                System.out.println("ooo"+que[i][0]);
//                System.out.println("]]]]]]555");
//            }
            for (int i = 0; i <quelength; i++) {
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("nicheng", que[i][2]);
                map.put("date", que[i][1]);
                map.put("content", que[i][0]);
                map.put("praiseNum",que[i][4]);
                map.put("commentNum",que[i][3]);
                mlist.add(map);
            }
            //listView = (ListView) super.findViewById(R.id.listView);
            MyAdapter listItem=new MyAdapter();
            listview.setAdapter(listItem);

            System.out.println("gggg");
        }
    };
    public void QuestionRequest()
    {
        final Map<String, Object> userInfo = new HashMap<String, Object>();
        userInfo.put("userask", "askquestion");
        final String user = "userask=askquestion";
        new Thread() {
            public void run() {
                String response = http.HttpUtil.doPostRequest(NetUtil.PATH_USER_QUESTION, user);
                System.out.println("111");
                android.os.Message message = android.os.Message.obtain();
                System.out.println("222");
                message.obj = response;
                System.out.println("问题返回" + response);
                System.out.println("ppp");
                mHanlder.sendMessage(message);
                System.out.println("lll");
            }
        }.start();

    }
    private ListView listview;
    private ArrayList<String> list;
    private ArrayList<Map<String,Object>> mlist;
    private SearchView searchView;
    public int clickPosition = -1;
    private TextView ReleaseDate;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //注意View对象的重复使用，以便节省资源
        QuestionRequest();
        View  view = inflater.inflate(R.layout.fragment_latest__question, container, false);
        searchView = (SearchView) view.findViewById(R.id.searchView);
        listview = (ListView) view.findViewById(R.id.listview);
        mlist = new ArrayList<Map<String,Object>>();
        ReleaseDate=(TextView)view.findViewById(R.id.date);
        list = new ArrayList<>();
        adapter = new MyAdapter();
        listview.setAdapter(adapter);

        listview.setTextFilterEnabled(true);
        //为该SearchView组件设置事件监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    //      listview.setFilterText(newText);
                } else {
                    listview.clearTextFilter();
                }
                return false;
            }
        });

        return view;
    }

    public static  MyAdapter adapter;

    class MyAdapter extends BaseAdapter implements View.OnClickListener {

        @Override
        public int getCount() {
            return mlist.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final MyViewHolder vh;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item, null);
                vh = new MyViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (MyViewHolder) convertView.getTag();
            }
            vh.title=(TextView)convertView.findViewById(R.id.title);
            vh.data=(TextView)convertView.findViewById(R.id.date);
            vh.CommentNum=(TextView)convertView.findViewById(R.id.Comment);
            vh.PraiseNum=(TextView)convertView.findViewById(R.id.Praise);
            vh.title.setText((String)mlist.get(position).get("nicheng"));
            vh.data.setText((String)mlist.get(position).get("date"));
            vh.tv_test.setText((String)mlist.get(position).get("content"));
            vh.CommentNum.setText((String)mlist.get(position).get("commentNum"));
            vh.PraiseNum.setText((String)mlist.get(position).get("praiseNum"));
            //刷新adapter的时候，getview重新执行，此时对在点击中标记的position做处理
            if (clickPosition == position) {//当条目为刚才点击的条目时
//                if (vh.selectorImg.isSelected()) {//当条目状态图标为选中时，说明该条目处于展开状态，此时让它隐藏，并切换状态图标的状态。
//                    vh.selectorImg.setSelected(false);
//                    vh.ll_hide.setVisibility(View.GONE);
//                    Log.e("listview", "if执行");
                    clickPosition = -1;//隐藏布局后需要把标记的position去除掉，否则，滑动listview让该条目划出屏幕范围，
//                    // 当该条目重新进入屏幕后，会重新恢复原来的显示状态。经过打log可知每次else都执行一次 （条目第二次进入屏幕时会在getview中寻找他自己的状态，相当于重新执行一次getview）
//                    //因为每次滑动的时候没标记得position填充会执行click
//                } else {//当状态条目处于未选中时，说明条目处于未展开状态，此时让他展开。同时切换状态图标的状态。
//                    vh.selectorImg.setSelected(true);
//                    vh.ll_hide.setVisibility(View.VISIBLE);
//
//                    Log.e("listview", "else执行");
//                }
//                ObjectAnimator//
//                        .ofInt(vh.ll_hide, "rotationX", 0.0F, 360.0F)//
//                        .setDuration(500)//
//                        .start();
                // vh.selectorImg.setSelected(true);
                vh.praise.setOnTouchListener(new View.OnTouchListener(){
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_DOWN){
                            //重新设置按下时的背景图片
                            ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.praiser));
                        }else if(event.getAction() == MotionEvent.ACTION_UP){
                            //再修改为抬起时的正常图片
                            ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.praise));
                        }
                        return false;
                    }
                });
                if(vh.praise.isSelected()){

                }

            } else {//当填充的条目position不是刚才点击所标记的position时，让其隐藏，状态图标为false。

                //每次滑动的时候没标记得position填充会执行此处，把状态改变。所以如果在以上的if (vh.selectorImg.isSelected()) {}中不设置clickPosition=-1；则条目再次进入屏幕后，还是会进入clickposition==position的逻辑中
                //而之前的滑动（未标记条目的填充）时，执行此处逻辑，已经把状态图片的selected置为false。所以上面的else中的逻辑会在标记过的条目第二次进入屏幕时执行，如果之前的状态是显示，是没什么影响的，再显示一次而已，用户看不出来，但是如果是隐藏状态，就会被重新显示出来
                vh.ll_hide.setVisibility(View.GONE);
                //vh.selectorImg.setSelected(false);

                Log.e("listview", "状态改变");
            }
//            vh.hide_1.setOnClickListener(this);
//            vh.hide_2.setOnClickListener(this);
//            vh.hide_3.setOnClickListener(this);
//            vh.hide_4.setOnClickListener(this);
//            vh.hide_5.setOnClickListener(this);
//            vh.selectorImg.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //Toast.makeText(getActivity(), "被点了", Toast.LENGTH_SHORT).show();
//                    clickPosition = position;//记录点击的position
//                    notifyDataSetChanged();//刷新adapter重新填充条目。在重新填充的过程中，被记录的position会做展开或隐藏的动作，具体的判断看上面代码
//                    //在此处需要明确的一点是，当adapter执行刷新操作时，整个getview方法会重新执行，也就是条目重新做一次初始化被填充数据。
//                    //所以标记position，不会对条目产生影响，执行刷新后 ，条目重新填充当，填充至所标记的position时，我们对他处理，达到展开和隐藏的目的。
//                    //明确这一点后，每次点击代码执行逻辑就是 onclick（）---》getview（）
//                }
//            });


            return convertView;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.hide_1:
                   // Toast.makeText(getActivity(), "点赞", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.hide_2:
                   // Toast.makeText(getActivity(), "评论", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                   // builder.setIcon(R.drawable.ic_launcher);
                   // builder.setTitle("回答问题");
                    //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.answer_dialog, null);
                    //    设置我们自己定义的布局文件作为弹出框的Content
                    builder.setView(view);

                    final EditText username = (EditText) view.findViewById(R.id.username);

                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String a = username.getText().toString().trim();

                            //    将输入的用户名和密码打印出来
                            //Toast.makeText(getActivity(), "输入了: " + a, Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();

                    break;
                case R.id.hide_3:
                    Toast.makeText(getActivity(), "查看详情", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getActivity(), Question_Detail.class);
//                    startActivity(intent);
                    break;
//                case R.id.hide_4:
//                    Toast.makeText(getActivity(), "删除", Toast.LENGTH_SHORT).show();
//                    break;
//                case R.id.hide_5:
//                    Toast.makeText(getActivity(), "属性", Toast.LENGTH_SHORT).show();
//                    break;
            }


        }


        class MyViewHolder {
            View itemView;
            TextView data;
            TextView title;
            TextView tv_test;
            TextView PraiseNum,CommentNum;
            //hide_3, hide_4, hide_5;
            ImageView selectorImg;
            LinearLayout ll_hide;
            ImageButton praise;
            public MyViewHolder(View itemView) {
                this.itemView = itemView;
                tv_test = (TextView) itemView.findViewById(R.id.tv_test);
                praise=(ImageButton)itemView.findViewById(R.id.praiseImage);
                selectorImg = (ImageView) itemView.findViewById(R.id.checkbox);
//                hide_1 = (TextView) itemView.findViewById(R.id.hide_1);
//                hide_2 = (TextView) itemView.findViewById(R.id.hide_2);
//                hide_3 = (TextView) itemView.findViewById(R.id.hide_3);
//                hide_4 = (TextView) itemView.findViewById(R.id.hide_4);
//                hide_5 = (TextView) itemView.findViewById(R.id.hide_5);
                ll_hide = (LinearLayout) itemView.findViewById(R.id.ll_hide);
            }
        }
    }

}
//    ListView list;
//    private String[] mName = {"这只是个例子1","这只是个例子2"};
//    private String[] mNum = {"1", "2"};
//    private ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
//
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.layout.fragment_wealth_value__question, null);
//        list = (ListView) view.findViewById(R.id.listView);
//        for (int i = 0; i < mNum.length; i++) {
//            Map<String, Object> item = new HashMap<String, Object>();
//            item.put("name", mName[i]);
//            item.put("num", mNum[i]);
//            mData.add(item);
//        }
//        SimpleAdapter adapter = new SimpleAdapter(getActivity(), mData, android.R.layout.simple_expandable_list_item_2,
//                new String[]{"name", "num"}, new int[]{android.R.id.text1, android.R.id.text2});
//        list.setAdapter(adapter);
//        return view;
//}