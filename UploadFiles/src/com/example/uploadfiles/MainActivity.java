package com.example.uploadfiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Button bt;
	private Button bt2;
	private Button setPath;
	private EditText path;
	private String Spath="default";
	private TextView tv_percent;
	
	public Handler handler = new Handler(){
		Bundle b = new Bundle();
		DecimalFormat df = new DecimalFormat();
		public void handleMessage(Message msg){
			switch(msg.what){
			case 11:
				b = msg.getData();
				tv_percent.setText(df.format(b.getDouble("percent")*100)+" % "+"has been uploaded");
				break;
			case 22:
				b= msg.getData();
				tv_percent.setText(df.format(b.getDouble("kb")*100)+" % has been downloaded");
				break;
			case 2:
				tv_percent.setText("");
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.getIntent().putExtra("verify", "ok");
		
		bt = (Button)findViewById(R.id.bt);
		bt2= (Button)findViewById(R.id.bt2);
		setPath = (Button)findViewById(R.id.bt_setpath);
		path = (EditText)findViewById(R.id.path);
		tv_percent = (TextView)findViewById(R.id.tv_percent);
		
		bt.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				new MyThread("up").start();	
			}
		});
		
		bt2.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				new MyThread("down").start();
			}	
		});
		
		setPath.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MenuAddGridView.class);
				startActivity(intent);
			}
			
		});
		
	}

	class MyThread extends Thread{
		private String name=null;
		private String flag;
		
		MyThread(String flag){
			this.flag = flag;
		}
		
		public void run(){
			try {
				int length;
				int sumL=0;
				Bundle msgb = new Bundle();
				msgb.clear();
				Socket socket = new Socket("192.168.1.102",30000);
				OutputStream os = socket.getOutputStream();
				InputStream is = socket.getInputStream();
				os.write((flag+"\n").getBytes());
				
				
				String sDStateString = Environment.getExternalStorageState();
				if(sDStateString.equals(Environment.MEDIA_MOUNTED)){
					File SDFile = Environment.getExternalStorageDirectory();
					File sdPath = new File(SDFile.getAbsolutePath());
					
/////////////////////////Upload Part//////////////////////////////////////////////					
					if(flag.equals("up")){
						sleep(5000);
					//	File file = new File(sdPath.getAbsolutePath()+"/"+path.getText().toString());
						File file = new File(Spath);
						name = new String(file.getName()+"\n");
						os.write(name.getBytes("gbk"));
						length=0;
						
						FileInputStream fis=new FileInputStream (file);
						byte[] xml = new byte[1024];
						
						
						//Message msg = new Message();
						
						
						double l = file.length();
						while ((length = fis.read(xml, 0, xml.length)) > 0) {  
			                sumL += length;    
			                Message msg = new Message();
			                msg.what = 11;
			             //   System.out.println("ÒÑ´«Êä£º"+((sumL/l)*100)+"%"); 
			                msgb.putDouble("percent",sumL/l );
			                msg.setData(msgb);
			                handler.sendMessage(msg);
			                os.write(xml, 0, length);  
			                os.flush();  
			            }
						Message msg = new Message();
						msg.what = 2;
						handler.sendMessage(msg);
						fis.close();
					}
/////////////////////Download Part///////////////////////////////////////////////////////////
					if(flag.equals("down")){
						ArrayList<String> namelist=new ArrayList<String>();
						String line=null;
						BufferedReader br = new BufferedReader(new InputStreamReader(is,"gbk"));
						while((line = br.readLine()) != null){
							namelist.add(line);
						}
						Intent intent = new Intent(MainActivity.this,MenuAddGridView_Down.class);
						intent.putStringArrayListExtra("namelist", namelist);
						startActivity(intent);
					}
/////////////////////////////////////////////////////////////////////////////////////////////					
					if(flag.equals("download")){
						File ParentFile = new File(sdPath.getAbsolutePath()+"/DC NetDisk/");
						if(!ParentFile.exists()) ParentFile.mkdirs();
						File file = new File(ParentFile.getAbsolutePath()+"/"+path.getText());
						FileOutputStream fos = new FileOutputStream(file);
						byte bt[] = new byte[1024];
						length=0;
			            
						os.write((path.getText().toString()+'\n').getBytes("gbk"));
						BufferedReader br = new BufferedReader(new InputStreamReader(is,"gbk"));
						double l = Double.valueOf(br.readLine());

			            while ((length = is.read(bt, 0, bt.length)) > 0) {
			            	sumL += length;
			            	Message msg = new Message();
			            	msg.what = 22;
			            	msgb.putDouble("kb", sumL/l);
			            	msg.setData(msgb);
			            	handler.sendMessage(msg);
			                fos.write(bt, 0, length);  
			                fos.flush();      
			            }  
			            Message msg = new Message();
			            msg.what = 2;
			            handler.sendMessage(msg);
						fos.close();
					}
					
					os.close();
					socket.close();
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	public void onResume(){
		super.onResume();
		Bundle b = this.getIntent().getExtras();
		if(b.get("verify").equals("ok")){
			this.getIntent().putExtra("verify", "");
			ArrayList<String> ASpath =null;
			if( (ASpath = b.getStringArrayList("Result"))!=null){
				Spath = ASpath.get(0);
				path.setText(Spath);
			}
			else if((ASpath = b.getStringArrayList("Result_down"))!=null){
				Spath = ASpath.get(0);
				path.setText(Spath);
				new MyThread("download").start();
			}
		}
	}
	

}
