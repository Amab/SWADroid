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

import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Class for create images.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class ImageFactory {
    /**
     * Initializes a cached image loader
     * @param ctx Application context
     * @param cacheMemory Indicates if the image should be cached in memory
     * @param cacheDisk Indicates if the image should be cached on disk
     * @param imageEmpty Image resource showed on empty URL
     * @param imageFail Image resource showed on failed image loading
     * @param imageLoading Image resource showed on image loading
     * @return An initialized cached image loader
     */
    public static ImageLoader init(Context ctx, boolean cacheMemory, boolean cacheDisk,
                                   int imageEmpty, int imageFail, int imageLoading) {

        ImageLoader loader = ImageLoader.getInstance();
        Builder builder = new DisplayImageOptions.Builder();
        DisplayImageOptions options;

        builder.cacheInMemory(cacheMemory);
        builder.cacheOnDisk(cacheDisk);

        if(imageEmpty == -1) {
            builder.showImageForEmptyUri(imageLoading);
        }

        if(imageFail == -1) {
            builder.showImageOnFail(imageLoading);
        }

        if(imageLoading == -1) {
            builder.showImageOnLoading(imageLoading);
        }

        options = builder.build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ctx)
                .defaultDisplayImageOptions(options)
                .build();

        loader.init(config);

        return loader;
    }

	/**
	 * Displays a cached image
	 * @param ctx Application context
	 * @param cacheMemory Indicates if the image should be cached in memory
	 * @param cacheDisk Indicates if the image should be cached on disk
     * @param imageEmpty Image resource showed on empty URL
     * @param imageFail Image resource showed on failed image loading
     * @param imageLoading Image resource showed on image loading
	 */
	public static void displayImage(Context ctx, String uri, ImageView imageView,
			boolean cacheMemory, boolean cacheDisk, int imageEmpty, int imageFail, int imageLoading) {

        ImageLoader loader = init(ctx, cacheMemory, cacheDisk, imageEmpty,
                imageFail, imageLoading);

        loader.displayImage(uri, imageView);
	}

    /**
     * Displays a cached image
     * @param loader A cached image loader
     * @param uri Image URI
     * @param imageView ImageView in which the image will be displayed
     */
    public static void displayImage(ImageLoader loader, String uri, ImageView imageView) {
        loader.displayImage(uri, imageView);
    }
}
