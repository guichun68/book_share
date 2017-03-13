package zyzx.linke.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import zyzx.linke.constant.Const;
import zyzx.linke.constant.GlobalParams;

/**
 * Created by austin on 2017/3/13.
 */

public class FileUtil {
    public static String getImageFileLocation() {
        // String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMddHHmmss");
        String cutnameString = dateFormat.format(date);
        String SDState = Environment.getExternalStorageState();

        GlobalParams.image_file_location = null;
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {
            GlobalParams.image_file_location = Environment.getExternalStorageDirectory().getPath() + "/lk/"
                    + cutnameString + "lk.jpg";
        } else {
            return null;
        }
        return GlobalParams.image_file_location;
    }

    public static String uriToFilePath(Uri targetUri,Context ctx) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = ctx.getContentResolver().query(targetUri, proj, null, null, null);

        String img_path = null;
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            img_path = cursor.getString(column_index);
        }
        cursor.close();
        return img_path;
    }
    /**
     * ScalingLogic defines how scaling should be carried out if source and
     * destination image has different aspect ratio.
     *
     * CROP: Scales the image the minimum amount while making sure that at least
     * one of the two dimensions fit inside the requested destination area.
     * Parts of the source image will be cropped to realize this.
     *
     * FIT: Scales the image the minimum amount while making sure both
     * dimensions fit inside the requested destination area. The resulting
     * destination dimensions might be adjusted to a smaller size than
     * requested.
     */
    public static enum ScalingLogic {
        CROP, FIT
    }
    /**
     * Utility function for creating a scaled version of an existing bitmap
     *
     * @param unscaledBitmap
     *            Bitmap to scale
     * @param dstWidth
     *            Wanted width of destination bitmap
     * @param dstHeight
     *            Wanted height of destination bitmap
     * @param scalingLogic
     *            Logic to use to avoid image stretching
     * @return New scaled bitmap object
     */
    public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight,
                                            ScalingLogic scalingLogic, int rotate) {

        Matrix matrix = new Matrix();
        matrix.preRotate(rotate);

        Bitmap rotatedBitmap = Bitmap.createBitmap(unscaledBitmap, 0, 0, unscaledBitmap.getWidth(),
                unscaledBitmap.getHeight(), matrix, true);

        Rect srcRect = calculateSrcRect(rotatedBitmap.getWidth(), rotatedBitmap.getHeight(), dstWidth, dstHeight,
                scalingLogic);
        Log.i("srcRect", srcRect.toString());
        Rect dstRect = calculateDstRect(rotatedBitmap.getWidth(), rotatedBitmap.getHeight(), dstWidth, dstHeight,
                scalingLogic);
        Log.i("dstRect", dstRect.toString());

        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(rotatedBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }
    /**
     * Calculates source rectangle for scaling bitmap
     *
     * @param srcWidth
     *            Width of source image
     * @param srcHeight
     *            Height of source image
     * @param dstWidth
     *            Width of destination area
     * @param dstHeight
     *            Height of destination area
     * @param scalingLogic
     *            Logic to use to avoid image stretching
     * @return Optimal source rectangle
     */
    public static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
                                        ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.CROP) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                final int srcRectWidth = (int) (srcHeight * dstAspect);
                final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
                return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
            } else {
                final int srcRectHeight = (int) (srcWidth / dstAspect);
                final int scrRectTop = (int) (srcHeight - srcRectHeight) / 2;
                return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
            }
        } else {
            return new Rect(0, 0, srcWidth, srcHeight);
        }
    }

    /**
     * Calculates destination rectangle for scaling bitmap
     *
     * @param srcWidth
     *            Width of source image
     * @param srcHeight
     *            Height of source image
     * @param dstWidth
     *            Width of destination area
     * @param dstHeight
     *            Height of destination area
     * @param scalingLogic
     *            Logic to use to avoid image stretching
     * @return Optimal destination rectangle
     */
    public static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
                                        ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                return new Rect(0, 0, dstWidth, (int) (dstWidth / srcAspect));
            } else {
                return new Rect(0, 0, (int) (dstHeight * srcAspect), dstHeight);
            }
        } else {
            return new Rect(0, 0, dstWidth, dstHeight);
        }
    }
    public static int getFileOrientation(String path) {
        int rotate = 0;
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
        }
        return rotate;
    }


    public static int getGalleryOrientation(Context context, Uri path) {
		/* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(path,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);

    }
}
