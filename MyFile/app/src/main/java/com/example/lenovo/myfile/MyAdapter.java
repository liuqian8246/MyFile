package com.example.lenovo.myfile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by lenovo on 2015/11/16.
 */
public class MyAdapter extends BaseAdapter{

    //上下文
    private Context context;
    //数据
    private ArrayList<File> files;
    //布局服务
    private LayoutInflater inflater;

    public MyAdapter(Context context,ArrayList<File> files) {
        this.context = context;
        this.files = files;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public File getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ButtonListener listener;
        MyHolder myHolder;
        //查看是否有复用框
        if(convertView == null) {
            convertView = inflater.inflate(
                    R.layout.activity_fileitem,
                    parent,
                    false
            );

            myHolder = new MyHolder();
            //获得数据
            myHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            myHolder.button = (Button) convertView.findViewById(R.id.button);
            myHolder.textViewTitle = (TextView) convertView.findViewById(R.id.textView_title);
            myHolder.textViewName = (TextView) convertView.findViewById(R.id.textView_name);

            listener = new ButtonListener();
            myHolder.button.setOnClickListener(listener);
            myHolder.button.setTag(listener);
            //绑定存储
            convertView.setTag(myHolder);
        }else{
            //有可以复用的
            myHolder = (MyHolder) convertView.getTag();

            listener = (ButtonListener) myHolder.button.getTag();
        }

        File file = files.get(position);
        myHolder.textViewTitle.setText(file.getName());
        if(file.exists()) {
            if(file.isDirectory()) {
                    File[] f = file.listFiles();
                    myHolder.imageView.setImageResource(R.drawable.ic_folder);
                    myHolder.textViewName.setText("目录：" + f.length);
                } else {
                    myHolder.textViewName.setText("文件大小：" + file.length());
                    myHolder.imageView.setImageResource(R.drawable.ic_file);
                }

        }

        listener.setFile(file);
        return convertView;
    }
    class ButtonListener implements View.OnClickListener{

        private File f;
        @Override
        public void onClick(View v) {
            PopupMenu menu = new PopupMenu(context,v);
            menu.inflate(R.menu.pupro);
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.edit:
                            doEdit();
                            break;
                        case R.id.remove:
                            doRemove();
                            break;
                    }
                    return true;
                }
                private void doRemove() {
                    f.delete();
                    files.remove(f);
                    notifyDataSetChanged();
                }

                private void doEdit() {
//                    String path = f.getAbsolutePath();
//                    File fi = new File(path + "副本");
                }
            });
            menu.show();
        }

            public void setFile(File s) {
                this.f = s;
            }

    }

    private static class  MyHolder{
        ImageView imageView;
        TextView textViewTitle;
        TextView textViewName;
        Button button;

    }
}
