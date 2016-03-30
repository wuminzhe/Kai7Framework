package com.kai7framework.image;

import java.util.List;
import java.util.Map;


import android.content.Context;
import android.widget.ImageView;
import android.widget.SimpleAdapter;


public class Kai7SimpleAdapter extends SimpleAdapter{
	private AsyncImageViewLoader imageViewLoader;
	public Kai7SimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, int imageStub) {
		super(context, data, resource, from, to);
		imageViewLoader = new AsyncImageViewLoader(context, imageStub);
	}
	
	public Kai7SimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		imageViewLoader = new AsyncImageViewLoader(context);
	}
	
	public void setViewImage(final ImageView v, final String url) {
		imageViewLoader.load(url, v);
	}
}
