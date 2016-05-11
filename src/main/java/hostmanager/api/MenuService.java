package hostmanager.api;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

import java.util.List;

public interface MenuService {

    @GET("/hosts/list")
    Observable<List<String>> getHosts();

    @GET("/hosts/one/")
    Observable<String> getHost(@Query("name") String name);
}
