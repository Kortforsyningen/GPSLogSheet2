package ref.sdfe.gpslogsheet2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Class that that makes a photo and text adapter for the SetupsFragment
 * Created by B028406 on 10/30/2017.
 */

public class PhotoList extends ArrayAdapter {
    private final Activity context;
    private final List<String> text;
    private final List<Bitmap> photo;

    public PhotoList(Activity context,
                     List<String> text,
                     List<Bitmap> photo) {
        super(context, R.layout.list_photo, text);
        this.context = context;
        this.text = text;
        this.photo = photo;

    }
    public int getCount(){
        return text.size();
    }
    public long getItemId(int position){
        return position;
    }

    @Override @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if (rowView == null) rowView = inflater.inflate(R.layout.list_photo, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.text);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.photo);

        txtTitle.setText(text.get(position));
        imageView.setImageBitmap(photo.get(position));

        return rowView;
    }
}
