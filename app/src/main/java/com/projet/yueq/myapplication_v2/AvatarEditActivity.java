package com.projet.yueq.myapplication_v2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by delll on 2017/5/21.
 */

public class AvatarEditActivity extends AppCompatActivity {

    private Bitmap head;// 头像Bitmap
    private static String path = null;// sd路径
    private static String head_name="head.jpg";
    private static final String EXTRA_DATA = "EXTRA_DATA";
    private static final String TABLE_NAME = "users";

    private ProgressDialog pd;
    private String resultStr = "";
    private String imgUrl="http://192.168.43.201/MyApplication";

    public static final void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, AvatarEditActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/com.projet.yueq.myapplication_v2/myHead/";
        showTypeDialog();
    }


    private void showTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.uploud_avatar, null);
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.tv_select_gallery);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.tv_select_camera);
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, 1);
                dialog.dismiss();
            }
        });
        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                        FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileProvider", new File(path+head_name)));
                startActivityForResult(intent2, 2);// 采用ForResult打开
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData());// 裁剪图片
                }

                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    //File temp = new File(path+head_name);
                    cropPhoto(FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileProvider", new File(path+head_name)));// 裁剪图片
                }

                break;
            case 3:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    head = extras.getParcelable("data");
                    if (head != null) {
                        /**
                         * 上传服务器代码
                         */
                        //uploadFile();
                        setPicToView(head);// 保存在SD卡中
                    }
                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 调用系统的裁剪功能
     *
     * @param uri
     */
    public void cropPhoto(Uri uri) {
        Log.d("clipPhoto", "clipPhoto====>" + uri);
//        this.grantUriPermission("com.android.camera", uri,
//                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent = new Intent("com.android.camera.action.CROP");
        getPermission(intent);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//请求URI授权读取
//        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);//请求URI授权写入
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        try {
            startActivityForResult(intent, 3);
        } catch(Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        //startActivityForResult(intent, 3);
    }

    private void setPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        FileOutputStream b = null;
        File file = new File(path);
        if (!file.exists()) {
            try {
                //按照指定的路径创建文件夹
                file.mkdirs();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        String fileName = path + head_name;// 图片名字
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
            b.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        pd = ProgressDialog.show(this, null, "正在上传图片，请稍候...");
//        new Thread(uploadImageRunnable).start();
    }

    /**
     * 使用HttpUrlConnection模拟post表单进行文件
     * 原理是： 分析文件上传的数据格式，然后根据格式构造相应的发送给服务器的字符串。
     **/
//    Runnable uploadImageRunnable = new Runnable() {
//        @Override
//        public void run() {
//
//
//            if(TextUtils.isEmpty(imgUrl)){
//                Toast.makeText(getApplicationContext(), "The path to the upload server has not been set", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            Map<String, String> textParams = new HashMap<String, String>();
//            Map<String, File> fileparams = new HashMap<String, File>();
//
//            try {
//                // 创建一个URL对象
//                URL url = new URL(imgUrl);
//                textParams = new HashMap<String, String>();
//                fileparams = new HashMap<String, File>();
//                // 要上传的图片文件
//                File file = new File(path+head_name);
//                fileparams.put("image", file);
//                // 利用HttpURLConnection对象从网络中获取网页数据
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                // 设置连接超时（记得设置连接超时,如果网络不好,Android系统在超过默认时间会收回资源中断操作）
//                conn.setConnectTimeout(5000);
//                // 设置允许输出（发送POST请求必须设置允许输出）
//                conn.setDoOutput(true);
//                // 设置使用POST的方式发送
//                conn.setRequestMethod("POST");
//                // 设置不使用缓存（容易出现问题）
//                conn.setUseCaches(false);
//                conn.setRequestProperty("Charset", "UTF-8");//设置编码
//                // 在开始用HttpURLConnection对象的setRequestProperty()设置,就是生成HTML文件头
//                conn.setRequestProperty("ser-Agent", "Fiddler");
//                // 设置contentType
//                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + NetUtil.BOUNDARY);
//                OutputStream os = conn.getOutputStream();
//                DataOutputStream ds = new DataOutputStream(os);
//                NetUtil.writeStringParams(textParams, ds);
//                NetUtil.writeFileParams(fileparams, ds);
//                NetUtil.paramsEnd(ds);
//                // 对文件流操作完,要记得及时关闭
//                os.close();
//                // 服务器返回的响应吗
//                int code = conn.getResponseCode(); // 从Internet获取网页,发送请求,将网页以流的形式读回来
//                // 对响应码进行判断
//                if (code == 200) {// 返回的响应码200,是成功
//                    // 得到网络返回的输入流
//                    InputStream is = conn.getInputStream();
//                    resultStr = NetUtil.readString(is);
//                } else {
//                    Toast.makeText(getApplicationContext(), "Request URL failed", Toast.LENGTH_SHORT).show();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            handler.sendEmptyMessage(0);// 执行耗时的方法之后发送消给handler
//        }
//    };
//
//    Handler handler = new Handler(new Handler.Callback() {
//
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    pd.dismiss();
//
//                    try {
//                        // 返回数据示例，根据需求和后台数据灵活处理
//                        // {"status":"1","statusMessage":"上传成功","imageUrl":"http://120.24.219.49/726287_temphead.jpg"}
//                        JSONObject jsonObject = new JSONObject(resultStr);
//
//                        // 服务端以字符串“1”作为操作成功标记
//                        if (jsonObject.optString("status").equals("1")) {
//                            BitmapFactory.Options option = new BitmapFactory.Options();
//                            // 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图，3为三分之一
//                            option.inSampleSize = 1;
//
//                            // 服务端返回的JsonObject对象中提取到图片的网络URL路径
//                            String imageUrl = jsonObject.optString("imageUrl");
//                            ContentValues values = new ContentValues();
//                            values.put("avatarUrl",imageUrl);
//                            CustomUserProvider.getInstance().getHelper().update(TABLE_NAME,values,"userId = ?", new String[]{LCChatKit.getInstance().getCurrentUserId()});
//                            Toast.makeText(getApplicationContext(), "Upload successfully", Toast.LENGTH_SHORT).show();
//                        }else{
//                            Toast.makeText(getApplicationContext(), jsonObject.optString("statusMessage"), Toast.LENGTH_SHORT).show();
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    break;
//
//                default:
//                    break;
//            }
//            return false;
//        }
//    });

    private void getPermission(Intent intent){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileProvider", new File(path+head_name));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileProvider", new File(path+head_name)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
    }

}
