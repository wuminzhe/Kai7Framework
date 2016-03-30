package com.kai7framework;

import android.app.Application;

public class Kai7Application extends Application {
	private String imageFileCacheDir = "ImagesCache";
	public String getImageFileCacheDir() {
		return imageFileCacheDir;
	}

	public void setImageFileCacheDir(String imageFileCacheDir) {
		this.imageFileCacheDir = imageFileCacheDir;
	}

	private static Kai7Application instance;

    public static Kai7Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
