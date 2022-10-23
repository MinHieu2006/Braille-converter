package com.example.brailleconverter;

public class Matching_Request {
    public int Matching(String s){
        int result = 0;
        if(s.contains("TĂNG TỐC ĐỘ")){
            return 1;
        } else if(s.contains("GIẢM TỐC ĐỘ")){
            return 2;
        } else  if(s.contains("DỊCH BÁO")){
            return 3;
        } else if(s.contains("DỊCH TÀI LIỆU")){
            return 4;
        } else if(s.contains("HỌC CHỮ CÁI")){
            return 5;
        } else if(s.contains("QUAY LẠI")){
            return 6;
        }
        return result;
    }
}
