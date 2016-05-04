package hostmanager.api;

import hostmanager.model.Repos;
import retrofit2.http.GET;
import rx.Observable;

import java.util.List;

public interface MenuService {

    //    @GET("/hosts/list")
    @GET("/users/chenDoInG/repos")
    Observable<List<Repos>> getHosts();
}
