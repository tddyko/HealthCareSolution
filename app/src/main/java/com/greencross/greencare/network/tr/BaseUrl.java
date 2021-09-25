package com.greencross.greencare.network.tr;

/**

* "이미지 경로관련- 이미지 경로는 /uploads/ 아래 메뉴별로 존재한다. 
* - 썸네일일경우 /uploads/cache/ 폴더부터 시작한다. 베너     
* banner 브랜드    
* brand_photo 전시회    
* expo_photo 회원     
* member_photo 피드     
* post_photo예) 전시회 이미지를 불러올때     	
* - 리스트이미지 : /uploads/cache/expo_photo/{서버에서 불러온 주소값}
* - 상세 이미지 : /uploads/expo_photo/{서버에서 불러온 주소값}        	
* (참고 : 전시회 상세이미지)http://baby.xdev.co.kr/uploads/expo_photo/2016/06/7467d2371feecd0d6b26723be435b549.png	
* (참고 : 전시회 썸네일이미지)http://baby.xdev.co.kr/uploads/cache/expo_photo/2016/06/7467d2371feecd0d6b26723be435b549.png	 
* * 추가설명 : 모든 이미지는 썸네일 폴더가 존재하는데, 리스트를 보여주는 상황에서는 썸네일을 보여주고 상세페이지 들어갔을때 원본이미지를 보여준다.",
*/
public class BaseUrl {
	
	public static  boolean RELEASE_MODE = false;
//	public static  boolean RELEASE_MODE = true; 

	public static String COMMON_URL;
	public static String API_URL = "";
	public static String FOOD_IMAGE_URL ="";

	static {
		if(RELEASE_MODE){
			COMMON_URL = "http://m.shealthcare.co.kr/SK/WebService/SK_Mobile_Call.asmx/SK_mobile_Call";	//신주소

		} else {
			COMMON_URL = "http://m.shealthcare.co.kr/SK/WebService/SK_Mobile_Call.asmx/SK_mobile_Call";	// 신주소
		}
		FOOD_IMAGE_URL ="http://wkd.walkie.co.kr/HS_HL/UPLOAD/SK_FOOD/";
		
	}
	

}