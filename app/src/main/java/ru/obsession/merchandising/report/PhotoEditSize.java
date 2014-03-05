package ru.obsession.merchandising.report;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileInputStream;

public class PhotoEditSize {

    /**
     * Счетчик коэффициента изменения изображения
     *
     * @param options   опции для изменения
     * @param reqWidth  ширина будущего изображения
     * @param reqHeight высота будущего изображения
     * @return коэффициент изменения изображения
     */
    private static int calculateSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSize;
    }

    /**
     * функция преобразования изображения
     *
     * @param imagePath путь до файла
     * @param reqWidth  ширина будущего изображения
     * @param reqHeight высота будущего изображения
     * @return измененное изображение в Bitmap
     */
    public static Bitmap rotaitPhoto(String imagePath, int reqWidth, int reqHeight) {
        try {
            File f = new File(imagePath);
            ExifInterface exif = new ExifInterface(f.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }

            Matrix mat = new Matrix();
            mat.postRotate(angle);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);
            options.inSampleSize = calculateSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f),
                    null, options);
            return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                    bmp.getHeight(), mat, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }

    }
}
