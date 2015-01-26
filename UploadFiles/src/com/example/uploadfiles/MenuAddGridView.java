package com.example.uploadfiles;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class MenuAddGridView extends Activity {
    GridView gridview;
    SimpleAdapter gridviewAdapter;
    ImageButton openFolder,addSingle,addFolder,backFolder;
    String sdcardFilePath,thisFilePath,selectFilePath;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.setContentView(R.layout.menuaddgridview);
        
        sdcardFilePath=Environment.getExternalStorageDirectory().getAbsolutePath();//得到sdcard目录
        thisFilePath=sdcardFilePath;
        
        openFolder=(ImageButton) this.findViewById(R.id.MenuAddGridView_button_openfolder);
        openFolder.setVisibility(View.INVISIBLE);//设置不可见
        openFolder.setOnClickListener(new buttonOnClickListener());//添加监听器
        addSingle=(ImageButton) this.findViewById(R.id.MenuAddGridView_button_addSingle);
        addSingle.setVisibility(View.INVISIBLE);
        addSingle.setOnClickListener(new buttonOnClickListener());
        addFolder=(ImageButton) this.findViewById(R.id.MenuAddGridView_button_addFolder);
        addFolder.setVisibility(View.INVISIBLE);
        addFolder.setOnClickListener(new buttonOnClickListener());
        backFolder=(ImageButton) this.findViewById(R.id.MenuAddGridView_button_backFolder);
        backFolder.setOnClickListener(new buttonOnClickListener());
        
        gridview=(GridView) this.findViewById(R.id.MenuAddGridView_gridview);
        //设置gridView的数据
        updategridviewAdapter(thisFilePath);
        gridview.setOnItemClickListener(new OnItemClickListener(){
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                HashMap<String, Object> item=(HashMap<String, Object>) arg0.getItemAtPosition(arg2);
                selectFilePath=(String) item.get("ItemFilePath");
                
                if(item.get("type").equals("isDirectory"))//判断是否是文件夹
                {
                    openFolder.setVisibility(View.VISIBLE);//打开文件按钮可见
                    addSingle.setVisibility(View.INVISIBLE);//选择单曲按钮不可见
                    addFolder.setVisibility(View.VISIBLE);//选择整个文件夹按钮可见
                }
                else if(item.get("type").equals("isMp3"))
                {
                    openFolder.setVisibility(View.INVISIBLE);
                    addSingle.setVisibility(View.VISIBLE);
                    addFolder.setVisibility(View.INVISIBLE);
                }
                else
                {
                    openFolder.setVisibility(View.INVISIBLE);
                    addSingle.setVisibility(View.VISIBLE);
                    addFolder.setVisibility(View.INVISIBLE);
                }
                
            }});
            this.setResult(0);
    }
    private File[] folderScan(String path)
    {
        File file=new File(path);
        File[] files=file.listFiles();
        return files;
    }
    //设置gridView的数据
    private void updategridviewAdapter(String filePath)
    {
        File[] files=folderScan(filePath);
        ArrayList<HashMap<String, Object>> lstImageItem = getFileItems(files);
        gridviewAdapter = new SimpleAdapter(MenuAddGridView.this,lstImageItem,R.layout.menuaddgridview_item,new String[] {"ItemImage","ItemText"}, new int[] {R.id.MenuAddGridView_ItemImage,R.id.MenuAddGridView_ItemText});
        gridview.setAdapter(gridviewAdapter);
        gridviewAdapter.notifyDataSetChanged();
    }
    //列表循环判断文件类型然后提供数据给Adapter用
    private ArrayList<HashMap<String, Object>> getFileItems(File[] files)
    {
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
        //循环加入listImageItem数据
        if(files==null)
        {
            return null;
        }
        for(int i=0;i<files.length;i++)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            String fileName=files[i].getName();//得到file名
            map.put("ItemText", fileName);
            if(files[i].isDirectory())//判断是否是文件夹
            {
                map.put("ItemImage", R.drawable.folder);//显示文件夹图标
                map.put("type", "isDirectory");
            }
            else if(files[i].isFile())//判断是否是文件
            {
                if(fileName.contains(".mp3"))//判断是否是MP3文件
                {
                    map.put("ItemImage", R.drawable.mp3flie);//加入MP3图标
                    map.put("type", "isMp3");
                }
                else
                {
                    map.put("ItemImage", R.drawable.otherfile);//加入非MP3文件图标
                    map.put("type", "isOthers");
                }
            }
            map.put("ItemFilePath", files[i].getAbsolutePath());//保存文件绝对路径
            
            lstImageItem.add(map);
        }
        return lstImageItem;
    }
    private ArrayList<String> getResultArrayList(ArrayList<HashMap<String, Object>> al)
    {
        ArrayList<String> musicResult=new ArrayList<String>();
        for(int i=0;i<al.size();i++)
        {
            HashMap<String, Object> map=al.get(i);
            String type=(String) map.get("type");
            String itemFilePath=(String) map.get("ItemFilePath");
            if(type.equals("isMp3"))
            {
                musicResult.add(itemFilePath);
            }
        }
        return musicResult;
    }
    class buttonOnClickListener implements OnClickListener
    {
        ArrayList<String> Result;
        Intent intent;
        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.MenuAddGridView_button_openfolder://打开文件夹
                    updategridviewAdapter(selectFilePath);//获取文件夹下数据并显示
                    thisFilePath=selectFilePath;//记录当前目录路径
                    openFolder.setVisibility(View.INVISIBLE);
                    addSingle.setVisibility(View.INVISIBLE);
                    addFolder.setVisibility(View.INVISIBLE);
                    break;
                case R.id.MenuAddGridView_button_addSingle:
                    Result=new ArrayList<String>();
                    Result.add(selectFilePath);
                    intent=new Intent(MenuAddGridView.this,MainActivity.class);
                    intent.putStringArrayListExtra("Result", Result);
                    intent.putExtra("verify", "ok");
                //    MenuAddGridView.this.setResult(1, intent);
                    startActivity(intent);
                    MenuAddGridView.this.onDestroy();
                    break;
                case R.id.MenuAddGridView_button_addFolder:
                    //得到文件夹下所有mp3文件
                    Result=getResultArrayList(getFileItems(folderScan(selectFilePath)));
                    intent=new Intent();
                    //返回给上一个activity数据
                    intent.putStringArrayListExtra("Result", Result);
                    MenuAddGridView.this.setResult(1, intent);
                    MenuAddGridView.this.onDestroy();
                    break;
                case R.id.MenuAddGridView_button_backFolder://返回上级目录
                    if(!thisFilePath.equals(sdcardFilePath))
                    {
                        File thisFile=new File(thisFilePath);//得到当前目录
                        String parentFilePath=thisFile.getParent();//上级的目录路径
                        updategridviewAdapter(parentFilePath);//获得上级目录数据并显示
                        thisFilePath=parentFilePath;//设置当前目录路径
                        
                        openFolder.setVisibility(View.INVISIBLE);
                        addSingle.setVisibility(View.INVISIBLE);
                        addFolder.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        MenuAddGridView.this.onDestroy();
                    }
                    break;
            }
        }
        
    }
    protected void onDestroy() {
        this.finish();
        super.onDestroy();
    }
    
}