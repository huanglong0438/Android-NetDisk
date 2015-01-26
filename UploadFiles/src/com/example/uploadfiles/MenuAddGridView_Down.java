package com.example.uploadfiles;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.uploadfiles.MenuAddGridView.buttonOnClickListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class MenuAddGridView_Down extends Activity{
	GridView gridview;
    SimpleAdapter gridviewAdapter;
    ImageButton openFolder,addSingle,addFolder,backFolder;
    String sdcardFilePath,thisFilePath,selectFilePath;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.setContentView(R.layout.menuaddgridview);
        
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
        updategridviewAdapter();
        
        gridview.setOnItemClickListener(new OnItemClickListener(){
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                HashMap<String, Object> item=(HashMap<String, Object>) arg0.getItemAtPosition(arg2);
                selectFilePath = (String) item.get("ItemText");
                //selectFilePath=(String) item.get("ItemFilePath");
                
                /*if(item.get("type").equals("isDirectory"))//判断是否是文件夹
                {
                    openFolder.setVisibility(View.VISIBLE);//打开文件按钮可见
                    addSingle.setVisibility(View.INVISIBLE);//选择单曲按钮不可见
                    addFolder.setVisibility(View.VISIBLE);//选择整个文件夹按钮可见
                }*/
                if(item.get("type").equals("isMp3"))
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
	}
	
	private void updategridviewAdapter()
    {
        //File[] files=folderScan(filePath);
		Bundle b = new Bundle();
		b = this.getIntent().getExtras();
		ArrayList<String> namelist = b.getStringArrayList("namelist");
		String names[] = (String[])namelist.toArray(new String[namelist.size()]);
		ArrayList<HashMap<String, Object>> lstImageItem = getFileItems(names);
		
		
        gridviewAdapter = new SimpleAdapter(MenuAddGridView_Down.this,lstImageItem,R.layout.menuaddgridview_item,new String[] {"ItemImage","ItemText"}, new int[] {R.id.MenuAddGridView_ItemImage,R.id.MenuAddGridView_ItemText});
        gridview.setAdapter(gridviewAdapter);
        gridviewAdapter.notifyDataSetChanged();
    }
	
	private ArrayList<HashMap<String, Object>> getFileItems(String[] names)
    {
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
        //循环加入listImageItem数据
        if(names==null)
        {
            return null;
        }
        for(int i=0;i<names.length;i++)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            String fileName=names[i];//得到file名
            map.put("ItemText", fileName);
            /*if(files[i].isDirectory())//判断是否是文件夹,直接排除这种情况
            {
                map.put("ItemImage", R.drawable.folder);//显示文件夹图标
                map.put("type", "isDirectory");
            }*/
            //else if(files[i].isFile())//判断是否是文件
            //{
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
            //}
            //map.put("ItemFilePath", files[i].getAbsolutePath());//保存文件绝对路径
            
            lstImageItem.add(map);
        }
        return lstImageItem;
    }
	
	
	class buttonOnClickListener implements OnClickListener
    {
        ArrayList<String> Result;
        Intent intent;
        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.MenuAddGridView_button_addSingle:
                    Result=new ArrayList<String>();
                    Result.add(selectFilePath);
                    intent=new Intent(MenuAddGridView_Down.this,MainActivity.class);
                    intent.putStringArrayListExtra("Result_down", Result);
                    intent.putExtra("verify", "ok");
                //    MenuAddGridView.this.setResult(1, intent);
                    startActivity(intent);
                    MenuAddGridView_Down.this.onDestroy();
                    break;
            }
        }
        
    }
	
	protected void onDestroy() {
        this.finish();
        super.onDestroy();
    }

}
