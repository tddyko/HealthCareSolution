package com.greencross.greencare.network.util;
public class TrCreate {
	
	public static void main(String[] args) {
		
		String ClassName = "Entity";
		String[] tr = new String[]{
				"CateID", "2",
		        "CateName", "농수축산/식음료",
		        "AdminID", "admin",
		        "Regdate", "2016-07-01 14,08,40",
		        "Sort", "2"
		};

		System.out.println("public class "+ClassName+" extends BaseData {");
		for (int i = 0; i < tr.length; i+= 2) {
			System.out.println("\t@SerializedName(\""+tr[i] +"\")");
			System.out.println("\tpublic String "+tr[i] +" = \"\"; // " + tr[i+1]);
		}
		System.out.println("}");
	}
}
