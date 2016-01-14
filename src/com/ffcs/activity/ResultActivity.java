package com.ffcs.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

import com.ffcs.mylocation.MyLocationManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class ResultActivity extends Activity {
	
	private String result = " \n";
	private Button btn_Return = null;
	private Button btn_Top = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
//		String urlPath = CaptureActivity.getUrlGet();
//		EditText etGet = (EditText) findViewById(R.id.etGet); // 获得EditText对象
//		etGet.setText(url);
	//  getAccount(url);
		MyLocationManager mgr = new MyLocationManager(this);
		String loc =  mgr.getLat() + "," + mgr.getLng();
		EditText result = (EditText) findViewById(R.id.View); // 获得EditText对象
		result.setText(loc);
		result.setTextSize(18);
		
		/*//获取解析xml
		try {

//            String xml = HttpClientUtil.get(urlPath, "requestData", null);
//            String xml = getContent(urlPath, "UTF-8");
			Map paraMap = new HashMap();
			paraMap.put("m", "lg_active");
			paraMap.put("c", "index");
			paraMap.put("a", "checkcode");
			paraMap.put("key", "123456");
			paraMap.put("code", "283c1b72b28f36f316e7161eb34aa3a6");
			String xmlString = HttpRequestUtil.doGet(urlPath, paraMap, "UTF-8");
			XMLParser xmlParser = new XMLParser(xmlString.trim());
			String name = xmlParser.getNodeValue("/root/persons/person/name");
			String sex = xmlParser.getNodeValue("/root/persons/person/sex");
			String company = xmlParser.getNodeValue("/root/persons/person/company");
			String email = xmlParser.getNodeValue("/root/persons/person/email");
			String starttime = xmlParser.getNodeValue("/root/persons/person/starttime");
			String address = xmlParser.getNodeValue("/root/persons/person/address");
			String number = xmlParser.getNodeValue("/root/persons/person/number");
			String mid = xmlParser.getNodeValue("/root/persons/person/mid");
			String meeting = xmlParser.getNodeValue("/root/persons/person/meeting");
			String code = xmlParser.getNodeValue("/root/persons/person/code");
			String id = xmlParser.getNodeValue("/root/persons/status/id");
			String des = xmlParser.getNodeValue("/root/persons/status/des");
//			Person per = new Person();
			person.setId(id);
			person.setName(name);
			person.setSex(sex);
			person.setCompany(company);
			person.setEmail(email);			
			person.setDes(des);
			person.setStarttime(starttime);
			person.setAddress(address);
			person.setNumber(number);
			person.setMid(mid);
			person.setMeeting(meeting);
			person.setCode(code);
			result += person.toString() + "\n";
            
            FileOutputStream out = new FileOutputStream(new File(
					Environment.getExternalStorageDirectory(), "Person.xml")); 
            FileOutputStream out = openFileOutput(fileName,  
                    Activity.MODE_PRIVATE);  

			OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
			writer.write(xml);
			writer.flush();
			writer.close();
			out.close();

   //         InputStream inputStream = this.getClassLoader().getResourceAsStream("/Person.xml");
			InputStream inputStream= this.openFileInput("Person.xml");
           
            SAXParserFactory factory = SAXParserFactory.newInstance();

            SAXParser parser = factory.newSAXParser();

            XMLReader reader = parser.getXMLReader();

            reader.setContentHandler(getRootElement().getContentHandler());

            reader.parse(new InputSource(inputStream));	


            Log.i("通知", "解析完毕");

        }catch (Exception e) {

            e.printStackTrace();

        }

        for(Person person:PersonList){ 

            result += person.toString() + "\n";
             

        }

        TextView textView = (TextView)findViewById(R.id.textView);
        
        textView.setText(result);
        
        textView.setTextSize(18);
        
        if("1".equals(person.getId())){
        	
        	textView.setTextColor(Color.GREEN);
        	
        }else {
        	
        	textView.setTextColor(Color.RED);
        }
        
     
}
	
	class ButtonListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			
			Intent intent = new Intent();
			
			if (view.getId() == R.id.btnReturn) {
								
				intent.setClass(ResultActivity.this, CaptureActivity.class);
				
				ResultActivity.this.startActivity(intent);
				ResultActivity.this.finish();

			}else{
								
				intent.setClass(ResultActivity.this, FirstActivity.class);
				
				ResultActivity.this.startActivity(intent);
				ResultActivity.this.finish();
				
			}			
		}}
	
	public static String getContent(String urlPath, String encoding)
			throws Exception {
		// TODO Auto-generated method stub
		URL url = new URL(urlPath);
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(6 * 1000);
			if (conn.getResponseCode() == 200) {

				// 定义一个输入流，获取从服务器返回的数据
				InputStream inStream = conn.getInputStream();
				// 调用readStream方法对输入流进行处理。返回的一个字节数组。
				byte[] data = readStream(inStream);
				// 得到返回值。
				// 以字符串形式的返回。
				return new String(data, encoding);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	public static byte[] readStream(InputStream inStream) throws Exception {
		// readStream获得了传递进来的输入流
		// 要返回输入流，就需要定义一个输出流。
		// 定义一个字节数组型的输出流，ByteArrayOutputStream
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// 建立一个缓冲区buffer
		byte[] buffer = new byte[1024];
		int len = -1;
		// 将输入流不断的读，并放到缓冲区中去。直到读完
		while ((len = inStream.read(buffer)) != -1) {
			// 将缓冲区的数据不断的写到内存中去。边读边写
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		// 将输出流以字节数组的方式返回
		return outStream.toByteArray();
	}
	
	private RootElement getRootElement() {
		// TODO Auto-generated method stub
		RootElement rootElement = new RootElement("persons");
		Element personElement = rootElement.getChild("person");
		Element _personElement = rootElement.getChild("status");
		// 读到元素开始位置时触发，如读到<person>时
		personElement.setStartElementListener(new StartElementListener(){
			@Override
            public void start(Attributes attributes) {

                // TODO Auto-generated method stub

                Log.i("通知", "start");

                person = new Person();

            }
			
		});
		//读到元素结束位置时触发，如读到</person>时
		personElement.setEndElementListener(new EndElementListener() {

            @Override

            public void end() {

                PersonList.add(person);

            }

        });
		 Element nameElement = personElement.getChild("name");
		// 读到文本的末尾时触发,这里的vip即为文本的内容部分

	        nameElement.setEndTextElementListener(new EndTextElementListener() {

	            @Override

	            public void end(String vip) {

	                person.setName(vip);

	            }

	        });
	        Element companyElement = personElement.getChild("company");

	        companyElement.setEndTextElementListener(new EndTextElementListener() {

	            @Override

	            public void end(String vip) {

	                person.setCompany(vip);

	            }

	        });
	        Element emailElement = personElement.getChild("email");

	        emailElement.setEndTextElementListener(new EndTextElementListener() {

	            @Override

	            public void end(String vip) {

	                person.setEmail(vip);

	            }

	        });
	        Element starttimeElement = personElement.getChild("starttime");

	        starttimeElement.setEndTextElementListener(new EndTextElementListener() {

	            @Override
				public void end(String vip) {
					
					person.setStarttime(vip);
	            	
				}

	        });
	        Element addressElement = personElement.getChild("address");

	        addressElement.setEndTextElementListener(new EndTextElementListener() {

	            @Override

	            public void end(String vip) {

	                person.setAddress(vip);

	            }

	        });
	        Element numberElement = personElement.getChild("number");

	        numberElement.setEndTextElementListener(new EndTextElementListener() {

	            @Override

	            public void end(String vip) {

	                person.setNumber(vip);

	            }

	        });
	        Element codeElement = personElement.getChild("code");

	        codeElement.setEndTextElementListener(new EndTextElementListener() {

	            @Override

	            public void end(String vip) {

	                person.setCode(vip);

	            }

	        });
	        Element sexElement = personElement.getChild("sex");

	        sexElement.setEndTextElementListener(new EndTextElementListener() {

	            @Override

	            public void end(String vip) {

	                person.setSex(vip);

	            }

	        });
	        Element midElement = personElement.getChild("mid");

	        midElement.setEndTextElementListener(new EndTextElementListener() {

	            @Override

	            public void end(String vip) {

	                person.setMid(vip);

	            }

	        });
	        Element meetingElement = personElement.getChild("meeting");

	        meetingElement.setEndTextElementListener(new EndTextElementListener() {

	            @Override

	            public void end(String vip) {

	                person.setMeeting(vip);

	            }

	        });
	        Element idElement = _personElement.getChild("id");

	        idElement.setEndTextElementListener(new EndTextElementListener() {

	            @Override

	            public void end(String vip) {

	                person.setId(vip);

	            }

	        });
	        Element desElement = _personElement.getChild("des");

	        desElement.setEndTextElementListener(new EndTextElementListener() {

	            @Override

	            public void end(String vip) {

	                person.setDes(vip);

	            }

	        });
	        
		return rootElement;*/
	}
 }

