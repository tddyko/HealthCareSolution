package com.greencross.greencare.network.tr;

import android.graphics.Bitmap;

public class AdapterData {
	private String imageUrl = "";
	private String field1 = "";
	private String field2 = "";
	private String date = "";
	private String field3 = "";
	private String field4 = "";
	private String viewCount = "";
	private String commantCnt = "";
	private String likeCnt = "";
	private String price = "";
	private Bitmap bitmap;
	
	
	private String post_title;
	
	private String brandName;
	private String postId;
	private String expoId;
	private String brandId;
	
	private String faq_id;
	private String fgr_id;
	
	private String paymentNo;
	private String deliveryNo;
	private String PostDiscount;
	private String PayID;
	private String catename;
	private String TicketID;
	private String status;
	private boolean isBefore;
	
	public String getBrandId() {
		return brandId;
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	private String brdKey;
	private String activeId;
	
	private String Dday;
	
	public AdapterData() {
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getField1() {
		return field1;
	}
	public void setField1(String field1) {
		this.field1 = field1;
	}
	public String getField2() {
		return field2;
	}
	public void setField2(String field2) {
		this.field2 = field2;
	}
	
	public String getField3() {
		return field3;
	}
	public void setField3(String field3) {
		this.field3 = field3;
	}
	
	public String getField4() {
		return field4;
	}
	public void setField4(String field4) {
		this.field4 = field4;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getViewCount() {
		return viewCount;
	}
	public void setViewCount(String viewCount) {
		this.viewCount = viewCount;
	}
	public String getCommantCnt() {
		return commantCnt;
	}
	public void setCommantCnt(String commantCnt) {
		this.commantCnt = commantCnt;
	}
	public String getLikeCnt() {
		return likeCnt;
	}
	public void setLikeCnt(String likeCnt) {
		this.likeCnt = likeCnt;
	}
	public void setPrice(String price) {
		this.price = price;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getPost_title() {
		return post_title;
	}

	public void setPost_title(String post_title) {
		this.post_title = post_title;
	}

	public String getDday() {
		return Dday;
	}

	public void setDday(String dday) {
		Dday = dday;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getExpoId() {
		return expoId;
	}

	public void setExpoId(String expoId) {
		this.expoId = expoId;
	}

	public String getBrdKey() {
		return brdKey;
	}

	public void setBrdKey(String brdKey) {
		this.brdKey = brdKey;
	}

	public String getActiveId() {
		return activeId;
	}

	public void setActiveId(String activeId) {
		this.activeId = activeId;
	}

	public String getFaq_id() {
		return faq_id;
	}

	public void setFaq_id(String faq_id) {
		this.faq_id = faq_id;
	}

	public String getFgr_id() {
		return fgr_id;
	}

	public void setFgr_id(String fgr_id) {
		this.fgr_id = fgr_id;
	}

	public String getPaymentNo() {
		return paymentNo;
	}

	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}

	public String getDeliveryNo() {
		return deliveryNo;
	}

	public void setDeliveryNo(String deliveryNo) {
		this.deliveryNo = deliveryNo;
	}

	public String getPostDiscount() {
		return PostDiscount;
	}

	public void setPostDiscount(String postDiscount) {
		PostDiscount = postDiscount;
	}

	public String getPayID() {
		return PayID;
	}

	public void setPayID(String payID) {
		PayID = payID;
	}

	public String getCatename() {
		return catename;
	}

	public void setCatename(String catename) {
		this.catename = catename;
	}

	public String getTicketID() {
		return TicketID;
	}

	public void setTicketID(String ticketID) {
		TicketID = ticketID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public boolean isBefore() {
		return isBefore;
	}

	public void setBefore(boolean isBefore) {
		this.isBefore = isBefore;
	}
	
}
