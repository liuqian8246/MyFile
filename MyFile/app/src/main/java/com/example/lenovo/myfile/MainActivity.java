package com.example.lenovo.myfile;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    File sdPath;
    ListView listView;
    ArrayList<File> arr;
    GridView gridView;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
//        // 获得SD 卡状态
//        String state = Environment.getExternalStorageState();
        initView(R.id.layout_L);
    }

    /**
     * 初始化
     *
     * @param id
     */
    private void initView(int id) {
        sdPath = Environment.getExternalStorageDirectory();
        arr = getArrayList(sdPath.listFiles());
        adapter = new MyAdapter(this, arr);

        if (id == R.id.layout_L) {
            //线性布局
            listView = (ListView) findViewById(R.id.listView);

            //设置适配器
            listView.setAdapter(adapter);

            gridView = null;
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new CabListener());

            //添加监听器
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("长度", String.valueOf(arr.size()));
                    File file = arr.get(position);
                    if (file.isDirectory()) {
                        if (file.length() != 0) {
                            arr = getArrayList(file.listFiles());
                            adapter = new MyAdapter(MainActivity.this, arr);
                            listView.setAdapter(adapter);
                        }
                    } else {
                        //如果不是文件夹 则打开文件
                        Intent intent = new Intent();

                        intent.setAction(Intent.ACTION_VIEW);
                        //获取文件的扩展名
                        String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);


                        //获取文件的类型
                        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                        String type = mimeTypeMap.getMimeTypeFromExtension(ext);

                        Uri uri = Uri.fromFile(file);

                        if (type != null) {
                            intent.setDataAndType(uri, type);

                            startActivity(intent);
                        }
                    }

                }
            });
        } else {
            //网格布局
            gridView = (GridView) findViewById(R.id.gridView);

            //设置适配器
            gridView.setAdapter(adapter);
            listView = null;

            gridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            gridView.setMultiChoiceModeListener(new CabListener());

            //添加监听器
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    File file = arr.get(position);
                    if (file.isDirectory()) {
                        if (file.length() != 0) {
                            arr = getArrayList(file.listFiles());
                            adapter = new MyAdapter(MainActivity.this, arr);
                            listView.setAdapter(adapter);
                        }

                    }
                }
            });

        }


    }

    // Cab菜单
    class CabListener implements AbsListView.MultiChoiceModeListener {

        /**
         * 改变选中状态
         *
         * @param mode
         * @param position
         * @param id
         * @param checked
         */
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            int count;
            if (listView != null) {
                count = listView.getCheckedItemCount();
            } else {
                count = gridView.getCheckedItemCount();
            }
            mode.setTitle("选中");
            mode.setSubtitle(String.valueOf(count));
        }

        /**
         * 创建动作模式
         *
         * @param mode
         * @param menu
         * @return
         */
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.cab_menu, menu);
            Log.d("启动", "onCreateActionMode");
            return true;
        }

        /**
         * 预处理
         *
         * @param mode
         * @param menu
         * @return
         */
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        /**
         * 选择动作
         *
         * @param mode
         * @param item
         * @return
         */
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.action_remove:
                    doRemove(mode);
                    break;
                case R.id.action_copy:
                    doCopy(mode);
                    break;
            }
            return false;
        }

        /**
         * 销毁菜单
         *
         * @param mode
         */
        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }

        //复制文件夹
        private void doCopy(ActionMode mode) {
            SparseBooleanArray array;
            if (listView != null) {
                array = listView.getCheckedItemPositions();
            } else {
                array = gridView.getCheckedItemPositions();
                Log.d("长度", String.valueOf(array.size()));
            }

            for (int i = array.size() - 1; i >= 0; i--) {
                int key = array.keyAt(i);
                if (array.get(key)) {

                    File f = arr.get(key);
                    String path = f.getAbsolutePath();
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            adapter.notifyDataSetChanged();
            mode.finish();
        }
    }


    //删除文件夹
    private void doRemove(ActionMode mode) {
        SparseBooleanArray array;
        if (listView != null) {
            array = listView.getCheckedItemPositions();
        } else {
            array = gridView.getCheckedItemPositions();
            Log.d("长度", String.valueOf(array.size()));
        }

        for (int i = array.size() - 1; i >= 0; i--) {
            int key = array.keyAt(i);
            if (array.get(key)) {

                File f = arr.remove(key);
                f.delete();
            }
        }
        adapter.notifyDataSetChanged();
        mode.finish();
    }


    /**
     * 把获取到的file[]存到集合里面
     *
     * @param files
     * @return
     */
    private ArrayList getArrayList(File[] files) {
        arr = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            arr.add(files[i]);
        }
        return arr;
    }

    /**
     * 创建选项菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        int groupId = item.getGroupId();
        if (groupId == R.id.sort_group) {
            item.setChecked(true);
        }
        switch (id) {
            case R.id.sort_char:
                Collections.sort(arr);
                adapter.notifyDataSetChanged();
//                if(listView!=null) {
//                    listView.setAdapter(new MyAdapter(MainActivity.this, arr));
//                } else {
//                    gridView.setAdapter(new MyAdapter(MainActivity.this,arr));
//                }
                break;
            case R.id.sort_file:

                break;
            case R.id.sort_sizeMax:
                Collections.sort(arr, new CompareBySizeMax());
                adapter.notifyDataSetChanged();
//                if(listView!=null) {
//                    listView.setAdapter(new MyAdapter(MainActivity.this, arr));
//                } else {
//                    gridView.setAdapter(new MyAdapter(MainActivity.this,arr));
//                }
                break;
            case R.id.sort_sizeMin:
                Collections.sort(arr, new CompareBySizeMin());
                adapter.notifyDataSetChanged();
//                if(listView!=null) {
//                    listView.setAdapter(new MyAdapter(MainActivity.this, arr));
//                } else {
//                    gridView.setAdapter(new MyAdapter(MainActivity.this,arr));
//                }
                break;
            case R.id.sort_time:
                Collections.sort(arr, new CompareByTime());
                adapter.notifyDataSetChanged();
//                if(listView!=null) {
//                    listView.setAdapter(new MyAdapter(MainActivity.this, arr));
//                } else {
//                    gridView.setAdapter(new MyAdapter(MainActivity.this,arr));
//                }
                break;
            case R.id.select:
                break;
            case R.id.layout_L:
                setContentView(R.layout.activity_file);
                initView(R.id.layout_L);
                break;
            case R.id.layout_G:
                setContentView(R.layout.activity_grid);
                initView(R.id.layout_G);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
