package io.javabrains.moviecatalogservice;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.javabrains.moviecatalogservice.models.Rating;
import io.javabrains.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class UserRatingInfo {
    @Autowired
    RestTemplate restTemplate;
    /*
    * 1 : timeout 2s.
    * 2 : số lượng thread check khi có 1 cái bị lỗi hoặc timeout.
    * 3 : % thread lỗi tạo thành error.
    * 4 : thời gian chờ đợi cho đến khi gửi request đến microservice đấy 1 lần nữa.
    */

    @HystrixCommand(
            fallbackMethod = "getFallbackUserRating"
            /*,
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "2000"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "5"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "50"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "5000")
            }*/
        )
    public UserRating getUserRating(String userId) {
        return restTemplate.getForObject("http://rating-data-service/ratingsdata/users/" + userId, UserRating.class);
    }

    public UserRating getFallbackUserRating(String userId) {
        UserRating userRating = new UserRating();
        userRating.setUserId(userId);
        userRating.setUserRating(Arrays.asList( new Rating("0",0)));
        return userRating;
    }
}
