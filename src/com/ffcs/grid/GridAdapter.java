package com.ffcs.grid;

import java.lang.reflect.Field;
import java.util.List;

import com.ffcs.activity.R;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//自定义GridView的适配器
public class GridAdapter extends BaseAdapter {

	//应用的图标和应用名称
	private class GridHolder {
		ImageView appImage;
		TextView appName;
	}

	private Context context;
	  
	private List<GridInfo> list;
	
    //用于获取资源对象	
	private LayoutInflater mInflater;

	public GridAdapter(Context c) {
		
		super();
		
		this.context = c;
	}

	public void setList(List<GridInfo> list) {
		this.list = list;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int index) {

		return list.get(index);
	}
	public String getItemImg(int index) {

		return (list.get(index)).getImg();
	}

	@Override
	public long getItemId(int index) {
		return index;
	}
	public void exchange(int startPosition, int endPosition){   
        
		//比较一下 使startPosition永远小于endPosition的值 解决问题1 ，2   
        
		if(startPosition > endPosition){   
           
			int temp = endPosition;   
           
			endPosition = startPosition;   
            
			startPosition = temp;   
        }   
        Object endObject = getItem(endPosition);   
        
        Object startObject = getItem(startPosition);   
            
        list.set(startPosition,(GridInfo)endObject);   
        
        list.set(endPosition,(GridInfo)startObject);   
        
        notifyDataSetChanged();   
    }   
	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		GridHolder holder;
		if (convertView == null) {   
			
			convertView = mInflater.inflate(R.layout.grid_item, null);   
			
			holder = new GridHolder();
			
			holder.appImage = (ImageView)convertView.findViewById(R.id.itemImage);
            
			this.list.get(index).setImgView(holder.appImage);
			
			holder.appName = (TextView)convertView.findViewById(R.id.itemText);
			
			this.list.get(index).setTxtView(holder.appName);
			
			convertView.setTag(holder);   

		}else{
			 holder = (GridHolder) convertView.getTag();   

		}
		GridInfo info = list.get(index);
		if (info != null) {   
			
			holder.appName.setText(info.getName());
			
			this.list.get(index).setTxtView(holder.appName);
			
			Field f;
			try {
				f = (Field)R.drawable.class.getDeclaredField(getItemImg(index));
				
				int i=f.getInt(R.drawable.class);
				
				holder.appImage.setImageResource(i);
				
				this.list.get(index).setImgView(holder.appImage);
			} catch (SecurityException e) {  

                e.printStackTrace();  

            } catch (NoSuchFieldException e) {  

               e.printStackTrace();  

           } catch (IllegalArgumentException e) {  

               e.printStackTrace();  

           } catch (IllegalAccessException e) {  

               e.printStackTrace();  

          }  
			
		}
		return convertView;
	}

}
