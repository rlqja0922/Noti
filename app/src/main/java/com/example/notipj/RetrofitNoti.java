package com.example.notipj;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
/*
*URL : http://221.139.102.72:25005/bank-notification
* INPUT 부분에 http://221.139.102.72:25005만 입력하게 하고 뒷부분은 코드상에 하드코딩으로 넣어주시면 될 듯 합니다.

METHOD : POST

DATA :
   {
      "title" : "",
      "text" : ""
   }
* 일단은 타이틀과 텍스트만 보내주시면 됩니다!
* 혹시 추가로 필요한 데이터가 있을지 제가 확인해보고 다시 알려드릴게요.

* 서버는 일단 꺼놓은 상태고, 작업 테스트 필요하실 때 켜놓도록 하겠습니다.
* return값 stauts : true, false
* */
public interface RetrofitNoti {
    @POST("/bank-notification") //로그인
    Call<NotificationData> getnotification(@Body NotificationData notificationdata);
}
