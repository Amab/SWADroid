/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2010 Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *
 *  SWADroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SWADroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SWADroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ugr.swad.swadroid.gui;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Context;
import android.widget.ImageView;

/**
 * Class for create images.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class ImageFactory {
	/**
	 * Displays a cached image
	 * @param ctx Application context
	 * @param uri Image URI
	 * @param imageView ImageView in which the image will be displayed
	 * @param cacheMemory Indicates if the image should be cached in memory
	 * @param cacheDisk Indicates if the image should be cached on disk
	 */
	public static void displayImage(Context ctx, String uri, ImageView imageView,
			boolean cacheMemory, boolean cacheDisk) {
		
		Builder builder = new DisplayImageOptions.Builder();
		DisplayImageOptions options;
		
		if(cacheMemory) {
			builder.cacheInMemory(true);
		}
		
		if(cacheDisk) {
			builder.cacheOnDisk(true);
		}
		
		options = builder.build();
    	
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ctx)
		            .defaultDisplayImageOptions(options)
		            .build();
		
		ImageLoader.getInstance().init(config);
		
		ImageLoader.getInstance().displayImage(uri, imageView);
	}
}
