package api;

public class ApiClient {

    public final UsersApiClient users =  new UsersApiClient();
    public final AuthApiClient  auth =new AuthApiClient();
    public final ClubsApiClient clubs =  new ClubsApiClient();
}
