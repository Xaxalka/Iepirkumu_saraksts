package com.example.iepirkumu_saraksts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/** Stores user photos as small JPEGs in the app's private storage. */
public final class PhotoStore {
    private static final int MAX_SIDE = 512;

    private PhotoStore() {}

    /** Decodes, downsizes, rotates and saves the image behind [uri]; returns the new file path or null. */
    public static String save(Context context, Uri uri) {
        try {
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            try (InputStream in = context.getContentResolver().openInputStream(uri)) {
                BitmapFactory.decodeStream(in, null, bounds);
            }
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = Math.max(1, Math.max(bounds.outWidth, bounds.outHeight) / (MAX_SIDE * 2));
            Bitmap bitmap;
            try (InputStream in = context.getContentResolver().openInputStream(uri)) {
                bitmap = BitmapFactory.decodeStream(in, null, opts);
            }
            if (bitmap == null) return null;

            int rotation = 0;
            try (InputStream in = context.getContentResolver().openInputStream(uri)) {
                int o = new ExifInterface(in).getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                if (o == ExifInterface.ORIENTATION_ROTATE_90) rotation = 90;
                else if (o == ExifInterface.ORIENTATION_ROTATE_180) rotation = 180;
                else if (o == ExifInterface.ORIENTATION_ROTATE_270) rotation = 270;
            } catch (IOException ignored) {
            }

            float scale = Math.min(1f, (float) MAX_SIDE / Math.max(bitmap.getWidth(), bitmap.getHeight()));
            Matrix m = new Matrix();
            m.postScale(scale, scale);
            m.postRotate(rotation);
            Bitmap out = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);

            File dir = new File(context.getFilesDir(), "photos");
            if (!dir.exists() && !dir.mkdirs()) return null;
            File file = new File(dir, UUID.randomUUID() + ".jpg");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                out.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            }
            return file.getAbsolutePath();
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    public static Bitmap load(String path) {
        return path == null ? null : BitmapFactory.decodeFile(path);
    }

    public static void delete(String path) {
        if (path != null) //noinspection ResultOfMethodCallIgnored
            new File(path).delete();
    }
}
