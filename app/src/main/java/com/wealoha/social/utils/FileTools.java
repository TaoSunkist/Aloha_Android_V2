package com.wealoha.social.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.wealoha.social.AppApplication;
import com.wealoha.social.beans.User;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class FileTools {

    public static final File FILE_SDCARD = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS);
    /**
     * app 所有文件根目录
     */
    public static final File ROOT_PATH = new File(FILE_SDCARD + File.separator);
    public static final File ALOHA_FILE_FOLDER = ROOT_PATH;
    @Inject
    static ContextUtil mContextUtil;

    /**
     * @return
     * @Description: 文件的保存形式为：userId+当前出入的时间
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-20
     */
    public static String getFileImgName(User user) {
        StringBuilder sBuilder = null;
        if (initAlohaFolder()) {
            if (user != null) {
                sBuilder = new StringBuilder();
                sBuilder.append(user.getId());
                sBuilder.append(System.currentTimeMillis());
                sBuilder.append(".jpg");
            }
        }
        return sBuilder != null ? sBuilder.toString() : null;
    }

    public static String getAlohaFilePath() {
        return ALOHA_FILE_FOLDER.getAbsolutePath();
    }

    public static String getFileImgNameHasDir(User user) {
        StringBuilder sBuilder = null;
        if (initAlohaFolder()) {
            if (user != null) {
                sBuilder = new StringBuilder(ALOHA_FILE_FOLDER.getAbsolutePath());
                sBuilder.append("/");
                sBuilder.append(user.getId());
                sBuilder.append(System.currentTimeMillis());
                sBuilder.append(".jpg");
            }
        }
        return sBuilder != null ? sBuilder.toString() : null;
    }

    /**
     * @Description: 初始化所有的文件夹, 并删除以前的文件夹;
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-20
     */
    public static boolean initAlohaFolder() {
        boolean isInitFolder = false;
        if (isSdcardValid()) {// 如果SDCard可以用
            XL.d("FileToolsInitFolder", "SD可用");
            isInitFolder = createFolder(ALOHA_FILE_FOLDER.getAbsolutePath());
        } else {
            XL.d("FileToolsInitFolder", "SD不可用");
        }

        return isInitFolder;
    }

    public static boolean isSdCard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    public static long byFileGetFileSize(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }

    // 正在推出的时候删除用户的所有数据;
    public static String getAlohaImgPath() {
        String path = ALOHA_FILE_FOLDER.getAbsolutePath();
        if (createFolder(path)) {
            return path;
        } else {
            return null;
        }

    }

    private static final String TAG = "FileUtils";

    /**
     * 判断SD卡是否可用
     */
    public static boolean isSdcardValid() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    public static File getExternalStorage(RemoteLogUtil remoteLogUtil) throws IOException {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            // remoteLogUtil.log("FILETOOLS_GETEXTERNALSTORAGE：" + status, new
            // Exception("无法获取文件目录"));
            throw (new IOException("文件目录无法读写: state=" + state));
        }
        return Environment.getExternalStorageDirectory();
    }

    /**
     * 判断指定路径的文件是否存在
     */
    public static boolean isFileExist(String filePath) {
        try {
            return new File(filePath).exists();
        } catch (SecurityException e) {
            // e.printStackTrace();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return false;
    }

    static public void saveBitmap(String filePath, String fileName, Bitmap bm) {
        File folder = new File(filePath, fileName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (folder.exists()) {
            folder.delete();
        }
        try {
            folder.createNewFile();
        } catch (IOException e1) {
            XL.d(FileTools.class.getSimpleName(), e1.getMessage());
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(folder);
            // TODO缩放
            bm = ImageUtil.zoomBitmap(bm, 1280, 720);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
        } catch (FileNotFoundException e) {
            XL.d(FileTools.class.getSimpleName(), e.getMessage());
        } catch (IOException e) {
            XL.d(FileTools.class.getSimpleName(), e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                XL.d(FileTools.class.getSimpleName(), e.getMessage());
            }
        }
    }

    /**
     * 创建目录
     *
     * @return 如果目录已经存在，或者目录创建成功，返回true；如果目录创建失败，返回false
     */
    public static boolean createFolder(String folderPath) {
        boolean success = false;
        try {
            File folder = new File(folderPath);
            boolean exists = folder.exists();
            boolean isDirectory = folder.isDirectory();
            if (exists && isDirectory) {
                success = true;
            } else {
                success = folder.mkdirs();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * 创建指定文件的目录。例如filePath=/sdcard/aaa/bbb/ccc/1.txt，会创建/sdcard/aaa/bbb/ccc/目录。
     */
    public static boolean createFileFolder(String filePath) {
        try {
            File file = new File(filePath);
            File folders = file.getParentFile();
            if (folders.exists()) {
                return true;
            } else {
                folders.mkdirs();
                return true;
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移动指定文件到指定的路径
     */
    public static boolean copyFile(String fromPath, String toPath) {
        boolean success;
        // get channels
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel fcin = null;
        FileChannel fcout = null;
        try {
            fis = new FileInputStream(fromPath);
            fos = new FileOutputStream(toPath);
            fcin = fis.getChannel();
            fcout = fos.getChannel();

            // do the file copy
            fcin.transferTo(0, fcin.size(), fcout);
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            success = false;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            try {
                // finish up
                if (fcin != null) {
                    fcin.close();
                }
                if (fcout != null) {
                    fcout.close();
                }
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return success;
    }

    /**
     * 移动指定文件到指定的路径
     */
    public static boolean moveFile(String fromPath, String toPath) {
        try {
            File fromFile = new File(fromPath);
            File toFile = new File(toPath);
            if (fromFile.exists()) {
                return fromFile.renameTo(toFile);
            } else {
                return false;
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除指定路径的文件
     */
    public static boolean deleteFile(String filePath) {
        try {
            return new File(filePath).delete();
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * 删除文件夹下的所有内容(不包含指定文件夹本身)
     *
     * @param directory       如果传入的不是文件夹，不进行任何处理
     * @param ignoreFileNames 不删除的文件/文件夹名
     */
    static public void cleanDirectory(File directory, Set<String> ignoreFileNames) {

        if (!directory.exists()) {
            XL.w(TAG, "不存在的路径: " + directory.getAbsolutePath());
            return;
        }
        if (directory.isFile()) {
            XL.w(TAG, "不删除文件: " + directory.getAbsolutePath());
            return;
        }

        if (directory.isDirectory()) {
            // 遍历目录下所有的文件
            if (!ignoreFileNames.contains(directory.getName())) {
                for (File currentFile : directory.listFiles()) {
                    if (!ignoreFileNames.contains(currentFile.getName())) {
                        if (currentFile.isDirectory()) {
                            // 递归
                            cleanDirectory(currentFile, ignoreFileNames);
                        }
                        currentFile.delete();
                    }
                }
            }
        }
    }

    /**
     * 删除文件夹所有内容
     */
    static public void deleteFile(File file) {

        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            if (file.isDirectory()) {
                return;
            } else {
                file.delete();
            }
        } else {
            //
        }
    }

    /**
     * 删除指定文件夹中的全部文件
     */
    public static boolean cleanDirectory(String folderPath) {
        if (TextUtils.isEmpty(folderPath)) {
            return false;
        }
        try {
            for (File tempFile : new File(folderPath).listFiles()) {
                if (tempFile.isDirectory()) {
                    cleanDirectory(tempFile.getPath());
                }
                tempFile.delete();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 这个是手机内存的总空间大小
     */
    // api level 18
    // public static long getTotalInternalMemorySize() {
    // File path = Environment.getDataDirectory();
    // StatFs stat = new StatFs(path.getPath());
    // long blockSize = stat.getBlockSizeLong();
    // long totalBlocks = stat.getBlockCountLong();
    // return totalBlocks * blockSize;
    // }

    /**
     * 这个是手机内存的可用空间大小
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 这个是外部存储的可用空间大小
     */
    public static long getAvailableExternalMemorySize() {
        long availableExternalMemorySize = 0;
        if (isSdcardValid()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            availableExternalMemorySize = availableBlocks * blockSize;
        } else {
            availableExternalMemorySize = -1;
        }
        return availableExternalMemorySize;
    }

    /**
     * 这个是外部存储的总空间大小
     */
    public static long getTotalExternalMemorySize() {
        long totalExternalMemorySize = 0;
        if (isSdcardValid()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            totalExternalMemorySize = totalBlocks * blockSize;
        } else {
            totalExternalMemorySize = -1;
        }

        return totalExternalMemorySize;
    }

    /**
     * 返回一个文件大小的字符串
     *
     * @param size 文件长度（单位Byte）
     * @return 文件大小的字符串（单位是MB、KB或者Byte）
     */
    public static String fileSize(long size) {
        String str;
        if (size >= 1024) {
            str = "KB";
            size /= 1024;
            if (size >= 1024) {
                str = "MB";
                size /= 1024;
            }
        } else {
            str = "Byte";
        }
        DecimalFormat formatter = new DecimalFormat();
        /* 每3个数字用,分隔如：1,000 */
        formatter.setGroupingSize(3);
        return formatter.format(size) + str;
    }

    /**
     * 将指定文本内容写入文件（指定目录）
     */
    public static boolean writeFile(String filePath, String content, boolean append) {

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                    fileWriter = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean writeBinary(File file, byte[] data) {
        FileOutputStream fos = null;
        ByteArrayInputStream is = null;
        try {
            is = new ByteArrayInputStream(data);
            fos = new FileOutputStream(file);
            org.apache.commons.io.IOUtils.copy(is, fos);
            fos.flush();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "写入数据到文件失败: " + file, e);
            return false;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public static byte[] readBinary(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            return IOUtils.toByteArray(fis);
        } catch (Exception e) {
            Log.e(TAG, "读取文件到数组失败: " + file, e);
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * 将指定文本内容写入文件（指定文件）
     */
    public static boolean writeFile(File file, String content, boolean append) {

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, append);
            fileWriter.write(content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                    fileWriter = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 从指定位置读取文本内容（指定目录）
     */
    public static String readFile(String filePath) {

        FileReader fileReader = null;
        BufferedReader br = null;
        String content = null;
        try {
            StringBuilder sb = new StringBuilder();
            // 建立对象fileReader
            fileReader = new FileReader(filePath);
            br = new BufferedReader(fileReader);
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s).append('\n');
            }
            // 将字符列表转换成字符串
            content = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                    fileReader = null;
                }
                if (br != null) {
                    br.close();
                    br = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    /**
     * 从指定位置读取文本内容（指定文件）
     */
    public static String readFile(File file) {

        FileReader fileReader = null;
        BufferedReader br = null;
        String content = null;
        try {
            StringBuilder sb = new StringBuilder();
            // 建立对象fileReader
            fileReader = new FileReader(file);
            br = new BufferedReader(fileReader);
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s).append('\n');
            }
            // 将字符列表转换成字符串
            content = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                    fileReader = null;
                }
                if (br != null) {
                    br.close();
                    br = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    /**
     * 从assets中获取文件并读取数据（资源文件只能读不能写）
     */
    public static String readAssetsFile(Context context, String fileName) {
        String res = null;
        InputStream is = null;
        try {
            is = context.getAssets().open(fileName);
            res = readInputStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                is = null;
            }
        }
        return res;
    }

    /**
     * 从assets中获取文件并读取数据（资源文件只能读不能写）
     */
    public static String readRawFile(Context context, int fileResId) {
        String res = null;
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(fileResId);
            res = readInputStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                is = null;
            }
        }
        return res;
    }

    /**
     * 从一个InputStrem中读取String内容
     *
     * @throws java.io.IOException
     */
    private static String readInputStream(InputStream is) throws IOException {
        int length = is.available();
        byte[] buffer = new byte[length];
        is.read(buffer);
        return new String(buffer, "UTF-8");
    }

    /**
     * 将文本用Gzip压缩后写入文件（指定文件名）
     */
    public static boolean writeGzipFile(Context context, String filePath, String content) {
        File file;
        file = new File(filePath);

        FileOutputStream fos = null;
        GZIPOutputStream gos = null;
        try {
            fos = new FileOutputStream(file, false);
            gos = new GZIPOutputStream(new BufferedOutputStream(fos));
            gos.write(content.getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                gos.finish();
                gos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static String getExternalFilesAbsolutePath(Context context) {
        return isSdcardValid() ? context.getExternalFilesDir(null).getAbsolutePath() : "/sdcard/Android/data/com.sankuai.xmpp/files";
    }

    public static String getExternalCacheAbsolutePath(Context context) {
        return isSdcardValid() ? context.getExternalFilesDir(null).getAbsolutePath() : "/sdcard/Android/data/com.sankuai.xmpp/cache";
    }

    static public Bitmap getBitmapFromUri(Context context, Uri uri) {
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            Log.e("[Android]", e.getMessage());
            Log.e("[Android]", "目录为：" + uri);
            e.printStackTrace();
            return null;
        }
    }

    // @SuppressLint("NewApi")
    // public static String getPath(final Context context, final Uri uri) {
    //
    // final boolean isKitKat = Build.VERSION.SDK_INT >=
    // Build.VERSION_CODES.KITKAT;
    //
    // // DocumentProvider
    // if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
    // // ExternalStorageProvider
    // if (isExternalStorageDocument(uri)) {
    // final String docId = DocumentsContract.getDocumentId(uri);
    // final String[] split = docId.split(":");
    // final String type = split[0];
    //
    // if ("primary".equalsIgnoreCase(type)) {
    // return Environment.getExternalStorageDirectory() + "/" + split[1];
    // }
    //
    // // TODO handle non-primary volumes
    // }
    // // DownloadsProvider
    // else if (isDownloadsDocument(uri)) {
    //
    // final String id = DocumentsContract.getDocumentId(uri);
    // final Uri contentUri =
    // ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
    // Long.valueOf(id));
    //
    // return getDataColumn(context, contentUri, null, null);
    // }
    // // MediaProvider
    // else if (isMediaDocument(uri)) {
    // final String docId = DocumentsContract.getDocumentId(uri);
    // final String[] split = docId.split(":");
    // final String type = split[0];
    //
    // Uri contentUri = null;
    // if ("image".equals(type)) {
    // contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    // } else if ("video".equals(type)) {
    // contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    // } else if ("audio".equals(type)) {
    // contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    // }
    //
    // final String selection = "_id=?";
    // final String[] selectionArgs = new String[] { split[1] };
    //
    // return getDataColumn(context, contentUri, selection, selectionArgs);
    // }
    // }
    // // MediaStore (and general)
    // else if ("content".equalsIgnoreCase(uri.getScheme())) {
    //
    // // Return the remote address
    // if (isGooglePhotosUri(uri))
    // return uri.getLastPathSegment();
    //
    // return getDataColumn(context, uri, null, null);
    // }
    // // File
    // else if ("file".equalsIgnoreCase(uri.getScheme())) {
    // return uri.getPath();
    // }
    //
    // return null;
    // }
    //
    // public static boolean isExternalStorageDocument(Uri uri) {
    // return
    // "com.android.externalstorage.documents".equals(uri.getAuthority());
    // }
    //
    // public static boolean isDownloadsDocument(Uri uri) {
    // return
    // "com.android.providers.downloads.documents".equals(uri.getAuthority());
    // }
    //
    // public static String getDataColumn(Context context, Uri uri, String
    // selection, String[] selectionArgs) {
    //
    // Cursor cursor = null;
    // final String column = "_data";
    // final String[] projection = { column };
    //
    // try {
    // cursor = context.getContentResolver().query(uri, projection, selection,
    // selectionArgs, null);
    // if (cursor != null && cursor.moveToFirst()) {
    // final int index = cursor.getColumnIndexOrThrow(column);
    // return cursor.getString(index);
    // }
    // } finally {
    // if (cursor != null)
    // cursor.close();
    // }
    // return null;
    // }
    //
    // public static boolean isMediaDocument(Uri uri) {
    // return
    // "com.android.providers.media.documents".equals(uri.getAuthority());
    // }
    //
    // public static boolean isGooglePhotosUri(Uri uri) {
    // return
    // "com.google.android.apps.photos.content".equals(uri.getAuthority());
    // }
    public static String FormatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public static String getImagePathFromUri(Uri fileUrl) {
        String fileName = null;
        Uri filePathUri = fileUrl;
        if (fileUrl != null) {
            if (fileUrl.getScheme().toString().compareTo("content") == 0) {
                // content://开头的uri
                Cursor cursor = AppApplication.getInstance().getContentResolver().query(fileUrl, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    fileName = cursor.getString(column_index); // 取出文件路径

                    // Android 4.1 更改了SD的目录，sdcard映射到/storage/sdcard0
                    if (!fileName.startsWith("/storage") && !fileName.startsWith("/mnt")) {
                        // 检查是否有”/mnt“前缀
                        fileName = "/mnt" + fileName;
                    }
                    cursor.close();
                }
            } else if (fileUrl.getScheme().compareTo("file") == 0) // file:///开头的uri
            {
                fileName = filePathUri.toString();// 替换file://
                fileName = filePathUri.toString().replace("file://", "");
                int index = fileName.indexOf("/sdcard");
                fileName = index == -1 ? fileName : fileName.substring(index);

                if (!fileName.startsWith("/mnt")) {
                    // 加上"/mnt"头
                    fileName = "/mnt" + fileName;
                }
            }
        }
        return fileName;
    }

    public static long getFileSize(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }

    static public String FormetFileSize(long fileS) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("0");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 获取缓存文件目录，如果sd卡存在则缓存在sd卡中，否则缓存在内置内存中
     *
     * @param context    上下文
     * @param uniqueName 唯一文件名
     * @return File 缓存文件目录
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cacheDir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            File file = context.getExternalCacheDir();
            if (file != null) {
                cacheDir = file.getPath();
            } else {
                cacheDir = context.getCacheDir().getPath();
            }
        } else {
            cacheDir = context.getCacheDir().getPath();
        }

        return new File(cacheDir + File.separator + uniqueName);
    }

    public static boolean isExists(String path) {
        if (new File(path).exists()) {
            return true;
        } else {
            return false;
        }
    }
}
