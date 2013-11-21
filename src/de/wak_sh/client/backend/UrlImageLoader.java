package de.wak_sh.client.backend;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

public class UrlImageLoader extends AsyncTask<String, Void, Bitmap> {

	private String url;
	private WeakReference<ImageView> imageViewRef;
	private WeakReference<LruCache<String, Bitmap>> bitmapCacheRef;

	public UrlImageLoader(ImageView iv) {
		imageViewRef = new WeakReference<ImageView>(iv);
	}

	public UrlImageLoader(ImageView iv, LruCache<String, Bitmap> bitmapCache) {
		this(iv);
		bitmapCacheRef = new WeakReference<LruCache<String, Bitmap>>(
				bitmapCache);
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		Bitmap bmp = null;
		url = params[0];

		try {
			InputStream is = new URL(url).openStream();
			bmp = BitmapFactory.decodeStream(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		if (bmp != null && bitmapCacheRef != null) {
			LruCache<String, Bitmap> cache = bitmapCacheRef.get();
			if (cache != null) {
				cache.put(url, bmp);
			}
		}

		return bmp;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if (imageViewRef != null) {
			ImageView iv = imageViewRef.get();
			if (iv != null) {
				iv.setImageBitmap(result);
			}
		}
	}

}
