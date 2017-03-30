package com.fourway.localapp.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.fourway.localapp.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 4 way on 28-03-2017.
 */

public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.ItemHolder> {

    private List<Uri> itemsUri;
    private LayoutInflater layoutInflater;
    private Context context;
    private OnItemClickListener onItemClickListener;
//    MainActivity mainActivity;


    public GalleryRecyclerViewAdapter(List<Uri> itemsUri, Context context) {
        this.itemsUri = itemsUri;
        this.context = context;
//        itemsUri = new ArrayList<Uri>();
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView itemCardView = (CardView)layoutInflater.inflate(R.layout.gallery_card, parent, false);
        return new ItemHolder(itemCardView, this);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        Uri targetUri = itemsUri.get(position);
//        holder.setItemUri(targetUri.getPath());
        holder.setItemUri(targetUri);

        if (targetUri != null){

//            try {
                //! CAUTION
                //I'm not sure is it properly to load bitmap here!
//                holder.setImageView(loadScaledBitmap(targetUri));
//                holder.imageView.setImageBitmap(loadScaledBitmap(targetUri));

                Picasso.with(context).load(targetUri).into(holder.imageView);
//
                /*if (!new File(targetUri.getPath()).getName().contains(".jpg")) {

//                Picasso.with(context).load(targetUri).error(new BitmapDrawable(context.getResources(),loadScaledBitmap(targetUri))).resize(200, 200).centerCrop().into(holder.imageView);
                    Picasso.with(context).load(targetUri).resize(200, 200).centerCrop().into(holder.imageView);
                }else {
                    Picasso.with(context).load(getImageUri(context, loadScaledBitmap(targetUri))).resize(200, 200).centerCrop().into(holder.imageView);
                }*/

//            holder.setImageUri(targetUri);
            /*} catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/
        }
    }

    /*
    reference:
    Load scaled bitmap
    http://android-er.blogspot.com/2013/08/load-scaled-bitmap.html
     */
    private Bitmap loadScaledBitmap(Uri src) throws FileNotFoundException {

        //display the file to be loadScaledBitmap(),
        //such that you can know how much work on it.
//        mainActivity.textInfo.append(src.getLastPathSegment() + "\n");

        // required max width/height
        final int REQ_WIDTH = 150;
        final int REQ_HEIGHT = 150;

       /* Bitmap bm = null;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(src),
                null, options);

//        Bitmap  mBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, REQ_WIDTH,
                REQ_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeStream(
                context.getContentResolver().openInputStream(src), null, options);*/

        final int THUMBSIZE = 200;

        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(src.getPath()),
                THUMBSIZE, THUMBSIZE);


        return ThumbImage;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 50, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    @Override
    public int getItemCount() {
        return itemsUri.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public OnItemClickListener getOnItemClickListener(){
        return onItemClickListener;
    }

    public interface OnItemClickListener{
        public void onItemClick(ItemHolder item, int position);
    }

    public void add(int location, Uri iUri){
        itemsUri.add(location, iUri);
        notifyItemInserted(location);
    }

    public void clearAll(){
        int itemCount = itemsUri.size();

        if(itemCount>0){
            itemsUri.clear();
            notifyItemRangeRemoved(0, itemCount);
        }
    }



    public static class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private GalleryRecyclerViewAdapter parent;
        private CardView cardView;
        ImageView imageView;
        Uri itemUri;



        public ItemHolder(CardView cardView, GalleryRecyclerViewAdapter parent) {
            super(cardView);
            itemView.setOnClickListener(this);
            this.cardView = cardView;
            this.parent = parent;
            imageView = (ImageView) cardView.findViewById(R.id.item_image);
            imageView.setBackgroundColor(getColorWithAlpha(Color.BLACK, 0.2f));
        }

        public void setItemUri(Uri itemUri){
            this.itemUri = itemUri;
        }

        public Uri getItemUri(){
            return itemUri;
        }

        public void setImageView(Bitmap bitmap){
            imageView.setImageBitmap(bitmap);

        }

        public void setImageUri(Uri uri) {
            imageView.setImageURI(uri);
        }


        @Override
        public void onClick(View v) {
            final OnItemClickListener listener = parent.getOnItemClickListener();
            if(listener != null){
                listener.onItemClick(this, getLayoutPosition());
                //or use
                //listener.onItemClick(this, getAdapterPosition());
            }
        }

        public static int getColorWithAlpha(int color, float ratio) {
            int newColor = 0;
            int alpha = Math.round(Color.alpha(color) * ratio);
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            newColor = Color.argb(alpha, r, g, b);
            return newColor;
        }
    }
}
