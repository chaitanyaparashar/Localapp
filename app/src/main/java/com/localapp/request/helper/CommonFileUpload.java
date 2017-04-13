package com.localapp.request.helper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.localapp.appcontroller.AppController;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 4 way on 01-03-2017.
 */

public class CommonFileUpload {

    private static final String COMMON_UPLOAD_MIME_TYPE_IMAGE = "image/jpeg";
    private static final String COMMON_UPLOAD_MIME_TYPE_VIDEO = "video/mpeg";
    private static final String COMMON_UPLOAD_MIME_TYPE_AUDIO = "audio/mp3";
    private static final String COMMON_UPLOAD_SEACRH_FILE_TAG = "file";
    private static final String COMMON_UPLOAD_SEARCH_TOKEN_TAG = "User-Agent";

    public enum FileType  {
        COMMON_UPLOAD_FILE_TYPE_IMAGE,
        COMMON_UPLOAD_FILE_TYPE_VIDEO,
        COMMON_UPLOAD_FILE_TYPE_AUDIO,

        COMMON_UPLOAD_FILE_TYPE_END
    }

    private FileType mFileType;
    private String mFileName;
    private String mAppKey;
    private Map<String, String> mParams;
    private Map<String, String> mHeader;
    private String mUrl;
    private String mFileRequestTag;
    private File mFile;
    private Context mContext;
    private Uri mFileUri;
    Response.Listener<NetworkResponse> mResponseListner;
    Response.ErrorListener mErrorListner;

    public CommonFileUpload (Context c, File file, FileType file_type, String file_name, String url, String key,
                             Response.Listener<NetworkResponse> listner, Response.ErrorListener eListner){
        mContext = c;
        mFileType = file_type;
        mFileName = file_name;
        mUrl = url;
        mResponseListner = listner;
        mErrorListner = eListner;
        mFile = file; mAppKey = key;

        mParams = null; mHeader = null; mFileRequestTag = null; mFileUri = null;
    }

    public void setParam (Map <String,String> p) {mParams = p;}
    public void setHeader (Map <String,String> h) {mHeader = h;}
    public void setFileTag (String t){mFileRequestTag = t;}


    public void uploadFile(){
        VolleyMultipartRequest multipartRequest =
                new VolleyMultipartRequest(Request.Method.POST, mUrl, mResponseListner, mErrorListner)
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        return (mParams != null) ? mParams : super.getParams();
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return (mHeader != null) ? mHeader : super.getHeaders();
                    }

                    @Override
                    protected Map<String, DataPart> getByteData() {
                        Map<String, DataPart> params = new HashMap<>();
                        String fileTag = mFileRequestTag != null ? mFileRequestTag :COMMON_UPLOAD_SEACRH_FILE_TAG;
                        params.put(fileTag, new DataPart(mFileName, getFileDataFromFile(mContext, mFile), getMimeType(mFileType)));
                        return params;
                    }
                };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,//MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(multipartRequest);
    }

    private byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();

    }

    private byte[] getFileDataFromFile(Context context, File f) {
        int size = (int) f.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(f));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;
    }

    private byte[] getByteDataFromUri(Context context, Uri uri) {
        File f = new File (uri.toString());
        return getFileDataFromFile (context, f);
    }

    private String getMimeType (FileType t)
    {
        String mime;

        switch (t)
        {
            case COMMON_UPLOAD_FILE_TYPE_IMAGE:
                mime = COMMON_UPLOAD_MIME_TYPE_IMAGE;
                break;
            case COMMON_UPLOAD_FILE_TYPE_AUDIO:
                mime = COMMON_UPLOAD_MIME_TYPE_AUDIO;
                break;
            case COMMON_UPLOAD_FILE_TYPE_VIDEO:
                mime = COMMON_UPLOAD_MIME_TYPE_VIDEO;
                break;
            default:
                mime = COMMON_UPLOAD_MIME_TYPE_IMAGE;
        }
        return mime;
    }

    private String getFileNameFromUri(Uri uri)
    {
        String fileName="unknown";//default fileName
        Uri filePathUri = uri;
        if (uri.getScheme().toString().compareTo("content")==0)
        {
            Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst())
            {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//Instead of "MediaStore.Images.Media.DATA" can be used "_data"
                filePathUri = Uri.parse(cursor.getString(column_index));
                fileName = filePathUri.getLastPathSegment().toString();
            }
        }
        else if (uri.getScheme().compareTo("file")==0)
        {
            fileName = filePathUri.getLastPathSegment().toString();
        }
        else
        {
            fileName = fileName+"_"+filePathUri.getLastPathSegment();
        }
        return fileName;
    }


}
