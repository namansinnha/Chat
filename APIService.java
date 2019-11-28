

public interface APIService {

    @Headers(
            {

                    "Content-Type:application/json",
                    "Authorization:key=AAAArJBqqXI:APA91bGpNEhcl-jjVx1Y0gBTTD8XPpuo3FIQ9VWVu3ImXxZysqRrhFl0EzVFjFDq1dEDZgbSPH-6khWF0GjqC1PuzeIhbFd0tufmDXOG2Mi1KjjT_tbMi401cgNeComdgtlwAp5mZP0Q"

            }
    )

    @POST ("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
