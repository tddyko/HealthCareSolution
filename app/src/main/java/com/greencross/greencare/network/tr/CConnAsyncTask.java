/**
 * SiseCommon_DEV
 * 2011. 9. 2. 오후 2:09:17
 * shinys
 */
package com.greencross.greencare.network.tr;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.greencross.greencare.BaseActivity;
import com.greencross.greencare.R;
import com.greencross.greencare.main.MainFragment;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shinys
 *
 */
public class CConnAsyncTask extends AsyncTask<CConnAsyncTask.CConnectorListener, Integer, CConnAsyncTask.CQueryResult> {
	protected static final String	TAG	= CConnAsyncTask.class.getSimpleName();

	public static abstract class CConnectorListener {
		public Object	tag;

		public Object getTag() {
			return tag;
		}

		public void setTag(Object tag) {
			this.tag = tag;
		}

		/**
		 * UI Thread로 데이터 처리 이전의 화면 작업
		 */
		public void preView() {

		}

		/**
		 * 일반 Thread로 데이터 처리
		 * @return
		 * @throws Exception
		 */
		public abstract Object run() throws Exception;

		/**
		 * UI Thread로 데이터 처리 결과에 대한 화면 작업
		 * @param result
		 */
		public void view(CQueryResult result) {

		}
	}

	public static class CQueryResult {
		public static final int	SUCCESS		= 0;
		public static final int	FAIL		= -1;
		public static final int	CANCEL		= -2;

		public int				result		= FAIL;
		public Object			data		= null;
		public String			errorStr	= null;
		public String			errorNo		= null;

		public String toString() {
			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append("result=" + result + "\n");
			sBuilder.append("data=" + data + "\n");
			sBuilder.append("errorStr=" + errorStr + "\n");
			return sBuilder.toString();
		}
	}

	public static final ConcurrentHashMap<Integer, CConnAsyncTask>	runningHashMap	= new ConcurrentHashMap<Integer, CConnAsyncTask>();
	protected CConnectorListener						connectorListener;
	protected BaseActivity								activity;
	protected int													hashCode		= 0;
	protected boolean												isCanceled		= false;

	public CConnAsyncTask() {
		hashCode = this.hashCode();
	}

	public CConnAsyncTask(Context context) {

		hashCode = this.hashCode();
	}

	public CConnAsyncTask(BaseActivity activity) {
		this.activity = activity;
		hashCode = this.hashCode();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (activity != null) {
			runningHashMap.put(hashCode, CConnAsyncTask.this);
		}

			Log.d(TAG, "hashCode=" + hashCode + ", hashSize=" + runningHashMap.size());
	}

	@Override
	protected CQueryResult doInBackground(CConnectorListener... arg0) {
		CQueryResult result = new CQueryResult();
		try {
			connectorListener = arg0[0];

			result.data = connectorListener.run();
			result.result = CQueryResult.SUCCESS;
		} catch (CConnectorCancelException e) {

			result.result = CQueryResult.CANCEL;
			result.errorStr = "데이터수신에 실패하였습니다.";//CMSG.ERR_RESET;
		} catch (Exception e) {
			Log.e(TAG, "doInBackground Exception", e);
			result.result = CQueryResult.FAIL;
			result.errorStr = e.getMessage();
			if (result.errorStr == null || result.errorStr.trim().length() == 0) {
				result.errorStr = "데이터수신에 실패";//CMSG.ERR_MSG_9999;
				Log.e(TAG, "doInBackground", e);
			}
		}
		return result;
	}

	@Override
	protected void onPostExecute(CQueryResult result) {
		runningHashMap.remove(hashCode);

		try {
			if (result.result != CQueryResult.CANCEL && isCanceled == false) {
				connectorListener.view(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onPostExecute(result);
	}

	public void doCancel(boolean mayInterruptIfRunning) {
		isCanceled = true;
		this.cancel(mayInterruptIfRunning);
	}
}
