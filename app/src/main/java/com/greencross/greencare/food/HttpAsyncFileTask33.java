package com.greencross.greencare.food;

import android.os.AsyncTask;

import com.greencross.greencare.util.Logger;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 사용법
 * 녹십자 제공 소스
 * String param = "77777788889";
 String url = "http://m.shealthcare.co.kr/SK/SKUPLOAD/skfood_upload.ashx";
 String fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/name/capture.jpg";

 HttpAsyncFileTask33 rssTask = new HttpAsyncFileTask33(this);
 rssTask.setParam(url, param, fileName);
 rssTask.execute();
 */
public class HttpAsyncFileTask33 extends AsyncTask<String, Void, String> {
    private static final String TAG = HttpAsyncFileTask33.class.getSimpleName();

    private HttpAsyncTaskInterface atv;
	private String param = "";
	private static FileInputStream mFileInputStream = null;
	private static URL connectUrl = null;

	static String lineEnd = "\r\n";
	static String twoHyphens = "--";
	static String boundary = "*****";

	String page; 
	String fileName;

	public HttpAsyncFileTask33(HttpAsyncTaskInterface atv) {
		this.atv = atv;
	}

	public void setParam(String param, String fileName) {
		this.param = param;
		this.fileName = fileName;
		this.page = "http://m.shealthcare.co.kr/SK/SKUPLOAD/skfood_upload.ashx";//page;
	}

	@Override
	protected void onPreExecute() {
		atv.onPreExecute();
	}

	@Override
	protected String doInBackground(String... urls) {
		return getJSONData(); 
	}

	@Override
	protected void onPostExecute(String data)  {
		atv.onFileUploaded(data);
	}

	public String getJSONData() {
		String result = HttpFileUpload(page, param, fileName); 
		return result;
	} 	

	private static String HttpFileUpload(String urlString , String param, String fileName) {
		String result = null;
		try{
			mFileInputStream = new FileInputStream(fileName);
			urlString+="?sn="+param;
			connectUrl = new URL(urlString);
			Logger.i(TAG, "mFileInputStream is " + mFileInputStream);

			HttpURLConnection conn = (HttpURLConnection)connectUrl.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

			DataOutputStream dos = new DataOutputStream( conn.getOutputStream()) ;
			dos.writeBytes(twoHyphens + boundary + lineEnd);

			dos.writeBytes("Content-Disposition:form-data;name=\"uploadedfile\";filename=\"" + fileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);
			
			int bytesAvailable = mFileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bufferSize];
			int bytesRead = mFileInputStream.read( buffer , 0 , bufferSize);
            Logger.i(TAG, "image byte is " + bytesRead );

			while(bytesRead > 0 ){
				dos.write(buffer , 0 , bufferSize);
				bytesAvailable = mFileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = mFileInputStream.read(buffer,0,bufferSize);
			}

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			int serverResponseCode = conn.getResponseCode();
		    String serverResponseMessage = conn.getResponseMessage();

            Logger.i(TAG, "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
		    if(serverResponseCode == 200){
		       
		    }

            Logger.i(TAG,  "File is written");
			mFileInputStream.close();
			dos.flush(); // 버퍼에 있는 값을 모두 밀어냄

			//웹서버에서 결과를 받아 EditText 컨트롤에 보여줌
			int ch;
			InputStream is = conn.getInputStream();
            Logger.i(TAG,   "is "+ is);
			StringBuffer b = new StringBuffer();
			while((ch = is.read()) != -1 ){
				b.append((char)ch);
			}

			result = b.toString();
            Logger.i(TAG,  "result = " + result);

			dos.close();
		}catch(Exception e){
            Logger.e(TAG,  "exception " + e.getMessage());
			result = null;
		}
		return result;
	}
}