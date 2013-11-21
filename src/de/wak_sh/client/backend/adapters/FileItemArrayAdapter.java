package de.wak_sh.client.backend.adapters;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import de.wak_sh.client.R;
import de.wak_sh.client.backend.UrlImageLoader;
import de.wak_sh.client.backend.model.FileItem;

public class FileItemArrayAdapter extends ArrayAdapter<FileItem> implements
		OnClickListener {

	private FragmentInterface fragmentInterface;
	private LruCache<String, Bitmap> bitmapCache;

	public interface FragmentInterface {
		public void doRename(FileItem item);

		public void doDelete(FileItem item);
	}

	public FileItemArrayAdapter(Context context, List<FileItem> objects,
			FragmentInterface fragmentInterface) {
		super(context, 0, objects);
		this.fragmentInterface = fragmentInterface;

		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;
		bitmapCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return (value.getWidth() * value.getHeight()) / 1024;
			}
		};
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.file_list_item, parent,
					false);
		}

		FileItem item = getItem(position);

		ImageView image = (ImageView) convertView
				.findViewById(R.id.imageView_icon);
		TextView text = (TextView) convertView.findViewById(R.id.file_name);
		TextView date = (TextView) convertView.findViewById(R.id.file_date);
		Button button = (Button) convertView.findViewById(R.id.file_actions);

		text.setSelected(true);
		text.setText(item.getName());
		date.setText(item.getDate().trim());

		if (item.isOwner()) {
			button.setTag(position);
			button.setBackgroundResource(android.R.drawable.ic_menu_more);
			button.setOnClickListener(this);
		} else {
			button.setBackgroundResource(0);
		}

		Bitmap bmpGame = bitmapCache.get("http://www.wak-sh.de/"
				+ item.getIconPath());
		if (bmpGame != null) {
			image.setImageBitmap(bmpGame);
		} else {
			new UrlImageLoader(image, bitmapCache)
					.execute("http://www.wak-sh.de/" + item.getIconPath());
		}

		return convertView;
	}

	@Override
	public void onClick(View view) {
		int index = (Integer) view.getTag();
		final FileItem item = getItem(index);

		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setItems(R.array.file_operations,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							fragmentInterface.doDelete(item);
							break;
						case 1:
							fragmentInterface.doRename(item);
							break;
						}
					}
				});
		builder.create().show();
	}

}